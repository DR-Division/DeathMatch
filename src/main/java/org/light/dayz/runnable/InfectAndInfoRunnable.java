package org.light.dayz.runnable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.dayz.game.GameController;

import java.util.UUID;

public class InfectAndInfoRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for (UUID data : GameController.gameData.keySet()) {
            Player p = Bukkit.getPlayer(data);
            if (p != null) {
                if (p.hasPotionEffect(PotionEffectType.LUCK))
                    p.damage(2.0);
            }
        }
    }
}
