package me.jinou.mysqlmap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.Collections;
import java.util.List;

import static org.bukkit.event.inventory.InventoryType.CARTOGRAPHY;

public class MapListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerInventory playerInventory = event.getPlayer().getInventory();
        int invSize = playerInventory.getSize();
        for (int idx = 0; idx < invSize; idx++) {
            ItemStack itemStack = playerInventory.getItem(idx);
            if (itemStack == null || itemStack.getType() != Material.FILLED_MAP) {
                continue;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                continue;
            }

            List<String> loreList = itemMeta.getLore();
            if (loreList == null || loreList.size() == 0) {
                continue;
            }

            String syncKey = "TODO";
            for (String lore : loreList) {
                if (!lore.contains(syncKey)) {
                    continue;
                }
                // TODO: sync map
                event.getPlayer().sendMessage("sync map in slot " + String.valueOf(idx));
            }

            MapView mapView = ((MapMeta) itemMeta).getMapView();
            if (mapView == null) {
                continue;
            }

            int mapViewId = mapView.getId();
            event.getPlayer().sendMessage("you have map id: " + String.valueOf(mapViewId));
        }
    }

    @EventHandler
    public void onMapCraft(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (event.getInventory().getType() != CARTOGRAPHY ||
                event.getSlot() != 2 ||
                event.getCurrentItem() == null ||
                !event.getCurrentItem().getType().equals(Material.FILLED_MAP)) {
            return;
        }

        ItemStack result = inventory.getItem(2);
        if (getMapView(result) == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack item0 = player.getOpenInventory().getItem(0);
        ItemStack item1 = player.getOpenInventory().getItem(1);
        if (item0 == null || item1 == null) {
            return;
        }

        MapMeta mapMeta = (MapMeta) result.getItemMeta();
        if (mapMeta == null) {
            return;
        }

        String keyLore = "String from Config " + player.getName();
        List<String> loreList = mapMeta.getLore();

        if (item0.getType().equals(Material.FILLED_MAP) && item1.getType().equals(Material.GLASS_PANE)) {
            if (loreList == null) {
                mapMeta.setLore(Collections.singletonList(keyLore));
            } else {
                loreList.add(keyLore);
            }
            result.setItemMeta(mapMeta);
            return;

        } else if (item0.getType().equals(Material.FILLED_MAP) && item1.getType().equals(Material.MAP)) {
            if (loreList == null || loreList.size() == 0) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);
            for (String lore : loreList) {
                if (lore.contains(keyLore)) {
                    event.setCancelled(false);
                    break;
                }
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
