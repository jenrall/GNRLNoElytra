package com.gnrl.noelytra;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraListener implements Listener {

    private final GNRLNoElytra plugin;
    private final ElytraManager manager;

    public ElytraListener(GNRLNoElytra plugin, ElytraManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    // ═══════════ جلوگیری از گلاید کردن ═══════════

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.isGliding() && plugin.getConfig().getBoolean("settings.prevent-glide", true)) {
            event.setCancelled(true);
            player.setGliding(false);
            player.sendRichMessage(plugin.getConfig().getString("settings.messages.glide-denied", ""));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        }
    }

    // ═══════════ جلوگیری از پوشیدن الایترا ═══════════

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!plugin.getConfig().getBoolean("settings.prevent-equip", true)) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.ELYTRA) return;

        // اگه داره توی اسلات سینه می‌ذاره (اسلات 38)
        if (event.getSlot() == 38) {
            event.setCancelled(true);
            player.sendRichMessage(plugin.getConfig().getString("settings.messages.equip-denied", ""));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        }
    }

    // ═══════════ جلوگیری از تعامل با الایترا ═══════════

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() == Material.ELYTRA) {
            event.setCancelled(true);
            // حذف از دست
            player.getInventory().setItemInMainHand(null);
            player.getInventory().setItemInOffHand(null);
            player.updateInventory();
        }
    }

    // ═══════════ حذف الایترا موقع جوین ═══════════

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // تاخیر ۱ ثانیه‌ای برای اطمینان از لود شدن اینونتوری
        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> manager.removeElytraFromInventory(player), 20L);
    }

    // ═══════════ حذف الایترا موقع برداشتن ═══════════

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item != null && item.getType() == Material.ELYTRA) {
            player.getInventory().setItem(event.getNewSlot(), null);
            player.updateInventory();
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

    // ═══════════ حذف الایترا موقع حرکت ═══════════

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // اگه در حال گلاید باشه، متوقفش کن
        if (player.isGliding()) {
            player.setGliding(false);
        }

        // اگه الایترا توی اسلات سینه داره، حذفش کن
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            player.getInventory().setChestplate(null);
            player.updateInventory();
        }
    }
}
