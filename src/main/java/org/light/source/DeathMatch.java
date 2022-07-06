package org.light.source;

import com.comphenix.protocol.PacketType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.dayz.DMain;
import org.light.source.Command.CommandController;
import org.light.source.Command.HologramCommand;
import org.light.source.Command.JoinLeaveCommand;
import org.light.source.Command.KillDeathCommand;
import org.light.source.Game.AfkManager;
import org.light.source.Listener.EventManager;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Runnable.WeatherRunnable;
import org.light.source.Singleton.FileManager;
import org.light.source.Singleton.KillDeathFileManager;
import com.comphenix.protocol.ProtocolLibrary;
import org.light.source.packet.SneakAdapter;

import java.util.Objects;

public class DeathMatch extends JavaPlugin {

    public static DeathMatch instance;
    private final DMain main;

    public DeathMatch() {
        super();
        main = new DMain(this);
    }

    @Override
    public void onEnable(){
        instance = this;
        getLogger().info("DeathMatch Plugin Enabled");
        loadCommand();
        getServer().getPluginManager().registerEvents(new EventManager(this), this);
        FileManager.getInstance().load();
        MinimizeLogger.getInstance().logStart();
        KillDeathFileManager.getInstance().load();
        new AfkManager().runTaskTimer(this, 0L, 20L);
        new WeatherRunnable(this);
        Bukkit.getScheduler().runTaskLater(this, () -> ProtocolLibrary.getProtocolManager().addPacketListener(new SneakAdapter(this, PacketType.Play.Server.ENTITY_METADATA)), 60L);
        main.makeEnable();

    }

    @Override
    public void onDisable(){
        getLogger().info("DeathMatch Plugin Disabled");
        FileManager.getInstance().save();
        MinimizeLogger.getInstance().forceSaveLog();
        KillDeathFileManager.getInstance().save();
        main.makeDisable();
    }

    public void loadCommand(){
        JoinLeaveCommand joinCommand = new JoinLeaveCommand();
        KillDeathCommand killDeathCommand = new KillDeathCommand();
        Objects.requireNonNull(getCommand("deathmatch")).setExecutor(new CommandController(this));
        Objects.requireNonNull(getCommand("join")).setExecutor(joinCommand);
        Objects.requireNonNull(getCommand("leave")).setExecutor(joinCommand);
        Objects.requireNonNull(getCommand("kd")).setExecutor(killDeathCommand);
        Objects.requireNonNull(getCommand("rank")).setExecutor(killDeathCommand);
        Objects.requireNonNull(getCommand("hd")).setExecutor(new HologramCommand());
    }

}
