package org.light.dayz.event;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevel implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onFood(FoodLevelChangeEvent event) {
        World world = event.getEntity().getWorld();
        if (world.getName().contains("dayz_"))
            event.setCancelled(false);
    }

    @EventHandler
    public void onFoodDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
            event.setDamage(event.getDamage() * 4);
            event.getEntity().sendMessage("§6[ §f! §6] §c배고픔에 의해 피해를 받고 있습니다. 음식을 섭취하여 해결할 수 있습니다.");
        }
    }
}
