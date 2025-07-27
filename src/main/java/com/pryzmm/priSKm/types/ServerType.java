package com.pryzmm.priSKm.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerType {

    static {
        Classes.registerClass(new ClassInfo<>(Server.class, "server")
                .parser(new Parser<Server>() {
                    private final Pattern SERVER_PATTERN = Pattern.compile("^([a-zA-Z0-9.-]+)(?::([0-9]{1,5}))?$");

                    @Override
                    @Nullable
                    public Server parse(@NotNull String input, @NotNull ParseContext context) {
                        if (input.trim().isEmpty()) {
                            return null;
                        }

                        String trimmed = input.trim();

                        var matcher = SERVER_PATTERN.matcher(trimmed);
                        if (!matcher.matches()) {
                            return null;
                        }

                        String address = matcher.group(1);

                        boolean isValidAddress = address.contains(".") ||
                                address.equalsIgnoreCase("localhost") ||
                                address.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");

                        if (!isValidAddress) {
                            return null;
                        }

                        if (address.contains(".") && !address.equalsIgnoreCase("localhost")) {
                            String[] parts = address.split("\\.");
                            if (parts.length < 2) {
                                return null;
                            }
                            String tld = parts[parts.length - 1];
                            if (tld.length() < 2 || !tld.matches("[a-zA-Z]+")) {
                                return null;
                            }
                        }

                        int port = 25565;

                        if (matcher.group(2) != null) {
                            try {
                                port = Integer.parseInt(matcher.group(2));
                                if (port < 1 || port > 65535) {
                                    return null;
                                }
                            } catch (NumberFormatException e) {
                                return null;
                            }
                        }

                        return new Server(address, port);
                    }

                    @Override
                    @NotNull
                    public String toString(@NotNull Server server, int flags) {
                        return server.toString();
                    }

                    @Override
                    @NotNull
                    public String toVariableNameString(@NotNull Server server) {
                        return server.getAddress().replace(".", "_") + "_" + server.getPort();
                    }
                })
                .serializer(new Serializer<Server>() {
                    @Override
                    @NotNull
                    public Fields serialize(@NotNull Server server) {
                        Fields fields = new Fields();
                        fields.putObject("address", server.getAddress());
                        fields.putPrimitive("port", server.getPort());
                        return fields;
                    }

                    @Override
                    public void deserialize(@NotNull Server server, @NotNull Fields fields) {}

                    @Override
                    @NotNull
                    public Server deserialize(@NotNull Fields fields) throws StreamCorruptedException {
                        String address = fields.getObject("address", String.class);
                        int port = fields.getPrimitive("port", int.class);
                        return new Server(address, port);
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return false;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                }));
    }

    private static final Map<String, Server> SERVER_CACHE = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService BACKGROUND_PINGER =
            Executors.newScheduledThreadPool(2, r -> {
                Thread t = new Thread(r, "ServerPing");
                t.setDaemon(true);
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            });

    public static class Server {
        private final String address;
        private final int port;

        private volatile boolean online = false;
        private volatile int playerCount = 0;
        private volatile int maxPlayers = 0;
        private volatile String motd = null;
        private volatile String version = null;
        private volatile int ping = -1;
        private volatile long lastPing = 0;
        private volatile long lastPingAttempt = 0;

        private static final int CONNECTION_TIMEOUT = 2000;
        private static final int READ_TIMEOUT = 1500;
        private static final long PING_COOLDOWN = 5000;

        public Server(String address, int port) {
            this.address = address;
            this.port = port;
            String key = address + ":" + port;
        }

        public void startBackgroundPinging() {
            scheduleBackgroundPing();
        }

        public Server(String address) {
            this(address, 25565);
        }

        public String getAddress() { return address; }
        public int getPort() { return port; }
        public boolean isOnline() { return online; }
        public int getPlayerCount() { return playerCount; }
        public int getMaxPlayers() { return maxPlayers; }
        public String getMotd() { return motd; }
        public String getVersion() { return version; }
        public int getPing() { return ping; }

        public boolean isDataFresh(long maxAgeMs) {
            return (System.currentTimeMillis() - lastPing) >= maxAgeMs;
        }

        public void requestPing() {
            long now = System.currentTimeMillis();
            if (now - lastPingAttempt > PING_COOLDOWN) {
                lastPingAttempt = now;
                BACKGROUND_PINGER.submit(this::performPingInBackground);
            }
        }

        private void scheduleBackgroundPing() {
            BACKGROUND_PINGER.schedule(this::performPingInBackground, 0, TimeUnit.SECONDS);

            BACKGROUND_PINGER.scheduleAtFixedRate(
                    this::performPingInBackground, 30, 30, TimeUnit.SECONDS
            );
        }

        private void performPingInBackground() {
            String threadName = Thread.currentThread().getName();

            long startTime = System.currentTimeMillis();

            try {
                InetAddress inetAddress = InetAddress.getByName(address);

                try (Socket socket = new Socket()) {
                    socket.setTcpNoDelay(true);
                    socket.setSoTimeout(READ_TIMEOUT);
                    socket.connect(new InetSocketAddress(inetAddress, port), CONNECTION_TIMEOUT);
                    doMinecraftPing(socket);
                    this.ping = (int) (System.currentTimeMillis() - startTime);
                    this.online = true;
                    this.lastPing = System.currentTimeMillis();


                } catch (SocketTimeoutException e) {
                    setOfflineState("Timeout");
                } catch (ConnectException e) {
                    setOfflineState("Connection refused");
                } catch (IOException e) {
                    setOfflineState("IO Error");
                }

            } catch (UnknownHostException e) {
                setOfflineState("Unknown host");
            } catch (Exception e) {
                setOfflineState("Error: " + e.getClass().getSimpleName());
            }
        }

        private void setOfflineState(String reason) {
            this.online = false;
            this.ping = -1;
            this.playerCount = 0;
            this.maxPlayers = 0;
            this.motd = reason;
            this.version = "";
            this.lastPing = System.currentTimeMillis();
        }

        private void doMinecraftPing(Socket socket) throws IOException {
            var out = socket.getOutputStream();
            var in = socket.getInputStream();

            byte[] handshake = createHandshakePacket();
            writeVarInt(out, handshake.length);
            out.write(handshake);
            out.flush();

            writeVarInt(out, 1);
            out.write(0x00);
            out.flush();
            try {
                int responseLength = readVarInt(in);
                if (responseLength > 0 && responseLength < 32768) {
                    int packetId = readVarInt(in);
                    if (packetId == 0x00) {
                        String json = readString(in);
                        parseMinecraftJson(json);
                    }
                }
            } catch (IOException e) {
                this.online = true;
                this.motd = null;
            }
        }

        private byte[] createHandshakePacket() throws IOException {
            var packet = new java.io.ByteArrayOutputStream();

            writeVarInt(packet, 0x00);
            writeVarInt(packet, 767);
            writeString(packet, address);
            packet.write((port >> 8) & 0xFF);
            packet.write(port & 0xFF);
            writeVarInt(packet, 1);

            return packet.toByteArray();
        }

        private void parseMinecraftJson(String json) {
            try {
                this.playerCount = 0;
                this.maxPlayers = 0;
                this.motd = "";
                this.version = "";

                if (json != null && !json.isEmpty()) {
                    java.util.regex.Matcher m;

                    m = Pattern.compile("\"online\"\\s*:\\s*(\\d+)").matcher(json);
                    if (m.find()) this.playerCount = Integer.parseInt(m.group(1));

                    m = Pattern.compile("\"max\"\\s*:\\s*(\\d+)").matcher(json);
                    if (m.find()) this.maxPlayers = Integer.parseInt(m.group(1));

                    m = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
                    if (m.find()) this.version = m.group(1);

                    m = Pattern.compile("\"description\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
                    if (m.find()) {
                        this.motd = m.group(1).replaceAll("§[0-9a-fk-or]", "");
                    } else {
                        m = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
                        if (m.find()) {
                            this.motd = m.group(1).replaceAll("§[0-9a-fk-or]", "");
                        }
                    }
                }

            } catch (Exception ignored) {}
        }

        private void writeVarInt(java.io.OutputStream out, int value) throws IOException {
            while ((value & 0xFFFFFF80) != 0) {
                out.write((value & 0x7F) | 0x80);
                value >>>= 7;
            }
            out.write(value & 0x7F);
        }

        private void writeVarInt(java.io.ByteArrayOutputStream out, int value) {
            while ((value & 0xFFFFFF80) != 0) {
                out.write((value & 0x7F) | 0x80);
                value >>>= 7;
            }
            out.write(value & 0x7F);
        }

        private void writeString(java.io.ByteArrayOutputStream out, String str) throws IOException {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            writeVarInt(out, bytes.length);
            out.write(bytes);
        }

        private int readVarInt(java.io.InputStream in) throws IOException {
            int value = 0;
            int position = 0;

            while (true) {
                int b = in.read();
                if (b == -1) throw new IOException("End of stream");

                value |= (b & 0x7F) << position;
                if ((b & 0x80) == 0) break;

                position += 7;
                if (position >= 32) throw new IOException("VarInt too big");
            }
            return value;
        }

        private String readString(java.io.InputStream in) throws IOException {
            int length = readVarInt(in);
            if (length > 32767) throw new IOException("String too long");

            byte[] bytes = new byte[length];
            int read = 0;
            while (read < length) {
                int n = in.read(bytes, read, length - read);
                if (n == -1) throw new IOException("End of stream");
                read += n;
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public String toString() {
            return address + ":" + port;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Server other)) return false;
            return address.equals(other.address) && port == other.port;
        }

        @Override
        public int hashCode() {
            return address.hashCode() * 31 + port;
        }
    }

    public static Server getOrCreateServer(String address, int port) {
        String key = address + ":" + port;
        Server server = SERVER_CACHE.get(key);

        if (server == null) {
            Bukkit.getLogger().info("Starting ping for " + key);
            server = new Server(address, port);

            Server existing = SERVER_CACHE.putIfAbsent(key, server);
            if (existing != null) {
                return existing;
            }

            server.startBackgroundPinging();
        }

        return server;
    }
}