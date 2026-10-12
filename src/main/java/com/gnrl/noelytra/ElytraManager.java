package com.gnrl.noelytra;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class ElytraManager {

    private final GNRLNoElytra plugin;
    private BukkitTask droppedTask;
    private BukkitTask playerCheckTask;

    public ElytraManager(GNRLNoElytra plugin) {
        this.plugin = plugin;
    }

    public void reload() {
    }

    // ═══════════ حذف الایترا از اینونتوری بازیکن ═══════════
    // فقط از اینونتوری، نه از اسلات سینه

    public void removeElytraFromInventory(Player player) {
        if (!plugin.getConfig().getBoolean("settings.remove-from-inventory", true)) return;

        boolean removed = false;

        // اینونتوری اصلی (به جز اسلات سینه)
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (i == 38) continue; // اسلات سینه رو رد کن
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.ELYTRA) {
                player.getInventory().setItem(i, null);
                removed = true;
            }
        }

        // دست آف
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (offHand.getType() == Material.ELYTRA) {
            player.getInventory().setItemInOffHand(null);
            removed = true;
        }

        // هلمت (احتیاط)
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && helmet.getType() == Material.ELYTRA) {
            player.getInventory().setHelmet(null);
            removed = true;
        }

        // بوت (احتیاط)
        ItemStack boots = player.getInventory().getBoots();
        if (boots != null && boots.getType() == Material.ELYTRA) {
            player.getInventory().setBoots(null);
            removed = true;
        }

        // لگ (احتیاط)
        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings != null && leggings.getType() == Material.ELYTRA) {
            player.getInventory().setLeggings(null);
            removed = true;
        }

        if (removed) {
            player.updateInventory();
            player.sendRichMessage(plugin.getConfig().getString("settings.messages.elytra-removed", ""));
        }
    }

    // ═══════════ تسک چک کردن همه بازیکن‌ها (هر ۰.۲۵ ثانیه) ═══════════

    public void startPlayerCheckTask() {
        // اجرای اولیه با تاخیر 0 تیک، بعد هر 5 تیک
        playerCheckTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!plugin.getConfig().getBoolean("settings.remove-from-inventory", true)) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean hasElytraInInv = false;

                // چک اینونتوری اصلی
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    if (i == 38) continue;
                    ItemStack item = player.getInventory().getItem(i);
                    if (item != null && item.getType() == Material.ELYTRA) {
                        hasElytraInInv = true;
                        break;
                    }
                }

                // چک دست آف
                if (!hasElytraInInv) {
                    ItemStack offHand = player.getInventory().getItemInOffHand();
                    if (offHand.getType() == Material.ELYTRA) {
                        hasElytraInInv = true;
                    }
                }

                if (hasElytraInInv) {
                    removeElytraFromInventory(player);
                }
            }
        }, 5L, 5L); // هر ۵ تیک (۰.۲۵ ثانیه)
    }

    // ═══════════ حذف الایتراهای دراپ شده روی زمین ═══════════

    public void startDroppedTask() {
        droppedTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!plugin.getConfig().getBoolean("settings.remove-dropped", true)) return;

            for (org.bukkit.World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof Item item) {
                        ItemStack stack = item.getItemStack();
                        if (stack.getType() == Material.ELYTRA) {
                            item.remove();
                        }
                    }
                }
            }
        }, 5L, 5L); // هر ۵ تیک
    }

    public void cancelTasks() {
        if (droppedTask != null) droppedTask.cancel();
        if (playerCheckTask != null) playerCheckTask.cancel();
    }
}
