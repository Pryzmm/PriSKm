package com.pryzmm.priSKm.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pryzmm.priSKm.types.DripstoneThicknessType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class DripstoneThickness extends SimpleExpression<DripstoneThicknessType> {

    static {
        Skript.registerExpression(DripstoneThickness.class, DripstoneThicknessType.class, ExpressionType.PROPERTY,
                "[the] [dripstone] thickness of %block%",
                "%block%['s] [dripstone] thickness"
        );
    }

    private Expression<Block> block;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        block = (Expression<Block>) exprs[0];
        return true;
    }

    @Override
    protected DripstoneThicknessType[] get(Event event) {
        Block b = block.getSingle(event);
        if (b != null && b.getType() == Material.POINTED_DRIPSTONE) {
            PointedDripstone dripstone = (PointedDripstone) b.getBlockData();
            return new DripstoneThicknessType[]{new DripstoneThicknessType(dripstone.getThickness())};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends DripstoneThicknessType> getReturnType() {
        return DripstoneThicknessType.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "dripstone thickness of " + block.toString(event, debug);
    }
}