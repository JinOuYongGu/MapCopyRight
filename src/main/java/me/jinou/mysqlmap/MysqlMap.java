package me.jinou.mysqlmap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MysqlMap extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new MapListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
