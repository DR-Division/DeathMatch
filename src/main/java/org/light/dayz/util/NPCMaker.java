package org.light.dayz.util;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.light.source.DeathMatch;

import javax.annotation.Nullable;
import java.util.*;

public class NPCMaker {

    public static ArrayList<EntityPlayer> npcList = new ArrayList<>();
    private static HashMap<EntityPlayer, ArrayList<UUID>> trackMap = new HashMap<>();

    public enum NPCType {
        CORPSE,
        NORMAL
    }


    public static EntityPlayer createNPC(Location location, String name, NPCType type, double health) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer ep = actualNPCCreate(location, name, type);
        ep.getBukkitEntity().setMaxHealth(health);
        ep.setHealth((float) health);
        npcList.add(ep);
        worldServer.addEntity(ep);
        renewNPC(ep, type, null);
        return ep;

    }

    public static EntityPlayer createNPC(Location location, String name, NPCType type, double health, org.bukkit.inventory.ItemStack mainHand) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer ep = actualNPCCreate(location, name, type);
        ep.getBukkitEntity().setMaxHealth(health);
        ep.setHealth((float) health);
        npcList.add(ep);
        ep.getBukkitEntity().getInventory().setItemInMainHand(mainHand);
        worldServer.addEntity(ep);
        ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> pair = new ArrayList<>();
        pair.add(new Pair(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(mainHand)));
        renewNPC(ep, type, pair);
        return ep;
    }

    public static EntityPlayer createNPC(Location location, String name, NPCType type, double health, org.bukkit.inventory.ItemStack mainHand, org.bukkit.inventory.ItemStack head) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer ep = actualNPCCreate(location, name, type);
        ep.getBukkitEntity().setMaxHealth(health);
        ep.setHealth((float) health);
        npcList.add(ep);
        ep.getBukkitEntity().getInventory().setItemInMainHand(mainHand);
        ep.getBukkitEntity().getInventory().setHelmet(head);
        worldServer.addEntity(ep);
        ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> pair = new ArrayList<>();
        pair.add(new Pair(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(mainHand)));
        pair.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(head)));
        renewNPC(ep, type, pair);
        return ep;

    }

    public static EntityPlayer createNPC(Location location, String name, NPCType type, double health, org.bukkit.inventory.ItemStack mainHand, org.bukkit.inventory.ItemStack head, org.bukkit.inventory.ItemStack chest) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer ep = actualNPCCreate(location, name, type);
        ep.getBukkitEntity().setMaxHealth(health);
        ep.setHealth((float) health);
        npcList.add(ep);
        ep.getBukkitEntity().getInventory().setItemInMainHand(mainHand);
        ep.getBukkitEntity().getInventory().setHelmet(head);
        ep.getBukkitEntity().getInventory().setChestplate(chest);
        worldServer.addEntity(ep);
        ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> pair = new ArrayList<>();
        pair.add(new Pair(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(mainHand)));
        pair.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(head)));
        pair.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(chest)));
        renewNPC(ep, type, pair);
        return ep;

    }

    public static EntityPlayer createNPC(Location location, String name, NPCType type, double health, org.bukkit.inventory.ItemStack mainHand, org.bukkit.inventory.ItemStack head, org.bukkit.inventory.ItemStack chest, org.bukkit.inventory.ItemStack leggings) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer ep = actualNPCCreate(location, name, type);
        ep.getBukkitEntity().setMaxHealth(health);
        ep.setHealth((float) health);
        npcList.add(ep);
        ep.getBukkitEntity().getInventory().setItemInMainHand(mainHand);
        ep.getBukkitEntity().getInventory().setHelmet(head);
        ep.getBukkitEntity().getInventory().setChestplate(chest);
        ep.getBukkitEntity().getInventory().setLeggings(leggings);
        worldServer.addEntity(ep);
        ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> pair = new ArrayList<>();
        pair.add(new Pair(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(mainHand)));
        pair.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(head)));
        pair.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(chest)));
        pair.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(leggings)));
        renewNPC(ep, type, pair);
        return ep;

    }

    public static EntityPlayer createNPC(Location location, String name, NPCType type, double health, org.bukkit.inventory.ItemStack mainHand, org.bukkit.inventory.ItemStack head, org.bukkit.inventory.ItemStack chest, org.bukkit.inventory.ItemStack leggings, org.bukkit.inventory.ItemStack boots) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer ep = actualNPCCreate(location, name, type);
        ep.getBukkitEntity().setMaxHealth(health);
        ep.setHealth((float) health);
        npcList.add(ep);
        ep.getBukkitEntity().getInventory().setItemInMainHand(mainHand);
        ep.getBukkitEntity().getInventory().setHelmet(head);
        ep.getBukkitEntity().getInventory().setChestplate(chest);
        ep.getBukkitEntity().getInventory().setLeggings(leggings);
        ep.getBukkitEntity().getInventory().setBoots(boots);
        worldServer.addEntity(ep);
        ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> pair = new ArrayList<>();
        pair.add(new Pair(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(mainHand)));
        pair.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(head)));
        pair.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(chest)));
        pair.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(leggings)));
        pair.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(boots)));
        renewNPC(ep, type, pair);
        return ep;

    }

    private static EntityPlayer actualNPCCreate(Location location, String name, NPCType type) {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile gameProfile;
        if (name.length() >= 15)
            gameProfile = new GameProfile(UUID.randomUUID(), name.substring(0, 14));
        else
            gameProfile = new GameProfile(UUID.randomUUID(), name);
        EntityPlayer entityPlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile, new PlayerInteractManager(worldServer));
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entityPlayer.playerConnection = new PlayerConnection(minecraftServer, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), entityPlayer);
        if (type == NPCType.CORPSE) {
            entityPlayer.getDataWatcher().set(DataWatcherRegistry.s.a(6), EntityPose.SLEEPING);
            entityPlayer.getBukkitEntity().setGameMode(GameMode.CREATIVE);
            entityPlayer.getBukkitEntity().setCollidable(false);
        }
        else {
            entityPlayer.getBukkitEntity().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999999, 3, true, false), true);
        }
        trackMap.put(entityPlayer, new ArrayList<>());
        return entityPlayer;
    }

    private static void renewNPC(EntityPlayer ep, NPCType type, @Nullable ArrayList<Pair<EnumItemSlot, ItemStack>> pair) {
        for (Entity all : ep.getBukkitEntity().getWorld().getNearbyEntities(ep.getBukkitEntity().getLocation(), Bukkit.getViewDistance() * Bukkit.getViewDistance() / 2, Bukkit.getViewDistance() * Bukkit.getViewDistance() / 2, Bukkit.getViewDistance() * Bukkit.getViewDistance() / 2)) {
            if (all instanceof Player) {
                trackMap.get(ep).add(all.getUniqueId());
                ((CraftPlayer) all).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep));
                ((CraftPlayer) all).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(ep));
                if (pair != null)
                    ((CraftPlayer) all).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(ep.getId(), pair));
                if (type == NPCType.CORPSE)
                    ((CraftPlayer) all).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(ep.getId(), ep.getDataWatcher(), true));
                ((CraftPlayer) all).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep));
            }
        }
    }

    private static void renewNPC(Player p, EntityPlayer ep, NPCType type, @Nullable ArrayList<Pair<EnumItemSlot, ItemStack>> pair) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep));
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(ep));
        if (pair != null && pair.size() != 0)
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(ep.getId(), pair));
        if (type == NPCType.CORPSE)
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(ep.getId(), ep.getDataWatcher(), true));
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep));

    }

    public static void removeEntity(EntityPlayer ep) {
        npcList.remove(ep);
    }

    public static void startTick() {
        Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(DeathMatch.class), () -> {
            ArrayList<EntityPlayer> removeMap = new ArrayList<>();
            ArrayList<UUID> trackedMap = new ArrayList<>();
            for (EntityPlayer ep : npcList) {
                trackedMap.clear();
                if (!ep.isAlive())
                    removeMap.add(ep);
                else {
                    for (Entity entity : ep.getBukkitEntity().getWorld().getNearbyEntities(ep.getBukkitEntity().getLocation(), Bukkit.getViewDistance() * Bukkit.getViewDistance(), Bukkit.getViewDistance() * Bukkit.getViewDistance(), Bukkit.getViewDistance() * Bukkit.getViewDistance())) {
                        if (entity instanceof Player) {
                            Player p = (Player) entity;
                            if (!trackMap.get(ep).contains(p.getUniqueId()) && Bukkit.getPlayer(p.getUniqueId()) != null) {
                                trackedMap.add(p.getUniqueId());
                                if (ep.getDataWatcher().get(DataWatcherRegistry.s.a(6)) == EntityPose.SLEEPING)
                                    renewNPC(p, ep, NPCType.CORPSE, pairByEntity(ep));
                                else
                                    renewNPC(p, ep, NPCType.NORMAL, pairByEntity(ep));
                            }
                        }
                        trackMap.put(ep, new ArrayList<>((ArrayList<UUID>) trackedMap.clone()));
                    }
                }
            }
            npcList.removeAll(removeMap);
        }, 0L, 20L);
    }

    public static ArrayList<Pair<EnumItemSlot, ItemStack>> pairByEntity(EntityPlayer ep) {
        ArrayList<Pair<EnumItemSlot, ItemStack>> result = new ArrayList<>();
        Player p = ep.getBukkitEntity();
        if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() != Material.AIR)
            result.add(new Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand())));
        if (p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR)
            result.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(p.getInventory().getHelmet())));
        if (p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() != Material.AIR)
            result.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(p.getInventory().getChestplate())));
        if (p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() != Material.AIR)
            result.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(p.getInventory().getLeggings())));
        if (p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() != Material.AIR)
            result.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(p.getInventory().getBoots())));
        return result;
    }
}
