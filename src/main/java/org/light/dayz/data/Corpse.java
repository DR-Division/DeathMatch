package org.light.dayz.data;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.dayz.game.GameController;
import org.light.dayz.util.CorpseRegistry;
import org.light.dayz.util.NPCMaker;
import org.light.source.DeathMatch;

import java.util.ArrayList;
import java.util.UUID;

public class Corpse {

    private EntityPlayer victim;
    private Inventory victimInv;
    private int remainTime;
    private int searchTime;
    private CheckState state; //NONE(수색 안됨), DOING(수색중), DONE(수색 완료)
    private ArrayList<UUID> searchPlayers;
    private int taskID;

    public Corpse(EntityPlayer victim, Inventory victimInv, int remainTime) {
        this.victim = victim;
        this.victimInv = victimInv;
        this.remainTime = remainTime;
        this.state = CheckState.NONE;
        this.searchPlayers = new ArrayList<>();
        this.taskID = -1;
        this.searchTime = 0;
    }

    public void remove() {
        victim.die();
        NPCMaker.removeEntity(victim);
        victimInv.clear();
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public CheckState getState() {
        return state;
    }

    public void setState(CheckState state) {
        this.state = state;
    }

    public enum CheckState {
        NONE,
        DOING,
        DONE
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }

    public Player getEntity() {
        return victim.getBukkitEntity();
    }

    public void openInventory(Player p) {
        switch (state) {
            case NONE:
                setState(CheckState.DOING);
                searchPlayers.add(p.getUniqueId());
                taskID = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(DeathMatch.class), () -> {
                    if (searchTime == remainTime) {
                        setState(CheckState.DONE);
                        for (UUID uuid : searchPlayers) {
                            Player value = Bukkit.getPlayer(uuid);
                            if (value != null && GameController.contains(uuid)) {
                                value.openInventory(victimInv);
                                CorpseRegistry.findingPlayer.remove(uuid);

                            }
                        }
                        Bukkit.getScheduler().cancelTask(taskID);
                        searchPlayers.clear();
                        return;
                    }
                    searchTime++;
                    if (!GameController.contains(p.getUniqueId()) || !p.getWorld().equals(victim.getBukkitEntity().getLocation().getWorld()) || p.getLocation().distance(victim.getBukkitEntity().getLocation()) > 3) {
                        setState(CheckState.NONE);
                        Bukkit.getScheduler().cancelTask(taskID);
                        for (UUID uid : searchPlayers) {
                            Player pp = Bukkit.getPlayer(uid);
                            CorpseRegistry.findingPlayer.remove(uid);
                            if (pp != null)
                                pp.sendTitle("§c[ §f! §c]", "§c거리가 3칸 이상 멀어져 탐색이 중단되었습니다.", 0, 50, 0);
                        }
                        searchPlayers.clear();
                        searchTime = 0;
                        return;
                    }
                    ArrayList<UUID> removeUID = new ArrayList<>();
                    for (UUID uid : searchPlayers) {
                        Player pl = Bukkit.getPlayer(uid);
                        if (pl != null)
                            pl.sendTitle("§6[ §f! §6]", "§c" + victim.getName() + "§f의 인벤토리를 §b탐색§f중입니다.. §b" + (remainTime - searchTime) + "§f초후 완료되며, §63§f칸 이상 벗어나지 마십시오.", 0, 25, 0);
                        else
                            removeUID.add(uid);
                    }
                    searchPlayers.removeAll(removeUID);
                    CorpseRegistry.findingPlayer.removeAll(removeUID);
                }, 0L, 20L).getTaskId();
                break;
            case DOING:
                if (!searchPlayers.contains(p.getUniqueId())) {
                    searchPlayers.add(p.getUniqueId());
                    p.sendMessage("§6[ §f! §6] §c누군가 이미 탐색중입니다.. 탐색 상태가 공유됩니다.");
                }
                break;
            case DONE:
                p.openInventory(victimInv);
                break;
        }

    }
}


