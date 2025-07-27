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

public class ServerMOTD extends SimpleExpression<String> {
    private Expression<Server> server;

    static {
        Skript.registerExpression(ServerMOTD.class, String.class,
                ExpressionType.PROPERTY, "motd of %server%", "%server%['s] motd");
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
    protected String[] get(Event event) {
        Server srv = server.getSingle(event);
        if (srv == null) return new String[0];

        if (!srv.isDataFresh(10000)) {
            srv.requestPing();
        }

        String result = srv.getMotd();
        return result != null ? new String[]{result} : new String[0];
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    @NotNull
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    @NotNull
    public String toString(@Nullable Event event, boolean debug) {
        return "motd of " + server.toString(event, debug);
    }
}