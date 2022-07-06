package org.light.source.Runnable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.source.Game.GameManager;
import org.light.source.Game.UserMananger;

public class MainTimer extends BukkitRunnable {

    private int maxSecond;
    private int now;
    private BossBar bossBar;
    private ObjectArrayList<UserMananger> users;

    public MainTimer(int maxSecond, ObjectArrayList<UserMananger> users){
        this.maxSecond = maxSecond;
        this.users = users;
        now = 0;
        bossBar = Bukkit.createBossBar("§5Timer", BarColor.RED, BarStyle.SOLID);
    }

    @Override
    public void run() {
        if (now == 0){
            for (UserMananger mgr : users){
                Player target = Bukkit.getServer().getPlayer(mgr.getUUID());
                if (target == null || !target.isOnline()) continue;
                bossBar.setProgress(1.0f);
                bossBar.addPlayer(target);
                bossBar.setTitle("§5Timer §7: §6" + maxSecond + "§f초");
            }
        }
        else if (now >= maxSecond){
            bossBar.removeAll();
            //우승자 가리기
            for (UserMananger mgr : users){
                Player target = Bukkit.getServer().getPlayer(mgr.getUUID());
                if (target != null && target.isOnline())
                target.sendMessage("§b시간이 다 되어 게임이 종료되었습니다!");
            }
            GameManager.getInstance().stop();
        }
        else{
            bossBar.setTitle("§5Timer §7: §6" + (maxSecond - now) + "§f초");
            bossBar.setProgress(calcProgress(maxSecond, now));
            if (maxSecond - now <= 5){
                for (UserMananger mgr : users){
                    Player target = Bukkit.getServer().getPlayer(mgr.getUUID());
                    if (target != null && target.isOnline())
                    target.sendMessage("§f게임 종료까지 §6" + (maxSecond-now) + "§f초 남았습니다!");
                }
            }
        }
        now++;
    }

    public BossBar getbossbarInstance(){
        return bossBar;
    }

    private float calcProgress(int remain, int now){
         if ((1.0 - (float)now / remain) < 0)
             return 0.0f;
         else
             return (float) (1.0 - (float)now / remain);
    }
}
