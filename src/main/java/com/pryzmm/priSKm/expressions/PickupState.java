package com.pryzmm.priSKm.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.Objects;

public class PickupState extends SimpleExpression<Boolean> {

    static {
        Skript.registerExpression(PickupState.class, Boolean.class, ExpressionType.PROPERTY,
                "[the] [item] pick[ ]up state of %player%",
                "[the] %player%['s] [item] pick[ ]up state"
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
    protected Boolean[] get(Event event) {
        LivingEntity p = player.getSingle(event);
        if (p != null) {
            return new Boolean[]{p.getCanPickupItems()};
        }
        return null;
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (Objects.requireNonNull(mode) == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }


    @Override
    public void change(Event event, @org.jetbrains.annotations.Nullable Object[] delta, Changer.ChangeMode mode) {
        LivingEntity p = player.getSingle(event);
        if (p == null) {
            return;
        }

        if (Objects.requireNonNull(mode) == Changer.ChangeMode.SET) {
            if (delta != null && delta[0] instanceof Boolean) {
                p.setCanPickupItems((Boolean) delta[0]);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "pickup state of " + player.toString(event, debug);
    }
}