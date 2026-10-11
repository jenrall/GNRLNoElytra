package com.gnrl.noelytra;

import org.bukkit.plugin.java.JavaPlugin;

public class GNRLNoElytra extends JavaPlugin {

    private ElytraManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        manager = new ElytraManager(this);

        getServer().getPluginManager().registerEvents(new ElytraListener(this, manager), this);

        // دستور reload
        getCommand("noelytra").setExecutor((sender, cmd, label, args) -> {
            if (!sender.hasPermission("gnrlnoelytra.admin")) {
                sender.sendRichMessage("<red>شما دسترسی ندارید.</red>");
                return true;
            }
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                manager.reload();
                sender.sendRichMessage("<green>✔ پلاگین ری‌لود شد!</green>");
                return true;
            }
            sender.sendRichMessage("<yellow>Usage: /noelytra reload</yellow>");
            return true;
        });

        // تسک حذف الایتراهای دراپ شده
        manager.startDroppedTask();

        getLogger().info("GNRLNoElytra فعال شد!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GNRLNoElytra غیرفعال شد.");
    }

    public ElytraManager getManager() { return manager; }
}
