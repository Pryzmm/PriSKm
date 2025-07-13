package com.pryzmm.priSKm.vive;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.cjcrafter.vivecraft.VSE;
import com.cjcrafter.vivecraft.VivePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class IsInVR extends Condition {

    static {
        Skript.registerCondition(IsInVR.class,
                "%player% is [in[side]] (vr|virtual reality|vive)",
                "%player% is not [in[side]] (vr|virtual reality|vive)"
        );
    }

    private Expression<Player> player;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        player = (Expression<Player>) exprs[0];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        Player p = player.getSingle(event);
        if (p == null) {
            return false;
        }
        VivePlayer vivePlayer = VSE.vivePlayers.get(p.getUniqueId());
        boolean isInVR = vivePlayer != null;
        return isNegated() != isInVR;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return player.toString(event, debug) + (isNegated() ? " is not in vr" : " is in vr");
    }
}