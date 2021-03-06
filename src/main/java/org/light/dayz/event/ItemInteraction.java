package org.light.dayz.event;

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
        if (stack != null && stack.getType() != Material.AIR && stack.getItemMeta().getDisplayName() != null) {
            event.setCancelled(true);
            String name = stack.getItemMeta().getDisplayName();
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && (name.contains("??????") || name.contains("????????????") || name.contains("????????? ?????????") || name.contains("?????????"))) {
                if (p.hasPotionEffect(PotionEffectType.FAST_DIGGING) && p.hasPotionEffect(PotionEffectType.SLOW))
                    p.sendMessage("??c[ ??f! ??c] ??b?????? ???????????? ??????????????????.");
                else {
                    if (stack.getAmount() > 1) {
                        stack.setAmount(stack.getAmount() - 1);
                        p.getInventory().setItemInMainHand(stack);
                    }
                    else
                        p.getInventory().setItemInMainHand(null);
                    if (name.contains("????????????")) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 1, true, false), false);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 100, true, false), false);
                        p.sendMessage("??c[ ??f! ??c] ??f??????????????? ??????????????????.. (5???, 3????????? ???????????? ??????)");
                        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
                            if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) <= 3.0) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 5, true, true), true);
                            }
                            else if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) > 3.0) {
                                ItemStack clone = stack.clone();
                                clone.setAmount(1);
                                p.sendMessage("??c????????? ?????? ????????? ????????? ?????????????????????.");
                                p.getInventory().addItem(clone);
                            }
                        }, 100L);
                    }
                    else {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, 1, true, false), false);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100, true, false), false);
                        p.sendMessage("??c[ ??f! ??c] ??f???????????? ??????????????????.. (5???, 3????????? ???????????? ??????)");
                        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
                            if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) <= 3.0) {
                                if (name.contains("??????"))
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25, 7, true, true), true);
                                else if (name.contains("????????? ?????????"))
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 900, 2, true, true), true);
                                else {
                                    p.removePotionEffect(PotionEffectType.SLOW);
                                    p.removePotionEffect(PotionEffectType.LUCK);
                                }
                            }
                            else if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) > 3.0) {
                                ItemStack clone = stack.clone();
                                clone.setAmount(1);
                                p.sendMessage("??c????????? ?????? ????????? ????????? ?????????????????????.");
                                p.getInventory().addItem(clone);
                            }
                        }, 60L);
                    }
                }
            }
        }
    }

}
