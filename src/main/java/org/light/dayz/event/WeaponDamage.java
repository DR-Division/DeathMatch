package org.light.dayz.event;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.dayz.data.DayZData;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.source.DeathMatch;

import java.util.concurrent.ThreadLocalRandom;

public class WeaponDamage implements Listener {

    private YamlConfig config;
    private DeathMatch Plugin;

    public WeaponDamage(YamlConfig config, DeathMatch Plugin) {
        this.config = config;
        this.Plugin = Plugin;
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent event) {
        World world = event.getPlayer().getWorld();
        Player shooter = event.getPlayer();
        if (world.getName().contains("dayz")) {
            if (GameController.contains(shooter.getUniqueId()) && event.getVictim() instanceof Player && GameController.contains(event.getVictim().getUniqueId())) {
                Player victim = (Player) event.getVictim();
                if (shooter.hasPotionEffect(PotionEffectType.INVISIBILITY) || shooter.getUniqueId().equals(victim.getUniqueId()))
                    event.setCancelled(true);
                double distance = Math.round(shooter.getLocation().distance(victim.getLocation()) * 100.0) / 100.0;
                if (victim.getHealth() - event.getDamage() <= 0) {
                    if (event.isHeadshot())
                        Bukkit.broadcastMessage("??c" + shooter.getName() + "??f??? " + event.getWeaponTitle() + "???(???) ????????? ??????????????? ??b" + victim.getName() + "??f???(???) ??????????????????. (" + distance + "m)");
                    else
                        Bukkit.broadcastMessage("??c" + shooter.getName() + "??f??? " + event.getWeaponTitle() + "(???)??? ?????? ??b" + victim.getName() + "??f???(???) ??????????????????. (" + distance + "m)");
                    DayZData data = GameController.getData(shooter.getUniqueId());
                    data.setKill(data.getKill() + 1);
                    data.setAccumulateMoney(data.getAccumulateMoney() + config.getHKill());
                    shooter.sendActionBar("??c[ ??f! ??c] ??4" + data.getKill() + "??f???");
                    event.setCancelled(true);
                    for (ItemStack stack : victim.getInventory().getContents())
                        if (stack != null && !stack.equals(victim.getInventory().getItemInOffHand()))
                            world.dropItem(victim.getLocation(), stack);
                    victim.getInventory().clear();
                    GameController.removePlayer(victim, false);
                }
            }
            else if (event.getVictim() instanceof Player)
                event.setDamage(0);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        //?????? ????????? ????????????
        Entity entity = event.getEntity();
        if (entity.getWorld().getName().contains("dayz")) {
            if (entity instanceof Zombie) {
                if (entity instanceof PigZombie) {
                    PigZombie victim = (PigZombie) entity;
                    Player killer = victim.getKiller();
                    if (killer != null && GameController.contains(killer.getUniqueId())) {
                        ItemStack drop = new ItemStack(Material.ROTTEN_FLESH, ThreadLocalRandom.current().nextInt(0,4));
                        int zKill = config.getZKill();
                        killer.sendActionBar("??f+ ??6" + (zKill * 2) + "??f?????????!");
                        DayZData data = GameController.getData(killer.getUniqueId());
                        data.setAccumulateMoney(data.getAccumulateMoney() + zKill);
                        event.getDrops().clear();
                        entity.getWorld().dropItem(victim.getLocation(), drop);
                    }
                }
                else {
                    Zombie victim = (Zombie) entity;
                    Player killer = victim.getKiller();
                    if (killer != null && GameController.contains(killer.getUniqueId())) {
                        int zKill = config.getZKill();
                        killer.sendActionBar("??f+ ??6" + zKill + "??f?????????!");
                        DayZData data = GameController.getData(killer.getUniqueId());
                        data.setAccumulateMoney(data.getAccumulateMoney() + zKill);
                    }
                }
            }
            else if (entity instanceof Player) {
                Player victim = (Player) entity;
                if (GameController.contains(victim.getUniqueId())) {
                    for (ItemStack stack : victim.getInventory().getContents())
                        if (stack != null && !stack.equals(victim.getInventory().getItemInOffHand()))
                            victim.getWorld().dropItem(victim.getLocation(), stack);

                    victim.getInventory().clear();
                    victim.spigot().respawn();
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
            if (GameController.contains(event.getPlayer().getUniqueId())) {
                GameController.removePlayer(event.getPlayer(), false);
            }
        }, 1L);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity().getWorld().getName().contains("dayz")) {
            Player p = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getFinalDamage() >= 7) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1200, 4, true, true), true);
                p.sendMessage("??c[ ??f! ??c] ??f??????????????? ????????? ??????????????? ???????????????. ???????????? ???????????? ??????????????? ????????? ??? ????????????.");
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Zombie && event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (random.nextInt(0, 101) == 0) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 1200, 1, true, false), true);
                victim.sendMessage("??c[ ??f! ??c] ??f???????????? ?????? ??c????????f????????? ?????? 3????????? ???????????? ?????? ?????????. ???????????? ??????????????????");
            }
        }
    }
}
