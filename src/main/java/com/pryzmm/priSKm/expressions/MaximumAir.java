package com.pryzmm.priSKm.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.Objects;

public class MaximumAir extends SimpleExpression<Integer> {

    static {
        Skript.registerExpression(MaximumAir.class, Integer.class, ExpressionType.PROPERTY,
                "[the] max[imum] air of %livingentity%",
                "[the] %livingentity%['s] max[imum] air"
        );
    }

    private Expression<LivingEntity> entity;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        entity = (Expression<LivingEntity>) exprs[0];
        return true;
    }

    @Override
    protected Integer[] get(Event event) {
        LivingEntity e = entity.getSingle(event);
        if (e != null) {
            return new Integer[]{e.getMaximumAir()};
        }
        return null;
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (Objects.requireNonNull(mode) == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Integer.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @org.jetbrains.annotations.Nullable Object[] delta, Changer.ChangeMode mode) {
        LivingEntity e = entity.getSingle(event);
        if (e == null) {
            return;
        }

        if (Objects.requireNonNull(mode) == Changer.ChangeMode.SET) {
            if (delta != null && delta[0] instanceof Integer) {
                e.setMaximumAir((Integer) delta[0]);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "maximum air of " + entity.toString(event, debug);
    }
}