package me.jinou.mapcopyright;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Jin_ou
 */
public final class MapCopyRight extends JavaPlugin {
    @Getter
    private static FileConfiguration fileConfig = null;
    @Getter
    private static MapCopyRight instance = null;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        fileConfig = getConfig();

        Bukkit.getPluginManager().registerEvents(new MapListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
