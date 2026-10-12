package com.gnrl.noelytra;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraListener implements Listener {

    private final GNRLNoElytra plugin;
    private final ElytraManager manager;

    public ElytraListener(GNRLNoElytra plugin, ElytraManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    // ═══════════ حذف الایترا موقع جوین ═══════════

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> manager.removeElytraFromInventory(player), 20L);
    }

    // ═══════════ حذف الایترا موقع برداشتن توی اینونتوری ═══════════

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> manager.removeElytraFromInventory(player), 1L);
    }

    // ═══════════ حذف الایترا موقع بستن اینونتوری ═══════════

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> manager.removeElytraFromInventory(player), 1L);
    }

    // ═══════════ حذف الایترا موقع برداشتن توی دست ═══════════

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item != null && item.getType() == Material.ELYTRA) {
            plugin.getServer().getScheduler().runTaskLater(plugin,
                    () -> manager.removeElytraFromInventory(player), 1L);
        }
    }

    // ═══════════ حذف الایترا موقع برداشتن از زمین ═══════════

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getItem().getItemStack().getType() == Material.ELYTRA) {
            event.setCancelled(true);
            event.getItem().remove();
            player.sendRichMessage(plugin.getConfig().getString("settings.messages.elytra-removed", ""));
        }
    }

    // ═══════════ حذف الایترا موقع دراپ کردن ═══════════

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.ELYTRA) {
            event.getItemDrop().remove();
        }
    }
}
