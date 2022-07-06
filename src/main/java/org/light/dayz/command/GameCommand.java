package org.light.dayz.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.light.dayz.data.Scav;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.dayz.util.DayZItem;
import org.light.dayz.util.Regen;
import org.light.dayz.util.ScavRegistry;
import org.light.source.Game.GameManager;
import org.light.source.Singleton.CrackShotApi;
import org.light.source.Singleton.DataManager;

import java.util.*;

public class GameCommand implements CommandExecutor {

    private final YamlConfig config;
    public static ArrayList<UUID> giftMap = new ArrayList<>();

    public GameCommand(YamlConfig config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (strings.length == 1 && strings[0].equalsIgnoreCase("참여"))
                GameController.addPlayer(p);
            else if (strings.length == 1 && strings[0].equalsIgnoreCase("독택통합"))
                giveTags(p);
            else if (strings.length == 1 && strings[0].equalsIgnoreCase("보상"))
                giveGift(p);
            else {
                if (!p.isOp())
                    p.sendMessage("§c[ §f! §c] §f/dayz [참여/독택통합/보상]");
                else {
                    if (strings.length == 1 && strings[0].equalsIgnoreCase("목록"))
                        locationInfo(p);
                    else if (strings.length == 1 && strings[0].equalsIgnoreCase("추가")) {
                        config.getLocations().add(p.getLocation());
                        p.sendMessage("§b현재 위치가 랜덤스폰 장소에 추가되었습니다.");
                    }
                    else if (strings.length == 2 && strings[0].equalsIgnoreCase("삭제") && parseInt(strings[1]) != -1 && config.getLocations().size() > parseInt(strings[1])) {
                        config.getLocations().remove(parseInt(strings[1]));
                        p.sendMessage("§b해당 인덱스의 위치가 제거되었습니다.");
                    }
                    else if (strings.length == 2 && strings[0].equalsIgnoreCase("이동") && parseInt(strings[1]) != -1 && config.getLocations().size() > parseInt(strings[1])) {
                        p.teleport(config.getLocations().get(parseInt(strings[1])));
                        p.sendMessage("§6해당 위치로 이동하였습니다.");
                    }
                    else if (strings.length == 1 && strings[0].equalsIgnoreCase("저장")) {
                        config.save();
                        p.sendMessage("§c콘피그가 저장되었습니다.");
                    }
                    else if (strings.length == 1 && strings[0].equalsIgnoreCase("리로드")) {
                        config.load();
                        p.sendMessage("§b콘피그가 로드되었습니다.");
                    }
                    else if (strings.length == 1 && strings[0].equalsIgnoreCase("초기화")) {
                        Regen.clear();
                        p.sendMessage("§b보급품 맵이 초기화되었습니다.");
                    }
                    //dayz 스캡추가 이름 체력 스캡삭제 이름 스캡목록 스캡생성
                    else if (strings.length == 3 && strings[0].equalsIgnoreCase("스캡추가")) {
                        if (p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getType() == Material.AIR || CrackShotApi.getCSID(p.getInventory().getItemInMainHand()) == null)
                            p.sendMessage("§4총을 들고 진행해주세요.");
                        try {
                            ScavRegistry.addScav(strings[1], CrackShotApi.getCSID(p.getInventory().getItemInMainHand()), p.getLocation(), Double.parseDouble(strings[2]), p.getInventory().getHelmet(), p.getInventory().getChestplate(), p.getInventory().getLeggings(), p.getInventory().getBoots());
                            p.sendMessage(strings[1] + "스캐브 생성됨.");
                        }
                        catch (NumberFormatException e) {
                            p.sendMessage("§4숫자만 입력해주세요.");
                        }
                    }
                    else if (strings.length == 2 && strings[0].equalsIgnoreCase("스캡삭제")) {
                        ScavRegistry.removeScav(strings[1]);
                        p.sendMessage("§4해당 스캐브가 삭제된지는 모르겠지만 진행은 하였음.");
                    }
                    else if (strings.length == 1 && strings[0].equalsIgnoreCase("스캡목록")) {
                        for (Scav scav : ScavRegistry.scavArrayList)
                            p.sendMessage(scav.toString());
                    }
                    else if (strings.length == 1 && strings[0].equalsIgnoreCase("스캡생성")) {
                        ScavRegistry.spawnScavs();
                    }
                    else
                        p.sendMessage("§c[ §f! §c] §f/dayz [참여/목록/추가/삭제/이동/저장/초기화/리로드]");
                }
            }
            return true;
        }
        return false;
    }

    public void locationInfo(Player p) {
        ArrayList<Location> locations = config.getLocations();
        if (locations.size() != 0) {
            p.sendMessage("§b맨 윗줄부터 인덱스 0번입니다.");
            p.sendMessage(" ");
            locations.forEach(data -> p.sendMessage(locationToString(data)));
        }
        else
            p.sendMessage("§c지정된 데이터가 없습니다.");
    }

    public String locationToString(Location loc) {
        return "§7[ §fX : " + Math.round(loc.getX()) + ", Y : " + Math.round(loc.getY()) + ", Z : " + Math.round(loc.getZ()) + ", World : " + loc.getWorld().getName() + " §7]";
    }

    public int parseInt(String val) {
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }



    public static void giveTags(Player p) {
        int amount = getSpecificAmount(p.getInventory(), Material.NAME_TAG);
        if (amount == 0)
            p.sendMessage("§c[ §f! §c] §f인식표가 인벤토리에 존재하지 않습니다.");
        else {
            removeSpecificItem(p.getInventory(), Material.NAME_TAG, amount);
            ItemStack clone = DayZItem.NORMAL_TAG.clone();
            clone.setAmount(amount);
            p.getInventory().addItem(clone);
            p.sendMessage("§c[ §f! §c] §b교환 완료!");
        }
    }

    public static void giveGift(Player p) {
        if (giftMap.contains(p.getUniqueId()))
            p.sendMessage("§c[ §f! §c] §c이미 보상을 받으셨습니다.");
        else {
            ItemStack gift = DayZItem.RANDOM_BOX.clone();
            gift.setAmount(10);
            p.getInventory().addItem(gift);
            giftMap.add(p.getUniqueId());
            p.sendMessage("§c[ §f! §c] §f보상이 지급되었습니다.");
        }
    }

    public static int getSpecificAmount(Inventory inventory, Material type) {
        int amount = 0;
        for (ItemStack stack : inventory.getContents())
            if (stack != null && stack.getType() == type)
                amount += stack.getAmount();
        return amount;
    }


    public static void removeSpecificItem(Inventory inventory, Material data, int amount) {
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
}
