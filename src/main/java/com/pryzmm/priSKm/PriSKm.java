package com.pryzmm.priSKm;

import org.bukkit.plugin.java.JavaPlugin;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;

import java.io.IOException;

public class PriSKm extends JavaPlugin {

    private static PriSKm instance;
    private SkriptAddon addon;

    @Override
    public void onEnable() {
        instance = this;

        // Check if Skript is installed and enabled
        if (getServer().getPluginManager().getPlugin("Skript") == null) {
            getLogger().severe("Skript is not installed! This plugin requires Skript to function.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register this plugin as a Skript addon
        addon = Skript.registerAddon(this);

        try {
            // Register your custom syntax elements
            registerSyntax();
            getLogger().info("Successfully loaded " + getDescription().getName() + " v" + getDescription().getVersion());
        } catch (Exception e) {
            getLogger().severe("Failed to load addon: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerSyntax() throws IOException {
        addon.loadClasses("com.pryzmm.priSKm.effects");
        addon.loadClasses("com.pryzmm.priSKm.events");
    }

    @Override
    public void onDisable() {
        instance = null;
        addon = null;
    }

    public static PriSKm getInstance() {
        return instance;
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }
}