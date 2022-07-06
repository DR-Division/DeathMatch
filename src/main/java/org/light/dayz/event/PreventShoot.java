package org.light.dayz.event;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponPreShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.game.GameController;
import org.light.source.Singleton.CrackShotApi;

public class PreventShoot implements Listener {

    @EventHandler
    public void onShoot(WeaponPreShootEvent event) {
        Player p = event.getPlayer();
        if ((p.getWorld().getName().contains("lobby") && !p.isOp()) || !canUseWeapon(p))
            event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onShieldDamage(WeaponDamageEntityEvent event) {
        if (event.getVictim() instanceof Player) {
            Player victim = (Player) event.getVictim();
            String title = CrackShotApi.getCSID(victim.getInventory().getItemInMainHand());
            if (title != null && CrackShotApi.getPlugin().getBoolean(title + ".Riot_Shield.Enable") && !canUseWeapon(victim)){
                event.setDamage(5);
                victim.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (GameController.contains(event.getPlayer().getUniqueId())) {
            for (ItemStack stack : event.getPlayer().getInventory().getContents())
                if (stack != null && !stack.equals(event.getPlayer().getInventory().getItemInOffHand()))
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), stack);
            event.getPlayer().getInventory().clear();
            GameController.removePlayer(event.getPlayer(), false);
        }
    }

    public boolean canUseWeapon(Player p) {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = p.getInventory().getItem(i);
            if (stack != null && stack.getType() != Material.AIR && CrackShotApi.getCSID(stack) != null)
                count++;
        }
        if (count >= 4) {
            p.sendTitle("§4[ §c! §4]","§c무기를 4개이상 사용할 수 없습니다. (방패의 경우 피격시 삭제됩니다.)", 0, 24, 0);
            return false;
        }
        return true;
    }
}
