// priSKm/PriSKm.java
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

        addon = Skript.registerAddon(this);

        try {
            registerSyntax();
            getLogger().info("Successfully loaded PriSKm " + getDescription().getVersion());
        } catch (Exception e) {
            getLogger().severe("Failed to load PriSKm: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerSyntax() throws IOException {
        addon.loadClasses("com.pryzmm.priSKm.conditions");
        addon.loadClasses("com.pryzmm.priSKm.expressions");
        addon.loadClasses("com.pryzmm.priSKm.events");
        addon.loadClasses("com.pryzmm.priSKm.types");
        addon.loadClasses("com.pryzmm.priSKm.effects");
        if (getServer().getPluginManager().getPlugin("VivecraftSpigot") != null) {
            getLogger().info("Found VivecraftSpigot, loading VR syntaxes...");
            addon.loadClasses("com.pryzmm.priSKm.vive");
        } else {
            getLogger().warning("Could not find VivecraftSpigot as an addon, cannot register syntaxes relating to VR support.");
        }
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