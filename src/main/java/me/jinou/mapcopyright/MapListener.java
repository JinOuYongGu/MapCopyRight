package me.jinou.mapcopyright;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.bukkit.event.inventory.InventoryType.CARTOGRAPHY;
import static org.bukkit.event.inventory.InventoryType.WORKBENCH;

/**
 * @author Jin_ou
 */
public class MapListener implements Listener {
    @NonNull
    private static final MapCopyRight PLUGIN = MapCopyRight.getInstance();
    @NonNull
    private static final FileConfiguration CONFIG = PLUGIN.getConfig();

    @EventHandler
    public void onMapCraft(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        boolean clickCartographyOutput = inventory.getType() == CARTOGRAPHY && event.getSlot() == 2;
        boolean clickWorkBenchOutput = inventory.getType() == WORKBENCH && event.getSlot() == 0;
        if (!clickCartographyOutput && !clickWorkBenchOutput) {
            return;
        }
        if (event.getCurrentItem() == null ||
                !event.getCurrentItem().getType().equals(Material.FILLED_MAP)) {
            return;
        }

        ItemStack result = inventory.getItem(event.getSlot());
        MapView resultView = getMapView(result);
        if (resultView == null) {
            return;
        }

        MapMeta mapMeta = (MapMeta) result.getItemMeta();
        if (mapMeta == null) {
            return;
        }

        String keyLore = CONFIG.getString("keyLore");
        if (keyLore == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        keyLore = keyLore.replace("{player}", player.getName()).replace("{mapId}", "1");
        List<String> keyLoreLists = Arrays.asList(keyLore.split("\n"));
        List<String> mapLores = null;
        if (mapMeta.hasLore()) {
            mapLores = mapMeta.getLore();
        }

        // When lock map
        if (result.getAmount() == 1) {
            if (mapLores == null) {
                mapLores = keyLoreLists;
            } else {
                mapLores.addAll(keyLoreLists);
            }
            mapMeta.setLore(mapLores);
            result.setItemMeta(mapMeta);
            return;
        }
        // When copy map
        else if (result.getAmount() > 1) {
            if (!resultView.isLocked()) {
                player.sendMessage(CONFIG.getString("map-not-locked"));
                event.setCancelled(true);
                player.closeInventory();
                return;
            }

            if (mapLores == null || mapLores.size() == 0) {
                return;
            }

            if (Collections.indexOfSubList(mapLores, keyLoreLists) == -1) {
                player.sendMessage(CONFIG.getString("map-no-copyright"));
                event.setCancelled(true);
                player.closeInventory();
            }
        }
    }

    private MapView getMapView(ItemStack map) {
        if (map == null || map.getType() != Material.FILLED_MAP) {
            return null;
        }

        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        if (mapMeta == null) {
            return null;
        }

        return mapMeta.getMapView();
    }
}
