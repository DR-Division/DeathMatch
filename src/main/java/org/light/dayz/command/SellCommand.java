package org.light.dayz.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.light.dayz.game.GameController;
import org.light.dayz.util.DayZItem;
import org.light.source.Game.GameManager;

public class SellCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (GameManager.getInstance().contains(p.getUniqueId()) || GameController.contains(p.getUniqueId()))
                p.sendMessage("§4게임 도중엔 사용할 수 없는 명령어 입니다.");
            else
                openSellGUI(p);
            return true;
        }
        return false;
    }

    public void openSellGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 18, "§0상점");
        inv.setItem(0, DayZItem.createItemStack(Material.ROTTEN_FLESH, "§c[ §f! §c] §f썩은 고기 판매", (short) 0, " ", " §8-  §f썩은 고기를 1개당 1원에 판매합니다.", " §8-  §7[ §f좌클릭 §7] §f1개 판매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f모두 판매", " §8-  §7[ §f우클릭 §7] §f10개 판매", " "));
        inv.setItem(1, DayZItem.createItemStack(Material.DIAMOND_SWORD, "§c[ §f! §c] §f총기 판매", (short) 0, " ", " §8-  §f총을 1개 당 썩은 고기 1~5개(스킨의 경우 5~15개)에 판매합니다.", "§c (스킨 취급받는 일반 무기가 존재할 수 있습니다.)", " §8-  §7[ §f좌클릭 §7] §f손에 든 총기 판매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f모두 판매", " "));
        inv.setItem(2, DayZItem.createItemStack(Material.BREAD, "§c[ §f! §c] §f빵 구매", (short) 0, " ", " §8-  §f빵을 개당 5원에 구매합니다. (인벤토리 비우고 사용하세요)", " §8-  §7[ §f좌클릭 §7] §f빵 1개 구매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f빵 64개 구매", " §8-  §7[ §f우클릭 §7] §f빵 10개 구매", " "));
        inv.setItem(3, DayZItem.createItemStack(Material.IRON_NUGGET, "§c[ §f! §c] §f비토코인 거래", (short) 0, " ", " §8-  §f비토코인을 개당 10원에 거래합니다", " §8-  §7[ §f좌클릭 §7] §f1개 구매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f10개 구매", " §8-  §7[ §f우클릭 §7] §f1개 판매"," §8-  §7[ §f쉬프트 + 우클릭 §7] §f10개 판매", " ", " "));
        inv.setItem(4, DayZItem.createItemStack(Material.GOLD_NUGGET, "§c[ §f! §c] §f리블 거래", (short) 0, " ", " §8-  §f리블을 개당 100원에 거래합니다", " §8-  §7[ §f좌클릭 §7] §f1개 구매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f10개 구매", " §8-  §7[ §f우클릭 §7] §f1개 판매"," §8-  §7[ §f쉬프트 + 우클릭 §7] §f10개 판매", " ", " "));
        inv.setItem(5, DayZItem.createItemStack(Material.GOLD_INGOT, "§c[ §f! §c] §f골든도지 거래", (short) 0, " ", " §8-  §f골든도지를 개당 1000원에 거래합니다", " §8-  §7[ §f좌클릭 §7] §f1개 구매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f10개 구매", " §8-  §7[ §f우클릭 §7] §f1개 판매"," §8-  §7[ §f쉬프트 + 우클릭 §7] §f10개 판매", " ", " "));
        inv.setItem(6, DayZItem.createItemStack(Material.TRAPPED_CHEST, "§c[ §f! §c] §f랜덤 총기 상자 교환", (short) 0, " ", " §8-  §f인식표 9개에 랜덤 총기 상자(일정 확률로 스킨 총기 등장) 1개와 교환합니다.", " §8-  §7[ §f좌클릭 §7] §f1개 구매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f10개 구매", " ", " "));

        p.openInventory(inv);
    }



}
