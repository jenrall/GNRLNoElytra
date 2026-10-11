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
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraListener implements Listener {

    private final GNRLNoElytra plugin;
    private final ElytraManager manager;

    public ElytraListener(GNRLNoElytra plugin, ElytraManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    // ═══════════ جلوگیری از گلاید کردن (اختیاری) ═══════════
    // اگه می‌خوای کلاً گلاید غیرفعال باشه، این رو نگه دار
    // اگه می‌خوای یه بار بتونه استفاده کنه، این رو کامنت کن

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // ═══ این شرط رو کامنت کن اگه می‌خوای گلاید فعال بمونه ═══
        // if (event.isGliding() && plugin.getConfig().getBoolean("settings.prevent-glide", true)) {
        //     event.setCancelled(true);
        //     player.setGliding(false);
        //     player.sendRichMessage(plugin.getConfig().getString("settings.messages.glide-denied", ""));
        // }
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

        // اگه در حال گلاید کردنه، کاری نکن
        if (player.isGliding()) return;

        if (event.getItem() != null && event.getItem().getType() == Material.ELYTRA) {
            event.setCancelled(true);
            player.getInventory().setItemInMainHand(null);
            player.getInventory().setItemInOffHand(null);
            player.updateInventory();
        }
    }

    // ═══════════ حذف الایترا موقع جوین ═══════════

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> manager.removeElytraFromInventory(player), 20L);
    }

    // ═══════════ حذف الایترا موقع برداشتن ═══════════

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // اگه در حال گلاید کردنه، کاری نکن
        if (player.isGliding()) return;

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
        Player player = event.getPlayer();

        // اگه در حال گلاید کردنه، کاری نکن
        if (player.isGliding()) return;

        if (event.getItemDrop().getItemStack().getType() == Material.ELYTRA) {
            event.getItemDrop().remove();
        }
    }

    // ═══════════ چک موقع حرکت: اگه گلاید تموم شد، الایترا رو حذف کن ═══════════

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // اگه در حال گلاید نیست ولی الایترا توی اسلات سینه داره، حذفش کن
        if (!player.isGliding()) {
            ItemStack chestplate = player.getInventory().getChestplate();
            if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
                player.getInventory().setChestplate(null);
                player.updateInventory();
                player.sendRichMessage(plugin.getConfig().getString("settings.messages.elytra-removed", ""));
            }
        }
    }

    // ═══════════ چک موقع Sneak: اگه یارو sneak کرد، الایترا رو حذف کن ═══════════
    // (چون sneak توی هوا = توقف گلاید)

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking() && !player.isGliding()) {
            ItemStack chestplate = player.getInventory().getChestplate();
            if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
                player.getInventory().setChestplate(null);
                player.updateInventory();
                player.sendRichMessage(plugin.getConfig().getString("settings.messages.elytra-removed", ""));
            }
        }
    }
}
