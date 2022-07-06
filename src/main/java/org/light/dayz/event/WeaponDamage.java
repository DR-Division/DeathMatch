package org.light.dayz.event;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.dayz.data.DayZData;
import org.light.dayz.data.Scav;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.dayz.util.CorpseRegistry;
import org.light.dayz.util.DayZItem;
import org.light.dayz.util.ScavRegistry;
import org.light.source.DeathMatch;
import org.light.source.Singleton.CrackShotApi;

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
        if (world.getName().contains("dayz_")) {
            if (GameController.contains(shooter.getUniqueId()) && event.getVictim() instanceof Player && GameController.contains(event.getVictim().getUniqueId())) {
                Player victim = (Player) event.getVictim();
                if (shooter.hasPotionEffect(PotionEffectType.INVISIBILITY) || shooter.getUniqueId().equals(victim.getUniqueId()))
                    event.setCancelled(true);
                double distance = Math.round(shooter.getLocation().distance(victim.getLocation()) * 100.0) / 100.0;
                if (ThreadLocalRandom.current().nextInt(0, 100) < 2) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 1200, 1, true, false), true);
                    victim.sendMessage("§c[ §f! §c] §f총상으로 인한 §c출혈 §f상태가 되어 3초마다 피해를 입게 됩니다. 치료제를 사용하십시오.");
                }
                if (event.isHeadshot() && victim.getInventory().getHelmet() != null && victim.getInventory().getHelmet().getType().toString().contains("HELMET") && ThreadLocalRandom.current().nextInt(0, 10) <= 0) {
                    short dur = (short) (victim.getInventory().getHelmet().getDurability() / 2);
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50, 1, false, false), true);
                    victim.getInventory().getHelmet().setDurability(dur);
                    event.setDamage(0);
                }
                else if (event.getDamage() >= 35 && (CrackShotApi.getPlugin().getString(event.getWeaponTitle() + ".Shooting.Projectile_Subtype") != null || CrackShotApi.getPlugin().getBoolean(event.getWeaponTitle() + ".Explosions.Enable")))
                    event.setDamage(event.getDamage() / 2);
                if (victim.getHealth() - event.getDamage() <= 0) {
                    if (event.isHeadshot()) {
                        Bukkit.broadcastMessage("§c" + shooter.getName() + "§f의 " + event.getWeaponTitle() + "을(를) 사용한 헤드샷으로 §b" + victim.getName() + "§f이(가) 사망했습니다. (" + distance + "m)");
                        CorpseRegistry.addCorpse(victim, shooter.getName() + " ＝lニニフ " + victim.getName().replace(">", "") + " [ Head, Eyes ]", 3);
                    }
                    else {
                        Bukkit.broadcastMessage("§c" + shooter.getName() + "§f의 " + event.getWeaponTitle() + "(으)로 인해 §b" + victim.getName() + "§f이(가) 사망했습니다. (" + distance + "m)");
                        CorpseRegistry.addCorpse(victim, shooter.getName() + " ＝lニニフ " + victim.getName().replace(">", "") + " [ Chest ]", 3);
                    }
                    DayZData data = GameController.getData(shooter.getUniqueId());
                    data.setKill(data.getKill() + 1);
                    data.setAccumulateMoney(data.getAccumulateMoney() + config.getHKill());
                    shooter.sendActionBar("§c[ §f! §c] §4" + data.getKill() + "§f킬");
                    event.setCancelled(true);
                    victim.getInventory().clear();
                    GameController.removePlayer(victim, false);
                }
            }
        }
        if (event.getVictim() instanceof Skeleton) {
            Skeleton skeleton = (Skeleton) event.getVictim();
            if (!skeleton.hasAI()) {
                skeleton.setAI(true);
                skeleton.setTarget(shooter);
                shooter.sendMessage("§4[ §f! §4] §c위치를 자극하지 마십시오..");
            }
        }
        else if (event.getVictim() instanceof Player) {
            Player victim = (Player) event.getVictim();
            if (ScavRegistry.isScavExist(victim)) {
                Scav scav = ScavRegistry.getScav(victim);
                Player target = scav.getScavEntity().getBukkitEntity();
                if (ScavRegistry.rayTrace(target, target.getEyeLocation(), ScavRegistry.calcVector(target, shooter)))
                    CrackShotApi.getPlugin().fireProjectile(victim, CrackShotApi.getPlugin().returnParentNode(victim), false);
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        //그외 사유로 죽었을때
        Entity entity = event.getEntity();
        if (entity.getWorld().getName().contains("dayz_")) {
            if (entity instanceof Zombie) {
                if (entity instanceof PigZombie) {
                    PigZombie victim = (PigZombie) entity;
                    Player killer = victim.getKiller();
                    if (killer != null && GameController.contains(killer.getUniqueId())) {
                        ItemStack drop = new ItemStack(Material.ROTTEN_FLESH, ThreadLocalRandom.current().nextInt(1, 4));
                        int zKill = config.getZKill();
                        killer.sendActionBar("§f+ §6" + (zKill * 2) + " §f포인트!");
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
                        ItemStack drop = new ItemStack(Material.ROTTEN_FLESH, ThreadLocalRandom.current().nextInt(1, 2));
                        int zKill = config.getZKill();
                        killer.sendActionBar("§f+ §6" + zKill + " §f포인트!");
                        DayZData data = GameController.getData(killer.getUniqueId());
                        data.setAccumulateMoney(data.getAccumulateMoney() + zKill);
                        event.getDrops().clear();
                        entity.getWorld().dropItem(victim.getLocation(), drop);
                    }
                }
            }
            else if (entity instanceof Skeleton) {
                Skeleton victim = (Skeleton) entity;
                Player killer = victim.getKiller();
                if (killer != null && GameController.contains(killer.getUniqueId())) {
                    ItemStack drop = new ItemStack(Material.ROTTEN_FLESH, ThreadLocalRandom.current().nextInt(2, 5));
                    int zKill = config.getZKill();
                    killer.sendActionBar("§f+ §6" + zKill * 3 + " §f포인트!");
                    DayZData data = GameController.getData(killer.getUniqueId());
                    data.setAccumulateMoney(data.getAccumulateMoney() + zKill);
                    event.getDrops().clear();
                    entity.getWorld().dropItem(victim.getLocation(), drop);
                }
            }
            else if (entity instanceof Creeper) {
                Creeper victim = (Creeper) entity;
                Player killer = victim.getKiller();
                if (killer != null && GameController.contains(killer.getUniqueId())) {
                    ItemStack drop = new ItemStack(Material.ROTTEN_FLESH, ThreadLocalRandom.current().nextInt(1, 2));
                    int zKill = config.getZKill();
                    killer.sendActionBar("§f+ §6" + zKill * 2 + " §f포인트!");
                    DayZData data = GameController.getData(killer.getUniqueId());
                    data.setAccumulateMoney(data.getAccumulateMoney() + zKill);
                    event.getDrops().clear();
                    entity.getWorld().dropItem(victim.getLocation(), drop);
                }
            }
            else if (entity instanceof Spider) {
                Spider victim = (Spider) entity;
                Player killer = victim.getKiller();
                if (killer != null && GameController.contains(killer.getUniqueId())) {
                    ItemStack drop = new ItemStack(Material.ROTTEN_FLESH, ThreadLocalRandom.current().nextInt(2, 3));
                    int zKill = config.getZKill();
                    killer.sendActionBar("§f+ §6" + zKill * 2 + " §f포인트!");
                    DayZData data = GameController.getData(killer.getUniqueId());
                    data.setAccumulateMoney(data.getAccumulateMoney() + zKill);
                    event.getDrops().clear();
                    entity.getWorld().dropItem(victim.getLocation(), drop);
                }
            }
            else if (entity instanceof Player) {
                Player victim = (Player) entity;
                if (event instanceof PlayerDeathEvent) {
                    ((PlayerDeathEvent) event).setDeathMessage("");
                    Bukkit.broadcastMessage("§b" + victim.getName() + "§f이(가) 사망했습니다. [ " + entity.getLastDamageCause().getCause() + " ]");
                }
                if (GameController.contains(victim.getUniqueId())) {
                    event.getDrops().clear();
                    CorpseRegistry.addCorpse(victim, "Minecraft ＝lニニフ " + victim.getName() + " [ " + victim.getLastDamageCause().getCause() + " ]", 3);
                    victim.getInventory().clear();
                }
                else if (ScavRegistry.isScavExist(victim)) {
                    int coinRand = ThreadLocalRandom.current().nextInt(0,50);
                    if (ThreadLocalRandom.current().nextInt(0,1000) != 0)
                        victim.getInventory().clear();
                    if (coinRand == 0)
                        victim.getInventory().addItem(DayZItem.DOGE);
                    else if (coinRand < 5) {
                        ItemStack give = DayZItem.RIBBLE;
                        give.clone().setAmount(5);
                        victim.getInventory().addItem(give);
                    }
                    else if (coinRand <= 20)
                        victim.getInventory().addItem(DayZItem.RIBBLE);
                    else
                        victim.getInventory().addItem(DayZItem.BITO);

                    event.getDrops().clear();
                    CorpseRegistry.addCorpse(victim, "Minecraft ＝lニニフ " + victim.getName() + " [ " + victim.getLastDamageCause().getCause() + " ]", 3);
                    ScavRegistry.getScav(victim).remove();
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
        if (event.getEntity() instanceof Player && event.getEntity().getWorld().getName().contains("dayz_")) {
            Player p = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getFinalDamage() >= 7) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1200, 4, true, true), true);
                p.sendMessage("§c[ §f! §c] §f높은 곳에서 떨어져 골절 상태가 되었습니다. 치료제를 이용하여 상태이상을 제거할 수 있습니다.");
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            if (event.getDamager() instanceof Zombie) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                if (random.nextInt(0, 101) <= 1) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 1200, 1, true, false), true);
                    victim.sendMessage("§c[ §f! §c] §f좀비에게 물려 §c감염 §f상태가 되어 3초마다 피해를 입게 됩니다. 치료제를 사용하십시오.");
                }
            }
            else if (event.getDamager() instanceof Creeper) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                if (random.nextInt(0, 101) < 10) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 1200, 1, true, false), true);
                    victim.sendMessage("§c[ §f! §c] §f부머의 §a부식성 진액§f의 영향으로 추가 피해를 입게 됩니다. 치료제를 사용하십시오.");
                }
            }
            else if (event.getDamager() instanceof Skeleton) {
                event.setDamage(event.getDamage() * 2);
            }
        }
        else if (event.getEntity() instanceof Skeleton && event.getDamager() instanceof Player) {
            Skeleton skeleton = (Skeleton) event.getEntity();
            Player player = (Player) event.getDamager();
            if (!skeleton.hasAI())
                skeleton.setAI(true);
            skeleton.setTarget(player);
        }
    }

    @EventHandler
    public void onItemDrop(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.GOLD_NUGGET)
            event.setCancelled(true);
    }
}
