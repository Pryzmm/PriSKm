package com.pryzmm.priSKm.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pryzmm.priSKm.types.ServerType.Server;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.pryzmm.priSKm.types.ServerType.getOrCreateServer;

public class ServerPing extends Effect {
    private Expression<String> address;

    static {
        Skript.registerEffect(ServerPing.class, "ping server %string%");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern,
                        Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        address = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        String addr = address.getSingle(event);
        if (addr == null) return;

        String[] parts = addr.split(":");
        String host = parts[0];
        int port = 25565;

        if (parts.length > 1) {
            try {
                port = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return;
            }
        }

        Server server = getOrCreateServer(host, port);
        server.requestPing();
    }

    @Override
    @NotNull
    public String toString(@Nullable Event event, boolean debug) {
        return "ping server " + address.toString(event, debug);
    }
}