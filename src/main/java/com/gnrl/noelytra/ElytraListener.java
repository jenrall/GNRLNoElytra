package com.gnrl.noelytra;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
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

        // تاخیر ۱ تیک بذار تا اینونتوری آپدیت شه، بعد چک کن
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
            // تاخیر ۱ تیک برای اطمینان
            plugin.getServer().getScheduler().runTaskLater(plugin,
                    () -> manager.removeElytraFromInventory(player), 1L);
        }
    }

    // ═══════════ حذف الایترا موقع دراپ کردن ═══════════

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.ELYTRA) {
            event.getItemDrop().remove();
        }
    }

    // ═══════════ حذف الایترا موقع برداشتن از زمین ═══════════

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack().getType() == Material.ELYTRA) {
            // اگه روی بدنه الایترا داره، اجازه نده برداره
            Player player = event.getPlayer();
            ItemStack chestplate = player.getInventory().getChestplate();
            boolean hasElytraOnBody = chestplate != null && chestplate.getType() == Material.ELYTRA;

            // اگه روی بدنش الایترا داره، این یکی رو برداره (توی اینونتوری)
            // که بعداً با removeElytraFromInventory پاک می‌شه
            // پس اینجا کاری نکن، بذار برداره بعد حذف شه
            if (hasElytraOnBody) {
                // می‌تونیم لغو کنیم که کلاً برنداره
                event.setCancelled(true);
                event.getItem().remove();
            }
        }
    }
}
