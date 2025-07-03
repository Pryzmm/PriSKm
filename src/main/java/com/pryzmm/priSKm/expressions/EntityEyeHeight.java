package com.pryzmm.priSKm.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EntityEyeHeight extends SimpleExpression<Double> {

    static {
        Skript.registerExpression(EntityEyeHeight.class, Double.class, ExpressionType.PROPERTY,
                "[the] eye height of %entity%",
                "[the] %entity%['s] eye height"
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
    protected Double[] get(Event event) {
        LivingEntity e = entity.getSingle(event);
        if (e != null) {
            return new Double[]{e.getEyeHeight()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Double> getReturnType() {
        return Double.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "eye height of " + entity.toString(event, debug);
    }
}