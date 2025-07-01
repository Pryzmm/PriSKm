package com.pryzmm.priSKm.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptEvent;
import org.bukkit.event.Event;
import org.bukkit.event.block.VaultDisplayItemEvent;

public class VaultDisplayItemEvent extends SkriptEvent {

    static {
        Skript.registerEvent("Vault Display Item", VaultDisplayItem.class, VaultDisplayItemEvent.class,
                "[on] player jump[ing]"
        );
    }

}
