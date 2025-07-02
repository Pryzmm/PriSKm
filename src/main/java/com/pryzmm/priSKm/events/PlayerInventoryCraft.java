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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.VaultDisplayItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PlayerInventoryCraft extends SkriptEvent {

    static {
        Skript.registerEvent("Player Inventory Craft", PlayerInventoryCraft.class, CraftItemEvent.class,
                "[on] craft[ing] in player[[']s] inv[entory]"
        );

        EventValues.registerEventValue(CraftItemEvent.class, ItemStack.class, new Getter<>() {
            @Override
            public ItemStack get(CraftItemEvent event) {
                return event.getCurrentItem();
            }
        }, 0);

        EventValues.registerEventValue(CraftItemEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(CraftItemEvent event) {
                System.out.println(event.getInventory().getHolder());
                return (Player) event.getInventory().getHolder();
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event event) {
        CraftItemEvent craftEvent = (CraftItemEvent) event;
        return craftEvent.getInventory().getType() == InventoryType.CRAFTING; // misleading, don't know why InventoryType.PLAYER isn't what needs to be used
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "player inventory craft event";
    }
}