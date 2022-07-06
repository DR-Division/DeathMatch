package org.light.dayz.runnable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.dayz.game.GameController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExitRunnable extends BukkitRunnable {

    public HashMap<UUID, Integer> exitMap;
    public UUID data;
    public Location start;

    public ExitRunnable(Location start, UUID data, HashMap<UUID, Integer> map) {
        this.exitMap = map;
        this.data = data;
        this.start = start;
    }

    @Override
    public void run() {
        Player p = Bukkit.getPlayer(data);
        int value = exitMap.get(data);
        if (p == null || !GameController.contains(p.getUniqueId()) || !p.getWorld().getName().contains("dayz_") || p.getLocation().distance(start) >= 5.0 ) {
            exitMap.remove(data);
            cancel();
            if (p != null && p.getLocation().distance(start) >= 5.0) {
                p.sendMessage("§c[ §f! §c] §c탈출 위치로부터 5m 이상 떨어져 탈출이 취소되었습니다.");

            }
        }
        else {
            if (value >= 10) {
                GameController.removePlayer(p, true);
                exitMap.remove(p.getUniqueId());
                cancel();
            }
            else {
                p.sendTitle("§c[ §f! §c] §b탈출!", "§6" + (10 - value) + "§f초 후 탈출합니다.", 0, 25, 0);
                exitMap.put(data, value + 1);
            }
        }

    }

}
