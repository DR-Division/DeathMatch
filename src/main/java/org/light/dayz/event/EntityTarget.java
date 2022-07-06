package org.light.dayz.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.light.dayz.util.ScavRegistry;

public class EntityTarget implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (ScavRegistry.isScavExist(player))
                event.setCancelled(true);
        }
    }
}
