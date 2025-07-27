package com.pryzmm.priSKm.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pryzmm.priSKm.types.ServerType.Server;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.pryzmm.priSKm.types.ServerType.getOrCreateServer;

public class ServerExpr extends SimpleExpression<Server> {
    private Expression<String> address;

    static {
        Skript.registerExpression(ServerExpr.class, Server.class,
                ExpressionType.SIMPLE, "server %string%");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern,
                        Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        address = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected Server[] get(Event event) {
        String addr = address.getSingle(event);
        if (addr == null) return new Server[0];

        String[] parts = addr.split(":");
        String host = parts[0];
        int port = 25565;

        if (parts.length > 1) {
            try {
                port = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return new Server[0];
            }
        }

        Server server = getOrCreateServer(host, port);
        return new Server[]{server};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @NotNull
    public Class<? extends Server> getReturnType() {
        return Server.class;
    }

    @Override
    @NotNull
    public String toString(@Nullable Event event, boolean debug) {
        return "server " + address.toString(event, debug);
    }
}