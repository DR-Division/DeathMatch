package org.light.dayz.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.DeathMatch;

public class ArmorStandInteraction implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInteractArmorStand(PlayerInteractAtEntityEvent event) {
        Player p = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() == Material.NAME_TAG) {
            p.sendMessage("§c이름 바꾸지 마세요..");
            if (entity.getCustomName() == null)
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(DeathMatch.class), () -> entity.setCustomName(null), 1L);
            else {
                String oldName = entity.getCustomName();
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(DeathMatch.class), () -> entity.setCustomName(oldName), 1L);
            }
        }
        if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            if (entity.getCustomName() != null) {
                if (entity.getCustomName().contains("상점"))
                    Bukkit.getServer().dispatchCommand(p, "상점");
                else if (entity.getCustomName().contains("창고"))
                    Bukkit.getServer().dispatchCommand(p, "창고");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onManipulate(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(!(event.getPlayer().isOp() && event.getPlayer().isSneaking()));
    }
}
