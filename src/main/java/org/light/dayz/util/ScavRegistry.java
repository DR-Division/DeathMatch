package org.light.dayz.util;

import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.light.dayz.data.Corpse;
import org.light.dayz.data.Scav;
import org.light.source.DeathMatch;
import org.light.source.Singleton.CrackShotApi;

import java.util.ArrayList;

public class ScavRegistry {

    public static ArrayList<Scav> scavArrayList = new ArrayList<>();

    public static boolean isScavExist(Player input) {
        for (Scav scav : scavArrayList) {
            if (scav.getName().equalsIgnoreCase(input.getName()))
                return true;
        }
        return false;
    }

    public static EntityPlayer getScavEntity(Scav input) {
        return input.getScavEntity();

    }

    public static boolean isScavSpawn(Scav input) {
        return input.getScavEntity() != null;
    }

    public static void addScav(String name, String weaponID, Location loc, double health, ItemStack helmet, ItemStack chest, ItemStack leggings, ItemStack boots) {
        removeScav(name);
        scavArrayList.add(new Scav(name, weaponID, loc, health, helmet, chest, leggings, boots));
    }

    public static void removeScav(String name) {
        ArrayList<Scav> find = new ArrayList<>();
        for (Scav scav : scavArrayList) {
            if (scav.getName().equalsIgnoreCase(name)) {
                find.add(scav);
                scav.remove();
            }
        }
        scavArrayList.removeAll(find);
    }

    public static Scav getScav(Player input) {
        for (Scav scav : scavArrayList) {
            if (scav.getName().equalsIgnoreCase(input.getName()))
                return scav;
        }
        return null;
    }

    public static void spawnScavs() {
        for (Scav scav : scavArrayList) {
            if (!isScavSpawn(scav)) {
                scav.setScavEntity(NPCMaker.createNPC(scav.getSpawnLoc(), scav.getName(), NPCMaker.NPCType.NORMAL, scav.getHealth(), CrackShotApi.getCSWeapon(scav.getWeaponID()), scav.getEquipment()[0], scav.getEquipment()[1], scav.getEquipment()[2], scav.getEquipment()[3]));

            }
        }
    }

    public static void active() {
        Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(DeathMatch.class), () -> {
            for (Scav scav : scavArrayList) {
                if (isScavSpawn(scav)) {
                    int viewMult = Bukkit.getViewDistance() * Bukkit.getViewDistance();
                    EntityPlayer ep = getScavEntity(scav);
                    if (!ep.isAlive()) {
                        getScav(ep.getBukkitEntity()).remove();
                        continue;
                    }
                    Player sc = ep.getBukkitEntity();
                    Entity target = null;
                    for (Entity entity : sc.getWorld().getNearbyEntities(sc.getLocation(), viewMult * viewMult, viewMult * viewMult, viewMult * viewMult)) {
                        if (entity instanceof Player && Bukkit.getPlayer(entity.getUniqueId()) == null)
                            continue;
                        if (entity == sc || !rayTrace(entity, sc.getEyeLocation(), calcVector(sc, entity)))
                            continue;
                        if (target == null || (!(target instanceof Player) && entity instanceof Player) || sc.getLocation().distance(target.getLocation()) > sc.getLocation().distance(entity.getLocation()))
                            target = entity;
                    }
                    if (target == null)
                        continue;

                    try {
                        Location result = sc.getLocation();
                        Vector dir = calcVector(sc, target);
                        result.setDirection(dir);
                        sc.teleport(result);
                        look(ep, result.getYaw(), result.getPitch());
                        CrackShotApi.getPlugin().fireProjectile(sc, CrackShotApi.getPlugin().returnParentNode(sc), false);
                    }
                    catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    }
                }
            }
        }, 0L, 1L);
    }

    public static boolean rayTrace(Entity entity, Location data, Vector direction) {
        Location taskLoc = data.clone().add(direction);
        double distance = data.distance(entity.getLocation());
        if (distance > 1)
            distance--;
        //World객체내에있는 RayTrace 사용하여 보스가 해당 위치로 총을 발사 할 수 있는지 없는지 판단하는 코드, 왜 rayTrace메소드가 사라졌는지 모르겠어서 주석처리하빈다.
        /*RayTraceResult result = data.getWorld().rayTraceBlocks(taskLoc, direction, distance );
        return result == null || result.getHitBlock() == null;*/
        return true;


    }

    public static Vector calcVector(Entity shooter, Entity victim) {
        return victim.getLocation().subtract(shooter.getLocation()).toVector().normalize();
    }
    private static void look(EntityPlayer entity, float yaw, float pitch) {
        yaw = clampYaw(yaw);
        entity.yaw = yaw;
        setHeadYaw(entity, yaw);
        entity.pitch = pitch;
    }

    private static float clampYaw(float yaw) {
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }

        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }
        return yaw;
    }

    private static void setHeadYaw(EntityPlayer entity, float yaw) {
        EntityLiving handle = entity;
        yaw = clampYaw(yaw);
        handle.aB = yaw;
        handle.setHeadRotation(yaw);
    }

    public static void removeAll() {
        for (Scav scav : scavArrayList) {
            scav.remove();
        }
        scavArrayList.clear();
    }

}
