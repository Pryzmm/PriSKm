package com.pryzmm.priSKm.vive;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.cjcrafter.vivecraft.VSE;
import com.cjcrafter.vivecraft.VivePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class HandPosition extends SimpleExpression<Location> {

    static {
        Skript.registerExpression(HandPosition.class, Location.class, ExpressionType.COMBINED,
                "[the] [vr|vivecraft] (0¦left hand|1¦right hand|2¦head) position of %player%"
        );
    }

    private Expression<Player> player;
    private int hand;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        player = (Expression<Player>) exprs[0];
        hand = parseResult.mark;
        return true;
    }

    @Override
    protected Location[] get(Event event) {
        Player p = player.getSingle(event);
        if (p != null) {
            VivePlayer vivePlayer = VSE.vivePlayers.get(p.getUniqueId());
            if (vivePlayer == null) return null;

            Location pos;
            switch (hand) {
                case 0: // left hand
                    pos = vivePlayer.getControllerPos(1);
                    break;
                case 1: // right hand
                    pos = vivePlayer.getControllerPos(0);
                    break;
                case 2: // head
                    pos = vivePlayer.getHMDPos();
                    break;
                default:
                    return null;
            }

            return pos != null ? new Location[]{pos} : null;
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String handType = hand == 0 ? "left hand" : (hand == 1 ? "right hand" : "head");
        return "the " + handType + " position of " + player.toString(event, debug);
    }
}