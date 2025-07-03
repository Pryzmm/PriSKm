package com.pryzmm.priSKm.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;

public class IsBlockClipping extends Condition {

    static {
        Skript.registerCondition(IsBlockClipping.class,
                "%player% is (block clipping|clipping through blocks)",
                "%player% is not (block clipping|clipping through blocks)"
        );
    }

    private Expression<Player> player;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        player = (Expression<Player>) exprs[0];
        // matchedPattern 0 = "is block clipping", 1 = "is not block clipping"
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        Player p = player.getSingle(event);
        if (p == null) {
            return false;
        }

        return isPlayerBlockClipping(p);
    }

    private boolean isPlayerBlockClipping(Player player) {
        BoundingBox playerBB = player.getBoundingBox();

        int minX = (int) Math.floor(playerBB.getMinX());
        int maxX = (int) Math.ceil(playerBB.getMaxX());
        int minY = (int) Math.floor(playerBB.getMinY());
        int maxY = (int) Math.ceil(playerBB.getMaxY());
        int minZ = (int) Math.floor(playerBB.getMinZ());
        int maxZ = (int) Math.ceil(playerBB.getMaxZ());

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    if (!block.getType().isSolid()) {
                        continue;
                    }
                    BoundingBox blockBB = block.getBoundingBox();
                    if (playerBB.overlaps(blockBB)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return player.toString(event, debug) + (isNegated() ? " is not block clipping" : " is block clipping");
    }
}