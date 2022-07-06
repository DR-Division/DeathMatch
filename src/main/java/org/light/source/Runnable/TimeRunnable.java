package org.light.source.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.source.DeathMatch;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimeRunnable extends BukkitRunnable {

    private DeathMatch Plugin;
    private SimpleDateFormat format;
    private long[] times;

    public TimeRunnable(DeathMatch Plugin){
        format = new SimpleDateFormat("kk");
        this.Plugin = Plugin;
        //0~24 0틱은 오전 6시부터 시작
        times = new long[]{18000,19000,20000,21000,22000,23000,0,1000,2000,3000,4000,5000,6000,7000,8000,9000,10000,11000,12000,13000,14000,15000,16000,17000};
        start();
    }

    @Override
    public void run() {
        int hour;
        hour = Integer.parseInt(format.format(Calendar.getInstance().getTime())) % 24;
        Bukkit.getWorlds().forEach(world -> {
            if (!world.getName().contains("dayz_"))
                world.setTime(times[hour]);
            //즉시 리스폰
            /*if (!world.getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN))
                world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);*/
        });
    }

    public void start(){
        runTaskTimer(Plugin, 0L, 400L);
    }
}
