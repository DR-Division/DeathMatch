package org.light.source.Log;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.DeathMatch;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MinimizeLogger {

    private DeathMatch Plugin;
    private static MinimizeLogger instance;
    private ObjectArrayList<String> logs;
    private File logFile;
    private SimpleDateFormat fileFormat, logFormat;
    private int taskID;
    private YamlConfiguration configFormat;

    static
    {
        instance = new MinimizeLogger();
    }

    private MinimizeLogger(){
        Plugin = JavaPlugin.getPlugin(DeathMatch.class);
        logs = new ObjectArrayList<>();
        File folder = new File("plugins/" + Plugin.getDescription().getName() + "/log");
        fileFormat = new SimpleDateFormat("MM.dd-HH.mm.ss");
        logFormat = new SimpleDateFormat("[ MM-dd / HH:mm:ss ]");
        taskID = 0;
        if (!folder.exists())
            folder.mkdir();
    }

    public static MinimizeLogger getInstance(){
        return instance;
    }

    public void appendLog(String log){
        String time = logFormat.format(Calendar.getInstance().getTime());
        logs.add(time + " " + log);
    }

    public void logStart(){
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin, this::saveLog,0L, 1200L);
    }

    public void saveLog(){
        if (logs.size() >= 20){
            logFile = new File("plugins/" + Plugin.getDescription().getName() + "/log/" + fileFormat.format(Calendar.getInstance().getTime()) + ".log");
            configFormat = YamlConfiguration.loadConfiguration(logFile);
            configFormat.set("로그", logs);
            try {
                configFormat.save(logFile);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            logs.clear();
        }
    }

    public void forceSaveLog(){
        if (!logs.isEmpty()){
            logFile = new File("plugins/" + Plugin.getDescription().getName() + "/log/" + fileFormat.format(Calendar.getInstance().getTime()) + ".log");
            configFormat = YamlConfiguration.loadConfiguration(logFile);
            configFormat.set("로그", logs);
            try {
                configFormat.save(logFile);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            logs.clear();
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

}
