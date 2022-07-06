package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.DeathMatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {

    private static FileManager manager;
    private File folder;
    private File file;
    private YamlConfiguration config;
    private DeathMatch Plugin;

    static {
        manager = new FileManager();
    }

    private FileManager() {
        Plugin = JavaPlugin.getPlugin(DeathMatch.class);
        folder = new File("plugins/" + Plugin.getDescription().getName());
        file = new File("plugins/" + Plugin.getDescription().getName() + "/config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        if (!folder.exists())
            folder.mkdir();
    }

    public static FileManager getInstance() {
        return manager;
    }

    public void save() {
        DataManager manager = DataManager.getInstance();
        config.set("Round", manager.getRounds());
        config.set("KillLevel", manager.getKilltolevel());
        config.set("Time", manager.getTime());
        config.set("RequireJoin", manager.getMinimumUser());
        config.set("JoinReward", manager.getJoinMoney());
        config.set("FirstReward", manager.getFirstReward());
        config.set("SecondReward", manager.getSecondReward());
        config.set("ThirdReward", manager.getThirdReward());
        config.set("LocationAmount", manager.getLocationAmount());
        config.set("KillMaintain", manager.getKillMaintain());
        config.set("MaxReRoll", manager.getMaxReroll());
        config.set("ReRollMoney", manager.getReRollMoney());
        config.set("WaitTime", manager.getWaitTime());
        config.set("Worlds", WorldManager.getInstance().getWorlds());
        if (manager.getLocations() != null) {
            for (int i = 0; i < DataManager.getInstance().getLocationAmount(); i++)
                setLocation("Location." + (i + 1), manager.getLocations()[i]);
        }
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            config.load(file);
        }
        catch (IOException | InvalidConfigurationException e) {
            Bukkit.broadcastMessage("Error Load File");
        }
        WorldManager worldManager = WorldManager.getInstance();
        DataManager manager = DataManager.getInstance();
        ArrayList<String> list = new ArrayList<>(config.getStringList("Worlds"));
        worldManager.clear();
        for (String world : list)
            worldManager.addWorld(world);
        worldManager.loadWorld();
        manager.setRounds(config.getInt("Round"));
        manager.setTime(config.getInt("Time"));
        manager.setKilltolevel(config.getInt("KillLevel"));
        manager.setMinimumUser(config.getInt("RequireJoin"));
        manager.setJoinMoney(config.getInt("JoinReward", 30));
        manager.setFirstReward(config.getInt("FirstReward", 150));
        manager.setSecondReward(config.getInt("SecondReward", 100));
        manager.setThirdReward(config.getInt("ThirdReward", 50));
        manager.setKillMaintain(config.getInt("KillMaintain", 10));
        manager.setMaxReroll(config.getInt("MaxReRoll", 10));
        manager.setReRollMoney(config.getInt("ReRollMoney", 200));
        manager.setWaitTime(config.getInt("WaitTime", 60));
        Bukkit.getScheduler().runTaskLaterAsynchronously(Plugin, () -> {
            if (config.get("Location.1") != null && config.getInt("LocationAmount") != 0) {
                int amount = config.getInt("LocationAmount");
                for (int i = 0; i < amount; i++) {
                    DataManager.getInstance().setLocations(getLocation("Location." + (i + 1)), i + 1);
                }
            }
        }, 40L);
    }

    private void setLocation(String path, Location value) {
        config.set(path + ".X", value.getX());
        config.set(path + ".Y", value.getY());
        config.set(path + ".Z", value.getZ());
        config.set(path + ".World", value.getWorld().getName());
        config.set(path + ".Pitch", value.getPitch());
        config.set(path + ".Yaw", value.getYaw());
    }

    private Location getLocation(String path) {
        double x = config.getDouble(path + ".X", -1);
        double y = config.getDouble(path + ".Y", -1);
        double z = config.getDouble(path + ".Z", -1);
        float pitch = (float) config.getDouble(path + ".Pitch", 0);
        float yaw = (float) config.getDouble(path + ".Yaw", 0);
        String world = config.getString(path + ".World", null);
        if (world == null || Bukkit.getWorld(world) == null)
            return null;
        else
            return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);

    }
}
