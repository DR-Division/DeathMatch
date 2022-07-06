package org.light.dayz.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.dayz.runnable.ExitRunnable;
import org.light.dayz.util.CorpseRegistry;
import org.light.dayz.util.DayZItem;
import org.light.dayz.util.Regen;
import org.light.source.DeathMatch;
import org.light.source.Singleton.CrackShotApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ChestInteraction implements Listener {

    public static HashMap<UUID, Integer> exitTime;
    private DeathMatch Plugin;
    private HashMap<Location, CheckState> stateMap;
    private HashMap<Location, Integer> timeMap;
    private HashMap<Location, Integer> taskID;
    private HashMap<Location, ArrayList<UUID>> searchPlayers;
    private int remainTime;

    public ChestInteraction(DeathMatch Plugin) {
        exitTime = new HashMap<>();
        this.Plugin = Plugin;
        stateMap = new HashMap<>();
        this.remainTime = 3;
        this.searchPlayers = new HashMap<>();
        taskID = new HashMap<>();
        timeMap = new HashMap<>();
    }

    private enum CheckState {
        NONE,
        DOING,
        DONE
    }
    @EventHandler
    public void onFinding(PlayerToggleSneakEvent event) {
        for (Entity entity : event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 2,2,2)) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                if (CorpseRegistry.isCorpseExist(p) && !CorpseRegistry.findingPlayer.contains(event.getPlayer().getUniqueId())) {
                    CorpseRegistry.getCorpse(p).openInventory(event.getPlayer());
                    return;
                }
            }
        }
    }

    private boolean isSearching(UUID data) {
        for (Location loc : searchPlayers.keySet()) {
            for (UUID uid : searchPlayers.get(loc))
                if (data.equals(uid))
                    return true;
        }
        return false;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getWorld().getName().contains("dayz_")
                && GameController.contains(p.getUniqueId()) && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST) {
            Location location = event.getClickedBlock().getLocation(), regenChest = checkChest(location);
            if (regenChest != null && Regen.chestRegen.get(regenChest) != null) {
                int amount = YamlConfig.instance.getRegen() - Regen.chestRegen.get(regenChest);
                if (amount == 0)
                    p.sendMessage("§c[ §f! §c] §f이미 누군가가 열어본 상자입니다. §b곧 리젠됩니다.");
                else
                    p.sendMessage("§c[ §f! §c] §f이미 누군가가 열어본 상자입니다. §b" + amount / 2 + "분 " + (amount % 2 == 0 ? 0 : 30) + "초 후 리젠됩니다.");
            }
            else {
                event.setCancelled(true);
                searchPlayers.putIfAbsent(regenChest, new ArrayList<>());
                stateMap.putIfAbsent(regenChest, CheckState.NONE);
                timeMap.putIfAbsent(regenChest, 0);
                taskID.putIfAbsent(regenChest, -1);
                if (!isSearching(p.getUniqueId()))
                    openInventory(p, regenChest);
            }
        }
    }

    private Location checkChest(Location location) {
        Location[] arrays = new Location[]{location, location.clone().subtract(1, 0, 0),
                location.clone().add(1, 0, 0), location.clone().subtract(0, 0, 1),
                location.clone().add(0, 0, 1)};
        Location result = null;
        for (Location _temp : arrays) {
            if (_temp.getBlock().getType() == Material.CHEST)
                result = _temp;
        }
        if (result == null || (Regen.chestRegen.containsKey(location) && !Regen.chestRegen.containsKey(result)))
            return location;
        return result;
    }

    @EventHandler
    public void exit(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getWorld().getName().contains("dayz_") && GameController.contains(p.getUniqueId()) && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.BEACON) {
            event.setCancelled(true);
            if (!exitTime.containsKey(p.getUniqueId())) {
                exitTime.put(p.getUniqueId(), 0);
                new ExitRunnable(p.getLocation(), p.getUniqueId(), exitTime).runTaskTimer(Plugin, 0L, 20L);
            }
        }
    }

    public void openInventory(Player p, Location loc) {
        CheckState state = stateMap.get(loc);
        switch (state) {
            case NONE:
                int taskDID;
                searchPlayers.get(loc).add(p.getUniqueId());
                stateMap.put(loc, CheckState.DOING);
                taskDID = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(DeathMatch.class), () -> {
                    if (timeMap.get(loc) == remainTime) {
                        stateMap.put(loc, CheckState.DONE);
                        for (UUID uuid : searchPlayers.get(loc)) {
                            Player value = Bukkit.getPlayer(uuid);
                            if (value != null && GameController.contains(uuid)) {
                                setChest(loc);
                                value.openInventory(((Chest)loc.getBlock().getState()).getBlockInventory());

                            }
                        }
                        Bukkit.getScheduler().cancelTask(taskID.get(loc));
                        searchPlayers.get(loc).clear();
                        timeMap.remove(loc);
                        stateMap.remove(loc);
                        taskID.remove(loc);
                        return;
                    }
                    timeMap.put(loc, timeMap.get(loc) + 1);
                    if (!GameController.contains(p.getUniqueId()) || !p.getWorld().equals(loc.getWorld()) || p.getLocation().distance(loc) > 3) {
                        stateMap.put(loc, CheckState.NONE);
                        Bukkit.getScheduler().cancelTask(taskID.get(loc));
                        for (UUID uid : searchPlayers.get(loc)) {
                            Player pp = Bukkit.getPlayer(uid);
                            if (pp != null)
                                pp.sendTitle("§c[ §f! §c]", "§c거리가 3칸 이상 멀어져 탐색이 중단되었습니다.", 0, 50, 0);
                        }
                        searchPlayers.get(loc).clear();
                        timeMap.remove(loc);
                        stateMap.remove(loc);
                        taskID.remove(loc);
                        return;
                    }
                    ArrayList<UUID> removeUID = new ArrayList<>();
                    for (UUID uid : searchPlayers.get(loc)) {
                        Player pl = Bukkit.getPlayer(uid);
                        if (pl != null)
                            pl.sendTitle("§6[ §f! §6]", "§6상자를 §b탐색§f하고 있습니다.. §b" + (remainTime - timeMap.get(loc)) + "§f초후 완료되며, §63§f칸 이상 벗어나지 마십시오.", 0, 25, 0);
                        else
                            removeUID.add(uid);
                    }
                    searchPlayers.get(loc).removeAll(removeUID);
                }, 0L, 20L).getTaskId();
                taskID.put(loc, taskDID);
                break;
            case DOING:
                if (!searchPlayers.get(loc).contains(p.getUniqueId())) {
                    searchPlayers.get(loc).add(p.getUniqueId());
                    p.sendMessage("§6[ §f! §6] §c누군가 이미 탐색중입니다.. 탐색 상태가 공유됩니다.");
                }
                break;
            case DONE:
                BlockState blockState = loc.getBlock().getState();
                if (!(blockState instanceof Chest))
                    Bukkit.broadcastMessage("§4Error..");
                else {
                    Chest chest = (Chest) blockState;
                    chest.getBlockInventory().clear();
                    p.openInventory(chest.getBlockInventory());
                }

                break;
        }

    }

    public void setChest(Location loc) {
        Regen.chestRegen.put(loc, 0);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Chest chest = (Chest) loc.getBlock().getState();
        chest.getBlockInventory().clear();
        int rand = random.nextInt(0, 10);
        switch (rand) {
            case 0:
            case 6:
                chest.getInventory().addItem(Regen.getPotions().get(random.nextInt(0, Regen.getPotions().size())));
                break;
            case 1:
                chest.getInventory().addItem(Regen.getPotions().get(random.nextInt(0, Regen.getPotions().size())));
                chest.getInventory().addItem(DayZItem.EATABLE.get(random.nextInt(0, DayZItem.EATABLE.size())));
                break;
            case 2:
                chest.getInventory().addItem(Regen.calcArmor());
                break;
            case 3:
                chest.getInventory().addItem(DayZItem.EATABLE.get(random.nextInt(0, DayZItem.EATABLE.size())));
                break;
            case 5:
                if (ThreadLocalRandom.current().nextInt(0, 11) <= 2)
                    chest.getInventory().addItem(CrackShotApi.generateNotOPWeapon());
                else
                    chest.getInventory().addItem(CrackShotApi.generateDayZWeapon());
                break;
            case 9:
                chest.getInventory().addItem(CrackShotApi.generateDayZWeapon());
            case 8:
                chest.getInventory().addItem(DayZItem.EATABLE.get(random.nextInt(0, DayZItem.EATABLE.size())));
                chest.getInventory().addItem(DayZItem.EATABLE.get(random.nextInt(0, DayZItem.EATABLE.size())));
                break;
            case 4:
                chest.getInventory().addItem(Regen.calcArmor());
                chest.getInventory().addItem(Regen.getPotions().get(random.nextInt(0, Regen.getPotions().size())));
                break;
            default:
                break;
        }
    }
}
