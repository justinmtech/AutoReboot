package com.justinmtech.autoreboot.autoreboot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;

/**
 * Reboot your server at an interval.
 */
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


    private int getUptimeInSeconds() {
        return (int) ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
    }
    private int getUptimeInHours() {
        int seconds = getUptimeInSeconds();
        int minutes = seconds / 60;
        return minutes / 60;
    }

    private int getRebootInterval() {
        return getConfig().getInt("interval", 24);
    }
    private int getRebootIntervalInSeconds() {
        int hours = getRebootInterval();
        int minutes = hours * 60;
        return minutes * 60;
    }

    private boolean isOneMinuteBefore() {
        return getRebootIntervalInSeconds() - getUptimeInSeconds() <= 60;
    }

    private boolean isRebootTime() {
        return getUptimeInHours() >= getRebootInterval();
    }


    private void rebootChecker() {
        String msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("warning", "§cThe server is rebooting in 1 minute!"));
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
        }.runTaskTimer(this, 600L, 600L);
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
