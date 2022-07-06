package org.light.dayz.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.dayz.data.DayZData;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.team.TeamData;
import org.light.dayz.util.DayZItem;
import org.light.dayz.util.Regen;
import org.light.source.Game.GameManager;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Singleton.CrackShotApi;
import org.light.source.Singleton.DataManager;
import org.light.source.Singleton.EconomyApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GameController {

    public static HashMap<UUID, DayZData> gameData = new HashMap<>();
    public static TeamData team = new TeamData();

    public static void openInv(Player p) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§8[ §f! §7] §0게임모드 선택");
        inventory.setItem(2, DayZItem.createItemStack(Material.DIAMOND_SWORD, "§6[ §f! §6] §bKSEC", (short)0, " ", " §8-  §f현재 가지고 있는 아이템으로 플레이 할 수 있는 모드입니다.", " "));
        if (Regen.isWeaponGet(p.getUniqueId()))
            inventory.setItem(6, DayZItem.createItemStack(Material.BARRIER, "§c[ §f! §c] §8SCAV", (short)0, " ", " §8-  §f랜덤한 아이템을 가진 상태로 플레이 가능한 모드입니다.", " §8-  §f20분에 한번씩 플레이가 가능하며, 참여시 가지고 있는 모든 아이템이 증발합니다.", " §8-  §f아직 참여하실 수 없습니다.", " "));
        else
            inventory.setItem(6, DayZItem.createItemStack(Material.WOODEN_SWORD, "§c[ §f! §c] §8SCAV", (short)0, " ", " §8-  §f랜덤한 아이템을 가진 상태로 플레이 가능한 모드입니다.", " §8-  §f20분에 한번씩 플레이가 가능하며, 참여시 가지고 있는 모든 아이템이 증발합니다.",  " "));
        p.openInventory(inventory);
    }

    public static void addPlayer(Player p) {
        if (!contains(p.getUniqueId()) && canStart() && !GameManager.getInstance().contains(p.getUniqueId()))
            openInv(p);
        else
            p.sendMessage("§c조건에 충족하지 않아 참여할 수 없습니다.");
    }

    public static void insertGame(Player p, boolean isScav){
        addData(p.getUniqueId());
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80.0);
        p.setHealthScaled(true);
        p.setHealth(80.0);
        p.setFoodLevel(20);
        if (p.getInventory().getItem(8) != null && p.getInventory().getItem(8).getType() == Material.PLAYER_HEAD)
            p.getInventory().setItem(8, new ItemStack(Material.AIR));
        p.setGameMode(GameMode.ADVENTURE);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 5, true, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 140, 3, true, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 140, 5, true, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 999999, 100, true, false));
        p.setLevel(0);
        p.setExp(0.0f);
        p.sendMessage(" ");
        if (isScav) {
            p.sendMessage( "§c현재 Scav모드로 플레이 중입니다. 기본 아이템이 지급되었습니다.");
            p.sendMessage(" ");
        }
        p.sendMessage("§f좀비와 다른 생존자들을 피해 아이템을 흭득하고");
        p.sendMessage("§f대치하며 탈출구§7(신호기, 우클릭 시 탈출 가능)§f로 탈출하세요");
        p.sendMessage("§b탈출 시 파밍 한 모든 아이템이 보존되며,");
        p.sendMessage("§b그 이외의 경우 아이템이 전부 드랍됩니다.");
        p.sendMessage("§c좀비나 생존자를 죽일 경우 포인트가 추가로 지급됩니다.");
        p.sendMessage("§7(단, 탈출 시 보상 흭득 가능)");
        p.sendMessage(" ");
        team.addPlayer(p);
        p.teleport(getRandomLocation());
        if (isScav) {
            int rand;
            p.getInventory().clear();
            rand = ThreadLocalRandom.current().nextInt(0, 3);
            if (rand == 0)
                p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            rand = ThreadLocalRandom.current().nextInt(0, 3);
            if (rand == 0)
                p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
            rand = ThreadLocalRandom.current().nextInt(0, 3);
            if (rand == 0)
                p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
            rand = ThreadLocalRandom.current().nextInt(0, 3);
            if (rand == 0)
                p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
            if (YamlConfig.instance.getHelpWeapon().size() != 0) {
                int i = ThreadLocalRandom.current().nextInt(0, YamlConfig.instance.getHelpWeapon().size());
                p.getInventory().setItemInMainHand(CrackShotApi.getCSWeapon(YamlConfig.instance.getHelpWeapon().get(i)));
            }
            Regen.kitMap.put(p.getUniqueId(), 0);
        }
    }
    public static void removePlayer(Player p, boolean alive) {
        if (contains(p.getUniqueId())) {
            DayZData data = getData(p.getUniqueId());
            gameData.remove(p.getUniqueId());
            team.removePlayer(p);
            new ArrayList<>(p.getActivePotionEffects()).forEach(pos -> p.removePotionEffect(pos.getType()));
            p.setLevel(0);
            p.setExp(0.0f);
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            p.setHealth(20.0);
            p.setFoodLevel(20);
            p.teleport(DataManager.getInstance().getLocation());
            p.setFireTicks(0);
            p.sendMessage(" ");
            p.sendMessage("§f《 레이드 결과 》");
            p.sendMessage(" ");
            p.sendMessage("§f플레이어 §4킬 §f총 " + data.getKill() + "§f회, 포인트 §6" + data.getAccumulateMoney() + "§f원");
            if (alive) {
                p.sendMessage("§f탈출에 성공하여 §6포인트§f와 아이템을 흭득하였습니다.");
                p.sendMessage(" ");
                EconomyApi.getInstance().giveMoney(p, data.getAccumulateMoney());
                MinimizeLogger.getInstance().appendLog(p.getName() + "님이 탈출하여 아이템과 " + data.getAccumulateMoney() + "원 흭득");
            }
            else {
                p.sendMessage("§f탈출에 §c실패§f하여 보상 흭득에 실패하였습니다.");
                MinimizeLogger.getInstance().appendLog(p.getName() + "님이 탈출 실패");
            }
        }
    }

    public static boolean contains(UUID data) {
        return gameData.containsKey(data);
    }

    public static DayZData getData(UUID data) {
        return gameData.get(data);
    }

    public static void addData(UUID data) {
        gameData.put(data, new DayZData());
    }

    public static int getSize() {
        return gameData.size();
    }

    public static boolean canStart() {
        return YamlConfig.instance.getLocations().size() != 0;
    }

    public static Location getRandomLocation() {
        int random = ThreadLocalRandom.current().nextInt(0, YamlConfig.instance.getLocations().size());
        return YamlConfig.instance.getLocations().get(random);
    }
}
