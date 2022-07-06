package org.light.dayz.event;

import com.shampaggon.crackshot.CSDirector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.dayz.game.GameController;
import org.light.source.DeathMatch;
import org.light.source.Game.GameManager;
import org.light.source.Singleton.CrackShotApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class ItemInteraction implements Listener {

    private DeathMatch Plugin;

    public ItemInteraction(DeathMatch Plugin) {
        this.Plugin = Plugin;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player p = event.getPlayer();
        ItemStack stack = event.getItem();
        ArrayList<Material> list = new ArrayList<>(Arrays.asList(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS));
        if (!GameManager.getInstance().contains(p.getUniqueId()) && list.contains(stack.getType()))
            event.setCancelled(false);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Location loc = p.getLocation().clone();
        ItemStack stack = p.getInventory().getItemInMainHand();
        if (stack.getType() == Material.AIR) return;
        if (stack.getType() == Material.NAME_TAG)
            event.setCancelled(true);
        String name = stack.getItemMeta().getDisplayName();
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && (name.contains("붕대") || name.contains("구급상자") || name.contains("에너지 드링크") || name.contains("치료제") || name.contains("랜덤"))) {
            event.setCancelled(true);
            if (name.contains("랜덤")) {
                ItemStack result = CrackShotApi.generateNotOPWeapon();
                ItemStack hand = p.getInventory().getItemInMainHand();
                Bukkit.broadcastMessage("§6[ §f! §6] §c" + p.getName() + "§f님이 §6랜덤 총기 상자§f에서 §f" + result.getItemMeta().getDisplayName() + "§f을 흭득하셨습니다!");
                if (hand.getAmount() == 1)
                    p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                else {
                    hand.setAmount(hand.getAmount() - 1);
                    p.getInventory().setItemInMainHand(hand);
                }
                p.getInventory().addItem(result);

            }
            else if (p.hasPotionEffect(PotionEffectType.FAST_DIGGING) && p.hasPotionEffect(PotionEffectType.SLOW))
                p.sendMessage("§c[ §f! §c] §b이미 아이템을 사용중입니다.");
            else {
                if (stack.getAmount() > 1) {
                    stack.setAmount(stack.getAmount() - 1);
                    p.getInventory().setItemInMainHand(stack);
                }
                else
                    p.getInventory().setItemInMainHand(null);
                if (name.contains("구급상자")) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 1, true, false), false);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 5, true, false), false);
                    p.sendMessage("§c[ §f! §c] §f구급 상자를 사용 중입니다.. (5초 | 3칸 이상 멀어질시 취소)");
                    Bukkit.getScheduler().runTaskLater(Plugin, () -> {
                        if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) <= 3.0) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 5, true, true), true);
                        }
                        else if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) > 3.0) {
                            ItemStack clone = stack.clone();
                            clone.setAmount(1);
                            p.sendMessage("§c거리가 너무 멀어져 사용이 취소되었습니다.");
                            p.getInventory().addItem(clone);
                        }
                    }, 100L);
                }
                else {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, 1, true, false), false);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 5, true, false), false);
                    p.sendMessage("§c[ §f! §c] §f아이템을 사용 중입니다.. (5초 | 3칸 이상 멀어질시 취소)");
                    Bukkit.getScheduler().runTaskLater(Plugin, () -> {
                        if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) <= 3.0) {
                            if (name.contains("붕대"))
                                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25, 7, true, true), true);
                            else if (name.contains("에너지 드링크"))
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 900, 2, true, true), true);
                            else {
                                p.removePotionEffect(PotionEffectType.SLOW);
                                p.removePotionEffect(PotionEffectType.LUCK);
                            }
                        }
                        else if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) > 3.0) {
                            ItemStack clone = stack.clone();
                            clone.setAmount(1);
                            p.sendMessage("§c거리가 너무 멀어져 사용이 취소되었습니다.");
                            p.getInventory().addItem(clone);
                        }
                    }, 60L);
                }
            }
        }
    }

}
