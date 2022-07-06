package org.light.dayz.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.command.TradeCommand;
import org.light.dayz.game.GameController;
import org.light.dayz.util.DayZItem;
import org.light.dayz.util.Regen;
import org.light.dayz.util.VirtualChest;
import org.light.source.Game.GameManager;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Singleton.CrackShotApi;
import org.light.source.Singleton.EconomyApi;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class InventoryClick implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if (event.getView().getTitle().contains("거래") && (TradeCommand.tradeMap.containsKey(p.getUniqueId()) || TradeCommand.getRequester(p.getUniqueId()) != null)) {
            Player target = null;
            if (TradeCommand.tradeMap.containsKey(p.getUniqueId())) {
                target = Bukkit.getPlayer(TradeCommand.tradeMap.get(p.getUniqueId()));
                for (int i = 0; i < 9; i++)
                    if (event.getInventory().getItem(i) != null)
                        p.getInventory().addItem(event.getInventory().getItem(i));
                for (int i = 18; i < 27; i++)
                    if (event.getInventory().getItem(i) != null)
                        target.getInventory().addItem(event.getInventory().getItem(i));
            }
            else {
                target = Bukkit.getPlayer(TradeCommand.getRequester(p.getUniqueId()));
                for (int i = 0; i < 9; i++)
                    if (event.getInventory().getItem(i) != null)
                        target.getInventory().addItem(event.getInventory().getItem(i));
                for (int i = 18; i < 27; i++)
                    if (event.getInventory().getItem(i) != null)
                        p.getInventory().addItem(event.getInventory().getItem(i));
            }
            target.sendMessage("§4[ §f! §4] §c상대방이 인벤토리를 닫아 거래가 종료되었습니다.");
            Bukkit.getConsoleSender().sendMessage(p.getName() + "과 " + target.getName() + "의 거래가 " + p.getName() + "의 GUI 강제종료로 종료됨.");
            TradeCommand.removeTrade(p);
            target.closeInventory();
        }
    }
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getView().getTitle().contains("거래")) {
            if ((TradeCommand.tradeMap.containsKey(p.getUniqueId()) && checkSet(true, event.getRawSlots())) || (TradeCommand.getRequester(p.getUniqueId()) != null && checkSet(false, event.getRawSlots()))) {
                event.setCancelled(true);
                p.sendMessage("§4상대방의 자리의 아이템은 움직일 수 없습니다.");
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        ItemStack stack = event.getCurrentItem();
        Player p = (Player) event.getWhoClicked();
        if (event.getRawSlot() == -999) {
            ItemStack trash = event.getCursor();
            if (!GameManager.getInstance().contains(p.getUniqueId()) && CrackShotApi.getCSID(trash) != null && !p.isOp()) {
                Item item = p.getWorld().dropItem(p.getLocation(), trash);
                item.setVelocity(p.getLocation().getDirection());
                p.setItemOnCursor(null);
            }
            return;
        }

        if (event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 39) {
            if (stack != null && stack.getType() == Material.PUMPKIN) {
                event.setCancelled(true);
                p.closeInventory();
                return;
            }
        }

        if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack.getType() == Material.PUMPKIN
                && stack.getItemMeta().getDisplayName().equalsIgnoreCase("§cCSP§7_§cPUMPKIN")) {
            event.getCurrentItem().setType(Material.AIR);
            event.setCancelled(true);
            p.closeInventory();
        }


        if (event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 40) {
            event.setCancelled(true);
            return;
        }

        if (stack != null && stack.getType() != Material.AIR && stack.getItemMeta().getDisplayName().equalsIgnoreCase("."))
            event.setCurrentItem(null);
        if (event.getView().getTitle().contains("게임모드")) {
            event.setCancelled(true);
            if (stack != null && stack.getType() == Material.DIAMOND_SWORD)
                GameController.insertGame(p, false);
            else if (stack != null && stack.getType() == Material.WOODEN_SWORD)
                GameController.insertGame(p, true);
        }
        else if (event.getView().getTitle().contains("창고 선택")) {
            event.setCancelled(true);
            if (event.getRawSlot() >= 0 && event.getRawSlot() <= 4) {
                if (stack.getType() == Material.CHEST)
                    VirtualChest.openChest(p, toNumber(event.getRawSlot()));
                else if (stack.getType() == Material.BARRIER) {
                    if (event.getRawSlot() == 2) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 2500) {
                            EconomyApi.getInstance().subtractMoney(p, 2500);
                            MinimizeLogger.getInstance().appendLog(p.getName() + "님이 3번 창고 구매");
                            p.sendMessage("§b3번 창고를 구매하셨습니다.");
                            Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 3");
                            VirtualChest.chest3.put(p.getUniqueId(), inventory);
                            VirtualChest.selectChest(p);
                        }
                        else
                            p.sendMessage("§c돈이 부족합니다.");
                    }
                    else if (event.getRawSlot() == 3) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 5000) {
                            EconomyApi.getInstance().subtractMoney(p, 5000);
                            MinimizeLogger.getInstance().appendLog(p.getName() + "님이 4번 창고 구매");
                            p.sendMessage("§b4번 창고를 구매하셨습니다.");
                            Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 4");
                            VirtualChest.chest4.put(p.getUniqueId(), inventory);
                            VirtualChest.selectChest(p);
                        }
                        else
                            p.sendMessage("§c돈이 부족합니다.");
                    }
                    else {
                        if (EconomyApi.getInstance().currentMoney(p) >= 7500) {
                            EconomyApi.getInstance().subtractMoney(p, 7500);
                            MinimizeLogger.getInstance().appendLog(p.getName() + "님이 5번 창고 구매");
                            p.sendMessage("§b5번 창고를 구매하셨습니다.");
                            Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 5");
                            VirtualChest.chest5.put(p.getUniqueId(), inventory);
                            VirtualChest.selectChest(p);
                        }
                        else
                            p.sendMessage("§c돈이 부족합니다.");
                    }
                }
            }
        }
        else if (event.getView().getTitle().contains("상점")) {
            event.setCancelled(true);
            if (event.getRawSlot() >= 0 && event.getRawSlot() <= 9 && stack != null) {
                if (stack.getType() == Material.ROTTEN_FLESH) {
                    int amount = getZombieAmount(p.getInventory());
                    if (event.getClick() == ClickType.LEFT) {
                        if (amount >= 1) {
                            removeZombieItem(p.getInventory(), 1);
                            EconomyApi.getInstance().giveMoney(p, 1);
                            p.sendMessage("§c[ §f! §c] §f썩은 고기 1개를 팔아 §61§f원을 흭득하였습니다.");
                        }
                        else
                            p.sendMessage("§c썩은 고기가 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (amount >= 1) {
                            removeZombieItem(p.getInventory());
                            EconomyApi.getInstance().giveMoney(p, amount);
                            p.sendMessage("§c[ §f! §c] §f썩은 고기 " + amount + "개를 팔아 §6" + amount + "§f원을 흭득하였습니다.");
                        }
                        else
                            p.sendMessage("§c썩은 고기가 모자랍니다.");

                    }
                    else if (event.getClick() == ClickType.RIGHT) {
                        if (amount >= 10) {
                            removeZombieItem(p.getInventory(), 10);
                            EconomyApi.getInstance().giveMoney(p, 10);
                            p.sendMessage("§c[ §f! §c] §f썩은 고기 10개를 팔아 §610§f원을 흭득하였습니다.");
                        }
                        else
                            p.sendMessage("§c썩은 고기가 모자랍니다.");
                    }
                }
                else if (stack.getType() == Material.DIAMOND_SWORD) {
                    if (event.getClick() == ClickType.LEFT) {
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if (item != null && item.getType() != Material.AIR && CrackShotApi.getCSID(item) != null) {
                            String id = CrackShotApi.getCSID(item);
                            int amount = getPrice(id);
                            for (int i = 0; i < amount; i++)
                                p.getInventory().addItem(new ItemStack(Material.ROTTEN_FLESH));
                            p.sendMessage("§c[ §f! §c] §f" + id + "를 팔아 §6" + amount + "§f개의 썩은 고기를 흭득하였습니다.");
                            p.getInventory().setItemInMainHand(null);
                        }
                        else
                            p.sendMessage("§c총을 손에 들고 사용해주세요.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (checkGun(p.getInventory())) {
                            int amount = sellAllWeapon(p.getInventory());
                            for (int i = 0; i < amount; i++)
                                p.getInventory().addItem(new ItemStack(Material.ROTTEN_FLESH));
                            p.sendMessage("§c[ §f! §c] §f총기 전체를 팔아 §6" + amount + "§f개의 썩은 고기를 흭득하였습니다.");
                        }
                        else
                            p.sendMessage("§c인벤토리에 총이 존재하지 않습니다.");
                    }
                }
                else if (stack.getType() == Material.BREAD) {
                    if (event.getClick() == ClickType.LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 5) {
                            p.getInventory().addItem(new ItemStack(Material.BREAD));
                            EconomyApi.getInstance().subtractMoney(p, 5);
                            p.sendMessage("§c[ §f! §c] §f빵 1개를 §65§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 320) {
                            p.getInventory().addItem(new ItemStack(Material.BREAD, 64));
                            EconomyApi.getInstance().subtractMoney(p, 320);
                            p.sendMessage("§c[ §f! §c] §f빵 64개를 §6320§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.RIGHT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 50) {
                            p.getInventory().addItem(new ItemStack(Material.BREAD, 10));
                            EconomyApi.getInstance().subtractMoney(p, 50);
                            p.sendMessage("§c[ §f! §c] §f빵 10개를 §650§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                }
                else if (stack.getType() == Material.IRON_NUGGET) {
                    ItemStack data = DayZItem.BITO;
                    if (event.getClick() == ClickType.LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 10) {
                            data.setAmount(1);
                            p.getInventory().addItem(data);
                            EconomyApi.getInstance().subtractMoney(p, 10);
                            p.sendMessage("§c[ §f! §c] §f비토코인 1개를 §610§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 100) {
                            data.setAmount(10);
                            p.getInventory().addItem(data);
                            EconomyApi.getInstance().subtractMoney(p, 100);
                            p.sendMessage("§c[ §f! §c] §f비토코인 10개를 §6100§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.RIGHT) {
                        if (getSpecificAmount(p.getInventory(), Material.IRON_NUGGET) != 0) {
                            removeSpecificItem(p.getInventory(), Material.IRON_NUGGET, 1);
                            EconomyApi.getInstance().giveMoney(p, 10);
                            p.sendMessage("§c[ §f! §c] §f비토코인 1개를 §610§f원에 판매하였습니다.");
                        }
                        else
                            p.sendMessage("§c아이템이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                        if (getSpecificAmount(p.getInventory(), Material.IRON_NUGGET) >= 10) {
                            removeSpecificItem(p.getInventory(), Material.IRON_NUGGET, 10);
                            EconomyApi.getInstance().giveMoney(p, 100);
                            p.sendMessage("§c[ §f! §c] §f비토코인 10개를 §6100§f원에 판매하였습니다.");
                        }
                        else
                            p.sendMessage("§c아이템이 모자랍니다.");
                    }
                }
                else if (stack.getType() == Material.GOLD_NUGGET) {
                    ItemStack data = DayZItem.RIBBLE;
                    if (event.getClick() == ClickType.LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 100) {
                            data.setAmount(1);
                            p.getInventory().addItem(data);
                            EconomyApi.getInstance().subtractMoney(p, 100);
                            p.sendMessage("§c[ §f! §c] §f리블 1개를 §6100§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 1000) {
                            data.setAmount(10);
                            p.getInventory().addItem(data);
                            EconomyApi.getInstance().subtractMoney(p, 1000);
                            p.sendMessage("§c[ §f! §c] §f리블 10개를 §61000§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.RIGHT) {
                        if (getSpecificAmount(p.getInventory(), Material.GOLD_NUGGET) != 0) {
                            removeSpecificItem(p.getInventory(), Material.GOLD_NUGGET, 1);
                            EconomyApi.getInstance().giveMoney(p, 100);
                            p.sendMessage("§c[ §f! §c] §f리블 1개를 §6100§f원에 판매하였습니다.");
                        }
                        else
                            p.sendMessage("§c아이템이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                        if (getSpecificAmount(p.getInventory(), Material.GOLD_NUGGET) >= 10) {
                            removeSpecificItem(p.getInventory(), Material.GOLD_NUGGET, 10);
                            EconomyApi.getInstance().giveMoney(p, 1000);
                            p.sendMessage("§c[ §f! §c] §f리블 10개를 §61000§f원에 판매하였습니다.");
                        }
                        else
                            p.sendMessage("§c아이템이 모자랍니다.");
                    }
                }
                else if (stack.getType() == Material.GOLD_INGOT) {
                    ItemStack data = DayZItem.DOGE;
                    if (event.getClick() == ClickType.LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 1000) {
                            data.setAmount(1);
                            p.getInventory().addItem(data);
                            EconomyApi.getInstance().subtractMoney(p, 1000);
                            p.sendMessage("§c[ §f! §c] §f골든도지 1개를 §61000§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 10000) {
                            data.setAmount(10);
                            p.getInventory().addItem(data);
                            EconomyApi.getInstance().subtractMoney(p, 10000);
                            p.sendMessage("§c[ §f! §c] §f골든도지 10개를 §610000§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.RIGHT) {
                        if (getSpecificAmount(p.getInventory(), Material.GOLD_INGOT) != 0) {
                            removeSpecificItem(p.getInventory(), Material.GOLD_INGOT, 1);
                            EconomyApi.getInstance().giveMoney(p, 1000);
                            p.sendMessage("§c[ §f! §c] §f골든도지 1개를 §61000§f원에 판매하였습니다.");
                        }
                        else
                            p.sendMessage("§c아이템이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                        if (getSpecificAmount(p.getInventory(), Material.GOLD_INGOT) >= 10) {
                            removeSpecificItem(p.getInventory(), Material.GOLD_INGOT, 10);
                            EconomyApi.getInstance().giveMoney(p, 10000);
                            p.sendMessage("§c[ §f! §c] §f골든도지 10개를 §610000§f원에 판매하였습니다.");
                        }
                        else
                            p.sendMessage("§c아이템이 모자랍니다.");
                    }
                }
                else if (stack.getType() == Material.TRAPPED_CHEST) {
                    ItemStack data = DayZItem.RANDOM_BOX.clone();
                    int tagAmount = getSpecificAmount(p.getInventory(), Material.NAME_TAG);
                    if (event.getClick() == ClickType.LEFT) {
                        if (tagAmount >= 9) {
                            data.setAmount(1);
                            p.getInventory().addItem(data);
                            removeSpecificItem(p.getInventory(), Material.NAME_TAG, 9);
                            p.sendMessage("§c[ §f! §c] §f랜덤 박스 1개를 교환하였습니다.");
                        }
                        else
                            p.sendMessage("§c인식표가 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (tagAmount >= 90) {
                            data.setAmount(10);
                            p.getInventory().addItem(data);
                            removeSpecificItem(p.getInventory(), Material.NAME_TAG, 90);
                            p.sendMessage("§c[ §f! §c] §f랜덤 박스 1개를 교환하였습니다.");
                        }
                        else
                            p.sendMessage("§c인식표가 모자랍니다.");
                    }
                }
            }

        }
        else if (event.getView().getTitle().contains("거래") || p.getOpenInventory().getTitle().contains("거래")) {
            if (event.isCancelled())
                event.setCancelled(false);
            if ((TradeCommand.tradeMap.containsKey(p.getUniqueId()) && event.getRawSlot() >= 18 && event.getRawSlot() < 27) || TradeCommand.getRequester(p.getUniqueId()) != null && event.getRawSlot() < 9) {
                event.setCancelled(true);
                p.sendMessage("§4상대방의 자리의 아이템은 움직일 수 없습니다.");
            }

            if (stack != null) {
                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                    event.setCancelled(true);
                if ((TradeCommand.tradeMap.containsKey(p.getUniqueId()) && event.getInventory().getItem(16) != null && event.getInventory().getItem(16).getType() == Material.STONE_BUTTON) || (TradeCommand.getRequester(p.getUniqueId()) != null && event.getInventory().getItem(10) != null && event.getInventory().getItem(10).getType() == Material.STONE_BUTTON)) {
                    event.setCancelled(true);
                    if (!stack.getType().toString().contains("BUTTON"))
                        p.sendMessage("§6[ §f! §6] §c현재 상대방이 교환을 수락한 상태입니다.");
                }
                if (stack.getType() == Material.IRON_BARS || stack.getType().toString().contains("BUTTON")) {
                    event.setCancelled(true);
                    if (stack.getType() == Material.OAK_BUTTON && stack.getItemMeta().getDisplayName().contains(p.getName())) {
                        p.getOpenInventory().setItem(event.getRawSlot(), DayZItem.createItemStack(Material.STONE_BUTTON, "§8[ §f! §8] §b" + p.getName(), (short) 0, " ", " §8-  §f상대방의 아이템을 확인하였습니다.", " "));
                    }
                    if (event.getInventory().getItem(10) != null && event.getInventory().getItem(10).getType() == Material.STONE_BUTTON && event.getInventory().getItem(16) != null && event.getInventory().getItem(16).getType() == Material.STONE_BUTTON) {
                        Player target = null;
                        if (TradeCommand.tradeMap.containsKey(p.getUniqueId())) {
                            target = Bukkit.getPlayer(TradeCommand.tradeMap.get(p.getUniqueId()));
                            tradeGive(p, target, event.getInventory());
                        }
                        else {
                            target = Bukkit.getPlayer(TradeCommand.getRequester(p.getUniqueId()));
                            tradeGive(target, p, event.getInventory());
                        }
                        TradeCommand.removeTrade(p);
                        p.sendMessage("§6거래가 종료되었습니다!.");
                        target.sendMessage("§6거래가 종료되었습니다!.");
                        p.closeInventory();
                        target.closeInventory();
                    }

                }
            }
        }

        else if (!GameManager.getInstance().contains(event.getWhoClicked().getUniqueId())
                && !event.getView().getTitle().contains("채널")
                && !checkDeathMatchTitle(event.getView().getTitle())) {
            event.setCancelled(false);
            if (p.getInventory().getItemInOffHand().getType() != Material.AIR)
                p.getInventory().setItemInOffHand(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        // 이게 왜 CRAFTING으로 분류되는지는 모르겠음
        if (event.getInventory().getType() == InventoryType.CRAFTING && event.getRawSlots().contains(45))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        ItemStack stack = event.getItemDrop().getItemStack();
        event.setCancelled(GameManager.getInstance().contains(p.getUniqueId()) || stack.getType() == Material.PLAYER_HEAD || checkDummy(stack));

    }

    public boolean checkDummy(ItemStack stack) {
        return stack.getItemMeta().getDisplayName().equalsIgnoreCase(".");
    }

    public boolean checkDeathMatchTitle(String title) {
        return title.contains("랭크");
    }

    public VirtualChest.Number toNumber(int value) {
        if (value == 0)
            return VirtualChest.Number.ONE;
        else if (value == 1)
            return VirtualChest.Number.TWO;
        else if (value == 2)
            return VirtualChest.Number.THREE;
        else if (value == 3)
            return VirtualChest.Number.FOUR;
        else if (value == 4)
            return VirtualChest.Number.FIVE;
        else
            return VirtualChest.Number.ONE;
    }

    public int getZombieAmount(Inventory inventory) {
        int amount = 0;
        for (ItemStack stack : inventory.getContents())
            if (stack != null && stack.getType() == Material.ROTTEN_FLESH)
                amount += stack.getAmount();
        return amount;
    }

    public void removeZombieItem(Inventory inventory) {
        for (ItemStack stack : inventory.getContents())
            if (stack != null && stack.getType() == Material.ROTTEN_FLESH)
                inventory.remove(stack);
    }

    public void removeZombieItem(Inventory inventory, int amount) {
        int count = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null && stack.getType() == Material.ROTTEN_FLESH) {
                if (count >= amount)
                    break;
                else if (amount - count < stack.getAmount()) {
                    ItemStack copyStack = stack.clone();
                    copyStack.setAmount(stack.getAmount() - (amount - count));
                    inventory.setItem(i, copyStack);
                    count = amount;
                }
                else {
                    count += stack.getAmount();
                    inventory.remove(stack);
                }
            }
        }
    }

    public boolean checkGun(Inventory inventory) {
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.getType() != Material.AIR && CrackShotApi.getCSID(stack) != null)
                return true;
        }
        return false;
    }

    public int sellAllWeapon(Inventory inventory) {
        int price = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.getType() != Material.AIR && CrackShotApi.getCSID(stack) != null) {
                price += getPrice(CrackShotApi.getCSID(stack));
                inventory.remove(stack);
            }
        }
        return price;
    }

    public int getPrice(String value) {
        if (value.contains("_"))
            return ThreadLocalRandom.current().nextInt(5, 16);
        else
            return ThreadLocalRandom.current().nextInt(1, 6);
    }

    public int getSpecificAmount(Inventory inventory, Material type) {
        int amount = 0;
        for (ItemStack stack : inventory.getContents())
            if (stack != null && stack.getType() == type)
                amount += stack.getAmount();
        return amount;
    }


    public void removeSpecificItem(Inventory inventory, Material data, int amount) {
        int count = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null && stack.getType() == data) {
                if (count >= amount)
                    break;
                else if (amount - count < stack.getAmount()) {
                    ItemStack copyStack = stack.clone();
                    copyStack.setAmount(stack.getAmount() - (amount - count));
                    inventory.setItem(i, copyStack);
                    count = amount;
                }
                else {
                    count += stack.getAmount();
                    inventory.remove(stack);
                }
            }
        }
    }

    private void tradeGive(Player a, Player b, Inventory inv) {
        StringBuilder builder = new StringBuilder();
        Bukkit.getConsoleSender().sendMessage(b.getName() + "이 " + a.getName() + "과 거래를 종료함. [A/B순서로 지급 받은 아이템]");
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR) {
                giveItem(b, inv.getItem(i));
                builder.append(itemToString(inv.getItem(i)));
            }
        }
        Bukkit.getConsoleSender().sendMessage(builder.toString());
        builder = new StringBuilder();
        for (int i = 18; i < 27; i++) {
            if (inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR) {
                giveItem(a, inv.getItem(i));
                builder.append(itemToString(inv.getItem(i)));
            }
        }
        Bukkit.getConsoleSender().sendMessage(builder.toString());
    }

    private void giveItem(Player p, ItemStack stack) {
        p.getInventory().addItem(stack);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 3f);
    }

    private String itemToString(ItemStack stack) {
        String isCRA = CrackShotApi.getCSID(stack);
        return isCRA != null ? isCRA + "[" + stack.getAmount() + "] " : stack.getType() + "[" + stack.getAmount() + "] ";
    }

    private boolean checkSet(boolean val, Set<Integer> input) {
        if (val) {
            for (int var : input)
                if (var >= 18 && var < 27)
                    return true;
        }
        else {
            for (int var2 : input)
                if (var2 < 9)
                    return true;
        }
        return false;
    }
}
