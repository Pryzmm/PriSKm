package com.pryzmm.priSKm.vive;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.cjcrafter.vivecraft.VSE;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class IsSeated extends Condition {

    static {
        Skript.registerCondition(IsSeated.class,
                "%player% is seated in[side] (vr|virtual reality|vive)",
                "%player% is not seated in[side] (vr|virtual reality|vive)"
        );
    }

    private Expression<Player> player;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        player = (Expression<Player>) exprs[0];
        // matchedPattern 0 = "is seated", 1 = "is not seated"
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        Player p = player.getSingle(event);
        if (p == null) {
            return false;
        }
        return isNegated() != VSE.isSeated(p);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return player.toString(event, debug) + (isNegated() ? " is seated in vr" : " is not seated in vr");
    }
}