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

public class ServerPingTime extends SimpleExpression<Number> {
    private Expression<Server> server;

    static {
        Skript.registerExpression(ServerPingTime.class, Number.class,
                ExpressionType.PROPERTY, "ping [time] of %server%", "%server%['s] ping [time]");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern,
                        Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        server = (Expression<Server>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected Number[] get(Event event) {
        Server srv = server.getSingle(event);
        if (srv == null) return new Number[0];

        if (!srv.isDataFresh(10000)) {
            srv.requestPing();
        }

        Number result = srv.getPing();
        return new Number[]{result};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @NotNull
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    @NotNull
    public String toString(@Nullable Event event, boolean debug) {
        return "ping time of " + server.toString(event, debug);
    }
}