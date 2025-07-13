package com.pryzmm.priSKm.vive;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.cjcrafter.vivecraft.VSE;
import com.cjcrafter.vivecraft.VivePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class VRHeight extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(VRHeight.class, Number.class, ExpressionType.PROPERTY,
                "[the] [vr|vivecraft] height of %player%"
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
    protected Number[] get(Event event) {
        Player p = player.getSingle(event);
        if (p != null) {
            VivePlayer vivePlayer = VSE.vivePlayers.get(p.getUniqueId());
            if (vivePlayer == null) return null;
            return new Number[]{vivePlayer.heightScale};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "vr height of " + player.toString(event, debug);
    }
}