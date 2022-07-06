package org.light.dayz.data;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.game.GameController;
import org.light.dayz.util.ScavRegistry;
import org.light.source.Command.CommandController;

import java.util.Objects;

public class Scav {

    private String name;
    private String weaponID;
    private double health;
    private ItemStack[] equipment;
    private Location spawnLoc;
    private EntityPlayer scavEntity;

    public Scav(String name, String weaponID, Location loc, double health, ItemStack helmet, ItemStack chest, ItemStack leggings, ItemStack boots) {
        //equipment null -> leather
        this.equipment = new ItemStack[4];
        this.name = name;
        this.weaponID = weaponID;
        this.health = health;
        this.spawnLoc = loc;
        equipment[0] = helmet == null ? new ItemStack(Material.LEATHER_HELMET) : helmet;
        equipment[1] = chest == null ? new ItemStack(Material.LEATHER_CHESTPLATE) : chest;
        equipment[2] = leggings == null ? new ItemStack(Material.LEATHER_LEGGINGS) : leggings;
        equipment[3] = boots == null ? new ItemStack(Material.LEATHER_BOOTS) : boots;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeaponID() {
        return weaponID;
    }

    public void setWeaponID(String weaponID) {
        this.weaponID = weaponID;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public ItemStack[] getEquipment() {
        return equipment;
    }

    public void setEquipment(ItemStack[] equipment) {
        this.equipment = equipment;
    }

    public Location getSpawnLoc() {
        return spawnLoc;
    }

    public void setSpawnLoc(Location spawnLoc) {
        this.spawnLoc = spawnLoc;
    }

    public void remove() {
        if (scavEntity != null) {
            scavEntity.die();
            scavEntity = null;
        }
    }

    public String locationToString(Location loc) {
        return "§7[ §fX : " + Math.round(loc.getX()) + ", Y : " + Math.round(loc.getY()) + ", Z : " + Math.round(loc.getZ()) + ", World : " + loc.getWorld().getName() + " §7]";
    }

    public String showEquipment() {
        return (equipment[0] != null ? equipment[0].getType() : "NONE") + " " + (equipment[1] != null ? equipment[1].getType() : "NONE") + " "  + (equipment[2] != null ? equipment[2].getType() : "NONE") + " "  + (equipment[3] != null ? equipment[3].getType() : "NONE") + " ";
    }

    public EntityPlayer getScavEntity() {
        return scavEntity;
    }

    public void setScavEntity(EntityPlayer scavEntity) {
        this.scavEntity = scavEntity;
    }

    @Override
    public String toString() {
        return "§f[ " + name + " ] " + weaponID + " " + health + " " + locationToString(spawnLoc) + " " + showEquipment();
    }
}
