package com.pryzmm.priSKm.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ThunderStateExpr extends SimpleExpression<Boolean> {

    // The expression that will hold the world
    private Expression<World> world;

    static {
        // Register the expression with Skript
        Skript.registerExpression(ThunderStateExpr.class, Boolean.class, ExpressionType.PROPERTY,
                "[the] thunder state of %world%",
                "%world%'s thunder state"
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        // Cast the first expression to a World expression
        world = (Expression<World>) exprs[0];
        return true;
    }

    @Override
    protected Boolean[] get(Event event) {
        World w = world.getSingle(event);
        if (w != null) {
            return new Boolean[]{w.isThundering()};
        }
        return new Boolean[0];
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
        return "thunder state of " + world.toString(event, debug);
    }

    // This method defines what change modes are supported
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (Objects.requireNonNull(mode) == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }

    // This method handles the actual changing of values
    @Override
    public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) {
        World w = world.getSingle(event);
        if (w == null) {
            return;
        }

        if (Objects.requireNonNull(mode) == Changer.ChangeMode.SET) {
            if (delta != null && delta[0] instanceof Boolean) {
                w.setThundering((Boolean) delta[0]);
            }
        }
    }
}