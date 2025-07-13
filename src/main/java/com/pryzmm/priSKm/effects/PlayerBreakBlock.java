package com.pryzmm.priSKm.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class PlayerBreakBlock extends Effect {

    static {
        Skript.registerEffect(PlayerBreakBlock.class,
                "(make|force) %player% [to] break [block] %block%"
        );
    }

    private Expression<Block> block;
    private Expression<Player> player;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        player = (Expression<Player>) exprs[0];
        block = (Expression<Block>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Player p = player.getSingle(event);
        Block b = block.getSingle(event);
        if (b != null && p != null) {
            p.breakBlock(b);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "make " + player.toString(event, debug) + " break " + block.toString(event, debug);
    }
}
