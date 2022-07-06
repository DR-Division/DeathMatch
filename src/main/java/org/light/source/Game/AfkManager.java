package org.light.source.Game;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Singleton.AfkObject;
import org.light.source.Singleton.DataManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class AfkManager extends BukkitRunnable {

    private Object2ObjectOpenHashMap<UUID, AfkObject> afkList;

    public AfkManager() {
        afkList = new Object2ObjectOpenHashMap<>();
    }

    public void addPlayer(Player p) {
        afkList.putIfAbsent(p.getUniqueId(), new AfkObject(p.getLocation()));
    }

    public AfkObject getPlayer(Player p) {
        if (afkList.containsKey(p.getUniqueId()))
            return afkList.get(p.getUniqueId());
        else {
            addPlayer(p);
            return getPlayer(p);
        }
    }

    @Override
    public void run() {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if (GameManager.getInstance().contains(player.getUniqueId()))
                addPlayer(player);
        });
        Iterator<UUID> uuidIterator = afkList.keySet().iterator();
        while(uuidIterator.hasNext()) {
            UUID key = uuidIterator.next();
            Player target = Bukkit.getPlayer(key);
            if (target == null || !GameManager.getInstance().contains(key))
                uuidIterator.remove();
            else {
                AfkObject object = getPlayer(target);
                if (object.getLocation().equals(target.getLocation()))
                    object.setCheckTime(object.getCheckTime() - 1);
                else
                    object.resetValue(target.getLocation());
                if (object.getCheckTime() <= DataManager.getInstance().getWaitTime() / 2) {
                    if (object.getCheckTime() <= 0) {
                        //킥
                        GameManager.getInstance().removePlayer(target);
                        uuidIterator.remove();
                        MinimizeLogger.getInstance().appendLog(target.getName() + "님이 게임 참여중 잠수가 감지되어 강제 퇴장 처리됨");
                    }
                    else {
                        target.sendTitle("§f[ §6! §f] §4AFK Detected!", "§f잠수가 감지 되었습니다. §6" + object.getCheckTime() + "§f초 후에 강제 퇴장 처리됩니다.", 0, 25, 0);
                    }
                }
            }
        }
    }
}
