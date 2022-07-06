package org.light.dayz.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DayZItem {

    public static final ItemStack DRINK = createItemStack(Material.MAGMA_CREAM, "§c[ §f! §c] §f에너지 드링크", (short)0, 1," ", " §8-  §f사용시 이동속도가 증가합니다. (사용시간 3초)", " ");
    public static final ItemStack HEAL = createItemStack(Material.PAPER, "§c[ §f! §c] §f붕대", (short) 0, 1," ", " §8-  §f사용 시 체력을 일부 회복합니다. (사용시간 3초)", " ");
    public static final ItemStack LARGE_HEAL =  createItemStack(Material.SUGAR, "§c[ §f! §c] §f구급상자", (short)0,1, " ", " §8-  §f사용시 체력을 모두 회복합니다. (사용시간 5초)", " ");
    public static final ItemStack CURE =  createItemStack(Material.END_ROD, "§c[ §f! §c] §f치료제", (short)0, 1," ", " §8-  §f골절도 치료가능한 치료제", " §8-  §f사용시 상태이상을 모두 제거합니다. (사용시간 3초)", " ");
    public static final List<ItemStack> EATABLE = new ArrayList<>(Arrays.asList(new ItemStack(Material.BREAD, 5), new ItemStack(Material.COOKED_BEEF, 2), new ItemStack(Material.APPLE, 10), new ItemStack(Material.COOKED_PORKCHOP, 3), new ItemStack(Material.BEETROOT, 10), new ItemStack(Material.COOKED_CHICKEN, 3)));
    public static final ItemStack BITO = createItemStack(Material.IRON_NUGGET, "§6[ §f! §6] §f비토코인", (short)0, " ", " §8-  §f개당 10원의 가치를 하는 코인이다.", " §8-  §f왠지 예전에는 가치가 높았던 것 같아보인다.", " ");
    public static final ItemStack RIBBLE = createItemStack(Material.GOLD_NUGGET, "§6[ §f! §6] §b리블", (short)0, " ", " §8-  §f개당 100원의 가치를 하는 코인이다.", " ");
    public static final ItemStack DOGE = createItemStack(Material.GOLD_INGOT, "§6[ §f! §6] §6골든도지", (short)0, " ", " §8-  §f개당 1000원의 가치를 하는 코인이다.", " §8-  §f안타깝게도 화성에 가지 못했다.", " ");
    public static final ItemStack NORMAL_TAG = createItemStack(Material.NAME_TAG, "§6[ §f! §6] §c인식표", (short)0, " ", " §8-  §f통합하기 쉽게 변경된 인식표다.", " ");
    public static final ItemStack RANDOM_BOX = createItemStack(Material.TRAPPED_CHEST, "§b[ §f! §b] §6랜덤 총기 박스", (short)0, " ", " §8-  §f랜덤한 총기가 등장하는 박스로, 일정 확률로 스킨 총기가 등장합니다.", " ");
    public static final ArrayList<Material> ITEM_LIST = getList();

    public static ItemStack createItemStack(Material data, String name, short color, String... lore) {
        ItemStack stack = new ItemStack(data);
        ItemMeta meta = stack.getItemMeta();
        stack.setDurability(color);
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createItemStack(Material data, String name, short color, int amount, String... lore) {
        ItemStack stack = new ItemStack(data);
        ItemMeta meta = stack.getItemMeta();
        stack.setDurability(color);
        stack.setAmount(amount);
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    private static ArrayList<Material> getList() {
        ArrayList<Material> materials = new ArrayList<>();
        materials.add(DRINK.getType());
        materials.add(HEAL.getType());
        materials.add(LARGE_HEAL.getType());
        materials.add(CURE.getType());
        materials.add(BITO.getType());
        materials.add(RIBBLE.getType());
        materials.add(DOGE.getType());
        materials.add(Material.NAME_TAG);
        materials.add(RANDOM_BOX.getType());
        return materials;
    }
}
