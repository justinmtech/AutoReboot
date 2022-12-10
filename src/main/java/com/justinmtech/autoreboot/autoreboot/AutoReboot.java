package com.justinmtech.autoreboot.autoreboot;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;

public final class AutoReboot extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
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

    private int getUpTimeInHours() {
        int seconds = (int) ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        int minutes = seconds / 60;
        return minutes * 60;
    }

    private int getRebootTime() {
        return getConfig().getInt("interval");
    }

    private boolean isOneMinuteBefore() {
        return getRebootTime() - getUpTimeInSeconds() == 60;
    }

    private boolean isRebootTime() {
        return getUpTimeInSeconds() >= getRebootTime();
    }


    public void rebootChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isOneMinuteBefore()) Bukkit.broadcastMessage(getConfig().getString("warning"));
                if (isRebootTime()) Bukkit.shutdown();
            }
        }.runTaskTimerAsynchronously(this, 20L, 20L);
    }
}
