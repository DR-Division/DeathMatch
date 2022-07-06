package org.light.dayz.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.light.source.Singleton.CrackShotApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class VirtualChest {

    public static HashMap<UUID, Inventory> chest = new HashMap<>();
    public static HashMap<UUID, Inventory> chest2 = new HashMap<>();
    public static HashMap<UUID, Inventory> chest3 = new HashMap<>();
    public static HashMap<UUID, Inventory> chest4 = new HashMap<>();
    public static HashMap<UUID, Inventory> chest5 = new HashMap<>();

    public enum Number {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE
    }

    public static void selectChest(Player p) {
        Inventory selChest = Bukkit.createInventory(null, 9, "§0창고 선택");
        selChest.setItem(0, DayZItem.createItemStack(Material.CHEST, "§c[ §f! §c] §f1번 창고", (short) 0, " ", " §8-  §f클릭 시 1번 창고를 엽니다.", " "));
        selChest.setItem(1, DayZItem.createItemStack(Material.CHEST, "§c[ §f1 §c] §f2번 창고", (short) 0, " ", " §8-  §f클릭 시 2번 창고를 엽니다.", " "));
        if (chest3.containsKey(p.getUniqueId()))
            selChest.setItem(2, DayZItem.createItemStack(Material.CHEST, "§c[ §f1 §c] §f3번 창고", (short) 0, " ", " §8-  §f클릭 시 3번 창고를 엽니다.", " "));
        else
            selChest.setItem(2, DayZItem.createItemStack(Material.BARRIER, "§c[ §f1 §c] §f3번 창고", (short) 0, " ", " §8-  §f클릭 시 3번 창고를 구매합니다.", " §8-  §f가격은 §62500§f원 입니다.", " "));
        if (chest4.containsKey(p.getUniqueId()))
            selChest.setItem(3, DayZItem.createItemStack(Material.CHEST, "§c[ §f1 §c] §f4번 창고", (short) 0, " ", " §8-  §f클릭 시 4번 창고를 엽니다.", " "));
        else
            selChest.setItem(3, DayZItem.createItemStack(Material.BARRIER, "§c[ §f1 §c] §f4번 창고", (short) 0, " ", " §8-  §f클릭 시 4번 창고를 구매합니다.", " §8-  §f가격은 §65000§f원 입니다.", " "));
        if (chest5.containsKey(p.getUniqueId()))
            selChest.setItem(4, DayZItem.createItemStack(Material.CHEST, "§c[ §f1 §c] §f5번 창고", (short) 0, " ", " §8-  §f클릭 시 5번 창고를 엽니다.", " "));
        else
            selChest.setItem(4, DayZItem.createItemStack(Material.BARRIER, "§c[ §f1 §c] §f5번 창고", (short) 0, " ", " §8-  §f클릭 시 5번 창고를 구매합니다.", " §8-  §f가격은 §67500§f원 입니다.", " "));
        p.openInventory(selChest);
    }

    public static void openChest(Player p, Number value) {
        switch (value) {
            case ONE:
                if (chest.containsKey(p.getUniqueId()))
                    p.openInventory(chest.get(p.getUniqueId()));
                else {
                    Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 1");
                    chest.put(p.getUniqueId(), inventory);
                    p.openInventory(inventory);
                }
                break;
            case TWO:
                if (chest2.containsKey(p.getUniqueId()))
                    p.openInventory(chest2.get(p.getUniqueId()));
                else {
                    Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 2");
                    chest2.put(p.getUniqueId(), inventory);
                    p.openInventory(inventory);
                }
                break;
            case THREE:
                p.openInventory(chest3.get(p.getUniqueId()));
                break;
            case FOUR:
                p.openInventory(chest4.get(p.getUniqueId()));
                break;
            case FIVE:
                p.openInventory(chest5.get(p.getUniqueId()));
                break;
            default:
                break;
        }
    }

    public static ArrayList<String> toConfig(Inventory inventory) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null || stack.getType() == Material.AIR)
                list.add("AIR");
            else if (CrackShotApi.getCSID(stack) != null)
                list.add(CrackShotApi.getCSID(stack));
            else if (stack.getType() == Material.NAME_TAG)
                list.add(stack.getItemMeta().hasDisplayName() ? stack.getType() + ";" + stack.getAmount() + ";" + stack.getItemMeta().getDisplayName() : stack.getType() + ";" + stack.getAmount());
            else
                list.add(stack.getType() + ";" + stack.getAmount());
        }
        return list;
    }

    public static void toInventory(UUID key, ArrayList<String> list, Number value) {
        Inventory inv;
        switch (value) {
            case ONE:
                inv = Bukkit.createInventory(null, 54, "§0창고 1");
                break;
            case TWO:
                inv = Bukkit.createInventory(null, 54, "§0창고 2");
                break;
            case THREE:
                inv = Bukkit.createInventory(null, 54, "§0창고 3");
                break;
            case FOUR:
                inv = Bukkit.createInventory(null, 54, "§0창고 4");
                break;
            default:
            case FIVE:
                inv = Bukkit.createInventory(null, 54, "§0창고 5");
                break;
        }
        for (int i = 0; i < 54; i++) {
            String val = list.get(i);
            if (val.contains("AIR"))
                continue;
            if (val.contains(";")) {
                String[] array = val.split(";");
                String item = array[0];
                int amount = array.length >= 2 ? toInt(array[1]) : 1;
                if (Material.getMaterial(item) != null) {
                    Material material = Material.getMaterial(item);
                    if (DayZItem.ITEM_LIST.contains(material)) {
                        ItemStack result;
                        switch (material) {
                            case PAPER:
                                result = DayZItem.HEAL.clone();
                                break;
                            case MAGMA_CREAM:
                                result = DayZItem.DRINK.clone();
                                break;
                            case SUGAR:
                                result = DayZItem.LARGE_HEAL.clone();
                                break;
                            case IRON_NUGGET:
                                result = DayZItem.BITO.clone();
                                break;
                            case GOLD_NUGGET:
                                result = DayZItem.RIBBLE.clone();
                                break;
                            case GOLD_INGOT:
                                result = DayZItem.DOGE.clone();
                                break;
                            case NAME_TAG:
                                if (array.length == 3) {
                                    result = new ItemStack(Material.NAME_TAG);
                                    ItemMeta meta = result.getItemMeta();
                                    meta.setDisplayName(array[2]);
                                    result.setItemMeta(meta);
                                }
                                else
                                    result = DayZItem.NORMAL_TAG.clone();
                                break;
                            case TRAPPED_CHEST:
                                result = DayZItem.RANDOM_BOX.clone();
                                break;
                            default:
                            case END_ROD:
                                result = DayZItem.CURE;
                                break;
                        }
                        result.setAmount(amount);
                        inv.setItem(i, result);
                    }
                    else
                        inv.setItem(i, new ItemStack(material, amount));
                }
                else if (CrackShotApi.getCSWeapon(item) != null)
                    inv.setItem(i, CrackShotApi.getCSWeapon(val));
            }
            else {
                if (Material.getMaterial(val) != null)
                    inv.setItem(i, new ItemStack(Material.getMaterial(val)));
                else if (CrackShotApi.getCSWeapon(val) != null)
                    inv.setItem(i, CrackShotApi.getCSWeapon(val));
            }
        }
        switch (value) {
            case ONE:
                chest.put(key, inv);
                break;
            case TWO:
                chest2.put(key, inv);
                break;
            case THREE:
                chest3.put(key, inv);
                break;
            case FOUR:
                chest4.put(key, inv);
                break;
            case FIVE:
                chest5.put(key, inv);
                break;
        }

    }

    public static int toInt(String value) {
        try {
            return Integer.parseInt(value);
        }
        catch (Exception e) {
            return 1;
        }
    }
}
