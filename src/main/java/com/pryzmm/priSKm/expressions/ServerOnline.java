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

public class ServerOnline extends SimpleExpression<Boolean> {
    private Expression<Server> server;

    static {
        Skript.registerExpression(ServerOnline.class, Boolean.class,
                ExpressionType.PROPERTY, "online [status] of %server%", "%server%['s] online [status]");
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
    protected Boolean[] get(Event event) {
        Server srv = server.getSingle(event);
        if (srv == null) return new Boolean[0];

        if (!srv.isDataFresh(10000)) {
            srv.requestPing();
        }

        boolean result = srv.isOnline();
        return new Boolean[]{result};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @NotNull
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    @NotNull
    public String toString(@Nullable Event event, boolean debug) {
        return "online status of " + server.toString(event, debug);
    }
}