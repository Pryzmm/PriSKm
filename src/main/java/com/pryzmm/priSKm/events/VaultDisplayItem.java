package com.pryzmm.priSKm.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.VaultDisplayItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class VaultDisplayItem extends SkriptEvent {

    static {
        Skript.registerEvent("Vault Display Item", VaultDisplayItem.class, VaultDisplayItemEvent.class,
                "[on] vault display[ing] item",
                "[on] display[ing] vault item"
        );

        EventValues.registerEventValue(VaultDisplayItemEvent.class, ItemStack.class, new Getter<>() {
            @Override
            public ItemStack get(VaultDisplayItemEvent event) {
                return event.getDisplayItem();
            }
        }, 0);

        EventValues.registerEventValue(VaultDisplayItemEvent.class, Block.class, new Getter<>() {
            @Override
            public Block get(VaultDisplayItemEvent event) {
                return event.getBlock();
            }
        }, 0);

        EventValues.registerEventValue(VaultDisplayItemEvent.class, Location.class, new Getter<>() {
            @Override
            public Location get(VaultDisplayItemEvent event) {
                return event.getBlock().getLocation();
            }
        }, 0);

        EventValues.registerEventValue(VaultDisplayItemEvent.class, World.class, new Getter<>() {
            @Override
            public World get(VaultDisplayItemEvent event) {
                return event.getBlock().getWorld();
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event event) {
        return event instanceof VaultDisplayItemEvent;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "vault display item event";
    }
}