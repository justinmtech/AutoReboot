package com.justinmtech.autoreboot.autoreboot;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;

public final class AutoReboot extends JavaPlugin implements Listener {
    private boolean hasAnnounced;

    @Override
    public void onEnable() {
        setHasAnnounced(false);
        saveDefaultConfig();
        rebootChecker();
        getLogger().log(Level.INFO, "Plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Plugin disabled.");
    }

    private int getUpTimeInSeconds() {
        return (int) ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
    }

    private int getRebootInterval() {
        return getConfig().getInt("interval", 24);
    }

    private boolean isOneMinuteBefore() {
        return getRebootInterval() - getUpTimeInSeconds() <= 60;
    }

    private boolean isRebootTime() {
        return getUpTimeInSeconds() >= getRebootInterval();
    }


    private void rebootChecker() {
        String msg = getConfig().getString("warning", "§cThe server is rebooting in 1 minute!");
        int playerThreshold = getConfig().getInt("player-threshold", 0);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getOnlinePlayerCount() <= playerThreshold) {
                    if (isOneMinuteBefore() && !isHasAnnounced()) {
                        Bukkit.broadcastMessage(msg);
                        setHasAnnounced(true);
                    }
                    if (isRebootTime()) {
                        cancel();
                        Bukkit.shutdown();
                    }
                }
            }
        }.runTaskTimer(this, 1200L, 1200L);
    }

    private int getOnlinePlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    private boolean isHasAnnounced() {
        return hasAnnounced;
    }

    private void setHasAnnounced(boolean hasAnnounced) {
        this.hasAnnounced = hasAnnounced;
    }
}
