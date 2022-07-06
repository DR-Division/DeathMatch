package org.light.dayz.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnMob implements Listener {


    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().clear();
        if (event.getEntity() instanceof Creeper) {
            Creeper creeper = (Creeper) event.getEntity();
            creeper.removePotionEffect(PotionEffectType.SPEED);
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();

        if (world.getName().contains("dayz_") && event.getEntityType() != EntityType.ZOMBIE && event.getEntityType() != EntityType.PIG_ZOMBIE && event.getEntityType() != EntityType.CREEPER && event.getEntityType() != EntityType.ARMOR_STAND && event.getEntityType() != EntityType.SKELETON && event.getEntityType() != EntityType.SPIDER) {
            event.setCancelled(true);
            int innerRand;
            innerRand = ThreadLocalRandom.current().nextInt(0, 10);
                if (innerRand == 0)
                    world.spawnEntity(event.getLocation(), EntityType.SKELETON);
                else if (innerRand < 1)
                    world.spawnEntity(event.getLocation(), EntityType.PIG_ZOMBIE);
                else if (innerRand < 2)
                    world.spawnEntity(event.getLocation(), EntityType.CREEPER);
                else if (innerRand < 3)
                    world.spawnEntity(event.getLocation(), EntityType.SPIDER);
                else
                    world.spawnEntity(event.getLocation(), EntityType.ZOMBIE);
        }
        else if (event.getEntityType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getEntity();
            if (zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue() != 40.0) {
                zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(40.0);
                zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
                zombie.setHealth(20.0);
                zombie.getEquipment().clear();
                zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                zombie.setSilent(true);
                zombie.setBaby(false);
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, true, false), false);

            }

        }
        else if (event.getEntityType() == EntityType.PIG_ZOMBIE) {
            PigZombie zombie = (PigZombie) event.getEntity();
            if (zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue() != 60.0) {
                zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(60.0);
                zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60.0);
                zombie.setHealth(60.0);
                zombie.setBaby(false);
                zombie.getEquipment().clear();
                zombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
                zombie.setSilent(true);
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, true, false), false);
            }
        }
        else if (event.getEntityType() == EntityType.CREEPER) {
            Creeper creeper = (Creeper) event.getEntity();
            if (creeper.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue() != 50.0) {
                creeper.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50.0);
                creeper.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50.0);
                creeper.setHealth(50.0);
                creeper.getEquipment().clear();
                creeper.setSilent(true);
                creeper.setMaxFuseTicks(15);
                creeper.setExplosionRadius(3);
                creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false), false);
            }
        }
        else if (event.getEntityType() == EntityType.SKELETON) {
            Skeleton skeleton = (Skeleton) event.getEntity();
            if (skeleton.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue() != 100.0) {
                skeleton.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100.0);
                skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80.0);
                skeleton.setHealth(80.0);
                skeleton.setAI(false);
                skeleton.getEquipment().clear();
                skeleton.setSilent(true);
                skeleton.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 4, true, false), false);
            }
        }
        else if (event.getEntityType() == EntityType.SPIDER) {
            Spider spider = (Spider) event.getEntity();
            if (spider.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue() != 50.0) {
                spider.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50.0);
                spider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                spider.setHealth(40.0);
                spider.getEquipment().clear();
                spider.setSilent(true);
                spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false), false);
                spider.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 1, true, false), true);
            }
        }
    }
}
