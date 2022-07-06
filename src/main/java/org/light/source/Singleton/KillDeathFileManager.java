package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.DeathMatch;
import org.light.source.Game.KillDeathManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class KillDeathFileManager {

    private DeathMatch Plugin;
    private static KillDeathFileManager instance;
    private File file;
    private YamlConfiguration config;

    static {
        instance = new KillDeathFileManager(JavaPlugin.getPlugin(DeathMatch.class));
    }

    private KillDeathFileManager(DeathMatch Plugin){
        this.Plugin = Plugin;
        File folder = new File("plugins/" + Plugin.getDescription().getName() + "/rank");
        if (!folder.exists())
            folder.mkdir();
        file = new File("plugins/" + Plugin.getDescription().getName() + "/rank/rank.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static KillDeathFileManager getInstance(){
        return instance;
    }

    public void load(){
        int input, kill, death;
        String name;
        String uid;
        try {
            config.load(file);
        }
        catch (IOException | InvalidConfigurationException e) {
            Bukkit.broadcastMessage("Error Load File");
        }
        input = config.getInt("RankAmount", 0);
        for (int i = 0; i < input; i++) {
            uid = config.getString("Rank." + (i+1) + ".UUID", null);
            kill = config.getInt("Rank." + (i+1) + ".Kill", 0);
            death = config.getInt("Rank." + (i+1) + ".Death", 0);
            name = config.getString("Rank." + (i+1) + ".Name", "Offline");
            if (uid != null){
                KillDeathManager.getInstance().setValue(name, UUID.fromString(uid), kill, death);
            }
        }
    }

    public void save(){
        int i,size;
        i = 1;
        size = KillDeathManager.getInstance().getList().size();
        config.set("RankAmount", size);
        for (UUID uuid : KillDeathManager.getInstance().getList().keySet()){
            KillDeathObject object = KillDeathManager.getInstance().getValue(uuid);
            config.set("Rank." + i + ".UUID", uuid.toString());
            config.set("Rank." + i + ".Kill", object.getKill());
            config.set("Rank." + i + ".Death", object.getDeath());
            config.set("Rank." + i++ + ".Name", object.getName());
        }
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
