package org.light.dayz.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.dayz.data.Corpse;
import org.light.source.DeathMatch;

import java.util.ArrayList;
import java.util.UUID;

public class CorpseRegistry {

    private static ArrayList<Corpse> corpseArray = new ArrayList<>();
    public static ArrayList<UUID> findingPlayer = new ArrayList<>();

    public static boolean isCorpseExist(Player input) {
        for (Corpse corpse : corpseArray) {
            Player corpsePlayer = corpse.getEntity();
            if (corpsePlayer.equals(input))
                return true;
        }
        return false;
    }

    public static void addCorpse(Player victim, String reason, int remainTick) {
        Inventory corpseInventory = Bukkit.createInventory(null, 54, "§0" + victim.getName() + "의 시체");
        victim.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        if (victim.getInventory().getHelmet() != null && victim.getInventory().getHelmet().getType() != Material.PUMPKIN)
            victim.getInventory().setHelmet(new ItemStack(Material.AIR));
        for (ItemStack stack : victim.getInventory().getContents()) {
            if (stack != null)
                corpseInventory.addItem(stack);
        }
        corpseInventory.setItem(53, DayZItem.createItemStack(Material.NAME_TAG, "§6[ §f! §6] §c" + victim.getName() + "§f의 인식표", (short)0 ," ", " §8-  §f사유 §7: " + reason, " "));
        corpseArray.add(new Corpse(NPCMaker.createNPC(victim.getLocation(), victim.getName(), NPCMaker.NPCType.CORPSE, 1.0), corpseInventory, remainTick));
    }

    public static Corpse getCorpse(Player input) {
        for (Corpse corpse : corpseArray) {
            Player corpsePlayer = corpse.getEntity();
            if (corpsePlayer.equals(input))
                return corpse;
        }
        return null;
    }

    public static void activeTimer() {
        Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(DeathMatch.class), () -> {
            ArrayList<Corpse> removeEntry = new ArrayList<>();
            for (Corpse corpse : corpseArray) {
                corpse.setRemainTime(corpse.getRemainTime() - 1);
                if (!corpse.getEntity().isValid())
                    removeEntry.add(corpse);
                else if (corpse.getRemainTime() <= 0) {
                    corpse.remove();
                    removeEntry.add(corpse);
                }
            }
            corpseArray.removeAll(removeEntry);
        }, 0L, 1200L);
    }

    public static void removeAll() {
        for (Corpse corpse : corpseArray) {
            corpse.remove();
        }
        corpseArray.clear();
    }

}
