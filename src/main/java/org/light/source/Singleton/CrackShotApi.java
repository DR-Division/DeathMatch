package org.light.source.Singleton;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSUtility;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.data.YamlConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CrackShotApi {

    private static CSUtility utility;
    private static Map<String, ItemStack> csItems;
    private static HashMap<String, ItemStack> nonSkinItems;

    static {
        utility = new CSUtility();
        csItems = new HashMap<>();
        nonSkinItems = new HashMap<>();
    }

    public static ItemStack getCSWeapon(String WeaponName){
        return utility.generateWeapon(WeaponName);
    }

    public static String getCSID(ItemStack Weapon){
        return utility.getWeaponTitle(Weapon);
    }

    public CSUtility getUtility(){
        return utility;
    }

    public static CSDirector getPlugin(){
        return utility.getHandle();
    }

    public static ItemStack generateRandomWeapon(){
        int random = ThreadLocalRandom.current().nextInt(1, csItems.size());
        ArrayList<String> list = new ArrayList<>(csItems.keySet());
        ItemStack weapon = csItems.get(list.get(random));
        if (weapon == null)
            weapon = csItems.get(list.get(0));
        return weapon;
    }

    public static ItemStack generateNotOPWeapon(){
        ArrayList<String> list = new ArrayList<>(csItems.keySet());
        Collections.shuffle(list);
        while(list.get(0) == null || list.get(0).equalsIgnoreCase("CHICKENGUN") || list.get(0).equalsIgnoreCase("C4_Ultimate_Nerf") || list.get(0).equalsIgnoreCase("DEAGLE_Golden") || list.get(0).equalsIgnoreCase("C4_Ultimate") || list.get(0).contains("Special"))
            Collections.shuffle(list);
        return csItems.get(list.get(0));
    }

    public static ItemStack generateDayZWeapon() {
        if (YamlConfig.instance.getDenyWeapon().size() == 0)
            return generateNotOPWeapon();
        else {
            int rand = ThreadLocalRandom.current().nextInt(0, nonSkinItems.size());
            String key = new ArrayList<>(nonSkinItems.keySet()).get(rand);
            return nonSkinItems.get(key);
        }
    }

    public static void generateWeaponMap() {
        csItems.clear();
        nonSkinItems.clear();
        for (String weapon : getPlugin().wlist.values())
            csItems.put(weapon, getCSWeapon(weapon));
        for (String weapons : getPlugin().wlist.values().stream().filter(data -> data != null && !data.contains("_") && !YamlConfig.instance.getDenyWeapon().contains(data)).collect(Collectors.toList()))
            nonSkinItems.put(weapons, getCSWeapon(weapons));
    }

}
