package org.light.dayz;

import org.bukkit.Bukkit;
import org.light.dayz.command.*;
import org.light.dayz.data.Scav;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.event.*;
import org.light.dayz.runnable.InfectAndInfoRunnable;
import org.light.dayz.util.CorpseRegistry;
import org.light.dayz.util.NPCMaker;
import org.light.dayz.util.Regen;
import org.light.dayz.util.ScavRegistry;
import org.light.source.DeathMatch;
import org.light.source.Singleton.DataManager;

public class DMain {

    private DeathMatch Plugin;
    private YamlConfig config;

    public DMain(DeathMatch Plugin) {
        this.Plugin = Plugin;
        config = new YamlConfig(Plugin);
    }

    public void makeEnable() {
        config.load();
        Plugin.getCommand("dayz").setExecutor(new GameCommand(config));
        Plugin.getCommand("chest").setExecutor(new ChestCommand());
        Plugin.getCommand("shop").setExecutor(new SellCommand());
        Plugin.getCommand("trash").setExecutor(new TrashCommand());
        Plugin.getCommand("bugremover").setExecutor(new BugCommand(Plugin));
        Plugin.getCommand("emergency").setExecutor(new EmergencyExit(Plugin));
        Plugin.getCommand("trade").setExecutor(new TradeCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new FoodLevel(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PreventShoot(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new SpawnMob(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new WeaponDamage(config, Plugin), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ChestInteraction(Plugin), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemInteraction(Plugin), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ArmorStandInteraction(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityTarget(), Plugin);
        new InfectAndInfoRunnable().runTaskTimer(Plugin, 0L, 40L);
        Regen.startTask();
        CorpseRegistry.activeTimer();
        NPCMaker.startTick();
        ScavRegistry.active();
        Bukkit.getScheduler().runTaskTimer(Plugin, ScavRegistry::spawnScavs, 0L, 36000L);
        DataManager.getInstance().setLocations(Bukkit.getWorld("lobby").getSpawnLocation(), 1);
    }

    public void makeDisable() {
        config.save();
        CorpseRegistry.removeAll();
        ScavRegistry.removeAll();
    }


}
