package org.light.dayz.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.command.GameCommand;
import org.light.dayz.util.ScavRegistry;
import org.light.dayz.util.VirtualChest;
import org.light.source.DeathMatch;
import org.light.source.Singleton.CrackShotApi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class YamlConfig {

    private DeathMatch Plugin;
    private File file;
    private File chestFile;
    private YamlConfiguration config;
    private YamlConfiguration chestConfig;
    private ArrayList<Location> locations;
    private ArrayList<String> helpWeapon;
    private ArrayList<String> denyWeapon;
    private int zKill;
    private int hKill;
    private int regen;
    public static YamlConfig instance;

    public YamlConfig(DeathMatch Plugin) {
        this.Plugin = Plugin;
        locations = new ArrayList<>();
        helpWeapon = new ArrayList<>();
        denyWeapon = new ArrayList<>();
        instance = this;
        zKill = 0;
        hKill = 0;
        file = new File("plugins/" + Plugin.getDescription().getName() + "/Dayz-Config.yml");
        chestFile = new File("plugins/" + Plugin.getDescription().getName() + "/Dayz-Chest.yml");
        checkFile();
        config = YamlConfiguration.loadConfiguration(file);
        chestConfig = YamlConfiguration.loadConfiguration(chestFile);
    }

    private void checkFile() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!chestFile.exists()) {
            try {
                chestFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        //location.1 ~ location.2 ~
        try {
            config.load(file);
            chestConfig.load(chestFile);
        }
        catch (IOException | InvalidConfigurationException e) {
            Bukkit.broadcastMessage("Error Load File");
        }
        regen = config.getInt("chest-regen", 10); //단위 분
        zKill = config.getInt("zombie-kill", 1);
        hKill = config.getInt("human-kill", 10);
        helpWeapon.clear();
        denyWeapon.clear();
        locations.clear();
        VirtualChest.chest.clear();
        VirtualChest.chest2.clear();
        VirtualChest.chest3.clear();
        VirtualChest.chest4.clear();
        VirtualChest.chest5.clear();
        helpWeapon.addAll(config.getStringList("first-weapon"));
        denyWeapon.addAll(config.getStringList("non-drop-weapon"));
        CrackShotApi.generateWeaponMap();
        if (chestConfig.getConfigurationSection("chest") != null) {
            for (String key : chestConfig.getConfigurationSection("chest").getKeys(false)) {
                UUID uid = UUID.fromString(key);
                ArrayList<String> strings = new ArrayList<>(chestConfig.getStringList("chest." + key));
                VirtualChest.toInventory(uid, strings, VirtualChest.Number.ONE);
            }
        }
        if (chestConfig.getConfigurationSection("chest2") != null) {
            for (String key : chestConfig.getConfigurationSection("chest2").getKeys(false)) {
                UUID uid = UUID.fromString(key);
                ArrayList<String> strings = new ArrayList<>(chestConfig.getStringList("chest2." + key));
                VirtualChest.toInventory(uid, strings, VirtualChest.Number.TWO);
            }
        }
        if (chestConfig.getConfigurationSection("chest3") != null) {
            for (String key : chestConfig.getConfigurationSection("chest3").getKeys(false)) {
                UUID uid = UUID.fromString(key);
                ArrayList<String> strings = new ArrayList<>(chestConfig.getStringList("chest3." + key));
                VirtualChest.toInventory(uid, strings, VirtualChest.Number.THREE);
            }
        }
        if (chestConfig.getConfigurationSection("chest4") != null) {
            for (String key : chestConfig.getConfigurationSection("chest4").getKeys(false)) {
                UUID uid = UUID.fromString(key);
                ArrayList<String> strings = new ArrayList<>(chestConfig.getStringList("chest4." + key));
                VirtualChest.toInventory(uid, strings, VirtualChest.Number.FOUR);
            }
        }
        if (chestConfig.getConfigurationSection("chest5") != null) {
            for (String key : chestConfig.getConfigurationSection("chest5").getKeys(false)) {
                UUID uid = UUID.fromString(key);
                ArrayList<String> strings = new ArrayList<>(chestConfig.getStringList("chest5." + key));
                VirtualChest.toInventory(uid, strings, VirtualChest.Number.FIVE);
            }
        }
        if (config.getConfigurationSection("location") != null)
            for (String key : config.getConfigurationSection("location").getKeys(false))
                locations.add(getLocation("location." + key));
        if (config.getConfigurationSection("scav") != null) {
            for (String key : config.getConfigurationSection("scav").getKeys(false)) {
                ItemStack helmet = new ItemStack(Material.valueOf(config.getString("scav." + key + ".helmet")));
                ItemStack chest = new ItemStack(Material.valueOf(config.getString("scav." + key + ".chest")));
                ItemStack legg = new ItemStack(Material.valueOf(config.getString("scav." + key + ".leggings")));
                ItemStack boots = new ItemStack(Material.valueOf(config.getString("scav." + key + ".boots")));
                ScavRegistry.addScav(config.getString("scav." + key + ".name", "NONE"), config.getString("scav." + key + ".weapon"), getLocation("scav." + key + ".location"), config.getDouble("scav" + key + ".health", 60.0), helmet, chest, legg, boots);
            }
        }
        ArrayList<String> data = new ArrayList<>(config.getStringList("gift"));
        ArrayList<UUID> result = new ArrayList<>();
        for (String value : data) {
            try {
                UUID uid = UUID.fromString(value);
                result.add(uid);
            }
            catch (Exception e) {

            }
        }
        GameCommand.giftMap.addAll(result);


    }

    public void save() {
        int count = 0;
        chestConfig.set("chest", null);
        chestConfig.set("chest2", null);
        chestConfig.set("chest3", null);
        chestConfig.set("chest4", null);
        chestConfig.set("chest5", null);
        config.set("gift", null);
        config.set("scav", null);
        config.set("chest-regen", regen);
        config.set("zombie-kill", zKill);
        config.set("human-kill", hKill);
        config.set("first-weapon", helpWeapon);
        config.set("non-drop-weapon", denyWeapon);
        for (int i = 0; i < locations.size(); i++)
            setLocation("location." + i, locations.get(i));
        for (UUID key : VirtualChest.chest.keySet()) {
            ArrayList<String> list = VirtualChest.toConfig(VirtualChest.chest.get(key));
            chestConfig.set("chest." + key.toString(), list);
        }
        for (UUID key : VirtualChest.chest2.keySet()) {
            ArrayList<String> list = VirtualChest.toConfig(VirtualChest.chest2.get(key));
            chestConfig.set("chest2." + key.toString(), list);
        }
        for (UUID key : VirtualChest.chest3.keySet()) {
            ArrayList<String> list = VirtualChest.toConfig(VirtualChest.chest3.get(key));
            chestConfig.set("chest3." + key.toString(), list);
        }
        for (UUID key : VirtualChest.chest4.keySet()) {
            ArrayList<String> list = VirtualChest.toConfig(VirtualChest.chest4.get(key));
            chestConfig.set("chest4." + key.toString(), list);
        }
        for (UUID key : VirtualChest.chest5.keySet()) {
            ArrayList<String> list = VirtualChest.toConfig(VirtualChest.chest5.get(key));
            chestConfig.set("chest5." + key.toString(), list);
        }
        for (Scav value : ScavRegistry.scavArrayList) {
            config.set("scav." + count + ".name", value.getName());
            config.set("scav." + count + ".weapon", value.getWeaponID());
            setLocation("scav." + count + ".location", value.getSpawnLoc());
            config.set("scav." + count + ".health", value.getHealth());
            config.set("scav." + count + ".helmet", value.getEquipment()[0].getType().toString());
            config.set("scav." + count + ".chest", value.getEquipment()[1].getType().toString());
            config.set("scav." + count + ".leggings", value.getEquipment()[2].getType().toString());
            config.set("scav." + count + ".boots", value.getEquipment()[3].getType().toString());
            count++;
        }
        ArrayList<String> value = new ArrayList<>();
        for (UUID data : GameCommand.giftMap) {
            value.add(data.toString());
        }
        config.set("gift", value);

        try {
            config.save(file);
            chestConfig.save(chestFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public ArrayList<String> getHelpWeapon() {
        return helpWeapon;
    }

    public void setHelpWeapon(ArrayList<String> helpWeapon) {
        this.helpWeapon = helpWeapon;
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


    public int getZKill() {
        return zKill;
    }

    public void setZKill(int zKill) {
        this.zKill = zKill;
    }

    public int getHKill() {
        return hKill;
    }

    public void setHKill(int hKill) {
        this.hKill = hKill;
    }

    public int getRegen() {
        return regen;
    }

    public void setRegen(int regen) {
        this.regen = regen;
    }

    public ArrayList<String> getDenyWeapon() {
        return denyWeapon;
    }

    public void setDenyWeapon(ArrayList<String> denyWeapon) {
        this.denyWeapon = denyWeapon;
    }
}