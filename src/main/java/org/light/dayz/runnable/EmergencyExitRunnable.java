package org.light.dayz.runnable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.dayz.event.ChestInteraction;
import org.light.dayz.game.GameController;

import java.util.HashMap;
import java.util.UUID;

public class EmergencyExitRunnable extends ExitRunnable {

    private int count;

    public EmergencyExitRunnable(Location start, UUID data) {
        super(start, data, new HashMap<>());
        count = 0;
    }

    @Override
    public void run() {
        Player p = Bukkit.getPlayer(data);
        if (p == null || !GameController.contains(p.getUniqueId()) || !p.getWorld().getName().contains("dayz_") || (p.getWorld().getName().contains("dayz_") && p.getLocation().distance(start) >= 10.0 )) {
            cancel();
            if (p != null && p.getWorld().getName().contains("dayz_") &&  p.getLocation().distance(start) >= 10.0)
                p.sendMessage("§c[ §f! §c] §c탈출 위치로부터 10m 이상 떨어져 탈출이 취소되었습니다.");
        }
        else {
            if (count >= 20) {
                GameController.removePlayer(p, true);
                cancel();
            }
            else
                p.sendTitle("§c[ §f! §c] §b탈출!", "§6" + (20 - count) + "§f초 후 탈출합니다.", 0, 25, 0);
            count++;
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        ChestInteraction.exitTime.remove(data);
    }
}
