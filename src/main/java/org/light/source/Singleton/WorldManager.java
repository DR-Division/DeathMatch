package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.ArrayList;

public class WorldManager {

    private static WorldManager instance;
    private ArrayList<String> worlds;

    static {
        instance = new WorldManager();
    }

    private WorldManager() {
        worlds = new ArrayList<>();
    }

    public static WorldManager getInstance() {
        return instance;
    }

    public void addWorld(String name) {
        if (!worlds.contains(name))
            worlds.add(name);
    }

    public boolean containWorld(String name) {
        return worlds.contains(name);
    }

    public void removeWorld(String name) {
        worlds.remove(name);
    }

    public void loadWorld() {
        worlds.stream().filter(data -> Bukkit.getWorld(data) == null).forEach(world -> {
            WorldCreator worldCreator = new WorldCreator(world);
            worldCreator.type(WorldType.FLAT);
            worldCreator.generateStructures(false);
            worldCreator.environment(World.Environment.NORMAL);
            worldCreator.generatorSettings("{\"structures\":{\"structures\":{}},\"layers\":[{\"block\":\"air\",\"height\":1}],\"biome\":\"minecraft:the_void\"}");
            World createdWorld = worldCreator.createWorld();
            if (createdWorld == null) return;
            createdWorld.setDifficulty(org.bukkit.Difficulty.NORMAL); // explode damage
            createdWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            createdWorld.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            createdWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        });
    }

    public ArrayList<String> getWorlds() {
        return worlds;
    }

    public void clear() {
        worlds.clear();
    }
}
