package com.pryzmm.priSKm.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

public class EffTest extends Effect {
    static {
        Skript.registerEffect(EffTest.class,
                "test priskm plugin for %world%"
        );
    }

    private Expression<World> world;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        world = (Expression<World>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        World w = world.getSingle(event);
        if (w != null) {
            w.setTime(13000);
            System.out.println("time: " + w.getTime());
            System.out.println("world: " + w.toString());
        } else {
            System.out.println("Error: world given is null!");
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "test priskm plugin for %world%";
    }
}
