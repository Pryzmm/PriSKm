package com.pryzmm.priSKm.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class CanBreakBlock extends Condition {

    static {
        Skript.registerCondition(CanBreakBlock.class,
                "%itemstack% can break %block%",
                "%itemstack% can( not|[']t) break %block%"
        );
    }

    private Expression<ItemStack> item;
    private Expression<Block> block;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        item = (Expression<ItemStack>) exprs[0];
        block = (Expression<Block>) exprs[1];
        // matchedPattern 0 = "can break block", 1 = "can not break block"
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        ItemStack i = item.getSingle(event);
        Block b = block.getSingle(event);
        if (i == null || b == null) {
            return false;
        }
        System.out.println(b.getDrops(i));

        return !b.getDrops(i).isEmpty();
    }

    @Override
    public String toString(Event event, boolean debug) {
        return item.toString(event, debug) + (isNegated() ? " can not break block" : " can break block");
    }
}