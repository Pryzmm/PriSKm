package com.pryzmm.priSKm.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.jetbrains.annotations.Nullable;

public class ThunderState extends SkriptEvent {

    static {
        Skript.registerEvent("Thunder State Change", ThunderState.class, ThunderChangeEvent.class,
                "[on] thunder [state] change",
                "[on] change [state [of]] thunder"
        );

        EventValues.registerEventValue(ThunderChangeEvent.class, World.class, new Getter<>() {
            @Override
            public World get(ThunderChangeEvent event) {
                return event.getWorld();
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event event) {
        return event instanceof ThunderChangeEvent;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "thunder state change event";
    }
}