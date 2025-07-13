package com.pryzmm.priSKm.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class PlayerEnderPearls extends SimpleExpression<EnderPearl> {

    static {
        Skript.registerExpression(PlayerEnderPearls.class, EnderPearl.class, ExpressionType.PROPERTY,
                "[the] [ender] pearl[s] of %player%",
                "[the] %player%['s] [ender] pearl[s]"
        );
    }

    private Expression<Player> player;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        player = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    protected EnderPearl[] get(Event event) {
        Player p = player.getSingle(event);
        if (p != null) {
            // Get all ender pearls in the world that were thrown by this player
            return p.getWorld().getEntitiesByClass(EnderPearl.class).stream()
                    .filter(pearl -> pearl.getShooter() instanceof Player &&
                            ((Player) pearl.getShooter()).getUniqueId().equals(p.getUniqueId()))
                    .toArray(EnderPearl[]::new);
        }
        return new EnderPearl[0];
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends EnderPearl> getReturnType() {
        return EnderPearl.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "ender pearls of " + player.toString(event, debug);
    }
}