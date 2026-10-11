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

    public ElytraManager(GNRLNoElytra plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        // فقط config رو دوباره می‌خونیم
    }

    // ═══════════ حذف الایترا از اینونتوری بازیکن ═══════════
    // فقط از اینونتوری، نه از اسلات سینه

    public void removeElytraFromInventory(Player player) {
        if (!plugin.getConfig().getBoolean("settings.remove-from-inventory", true)) return;

        boolean removed = false;

        // اینونتوری اصلی (به جز اسلات سینه)
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            // اسلات سینه رو رد کن (38 = اسلات chestplate)
            if (i == 38) continue;

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

        if (removed) {
            player.updateInventory();
            player.sendRichMessage(plugin.getConfig().getString("settings.messages.elytra-removed", ""));
        }
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
        }, 20L, 20L); // هر ۱ ثانیه
    }

    public void cancelTasks() {
        if (droppedTask != null) {
            droppedTask.cancel();
        }
    }
}
