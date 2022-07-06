package org.light.dayz.command;

import com.sun.istack.internal.NotNull;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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

import java.util.HashMap;
import java.util.UUID;

public class TradeCommand implements CommandExecutor {

    public static HashMap<UUID, UUID> tradeMap = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // /거래 DR_Division -> /거래 수락
        if (strings.length == 1 && !strings[0].equalsIgnoreCase(commandSender.getName()) && (strings[0].equalsIgnoreCase("수락") || Bukkit.getPlayer(strings[0]) != null || strings[0].equalsIgnoreCase("취소"))) {
            Player p = (Player) commandSender;
            if (strings[0].equalsIgnoreCase("수락")) {
                if (checkRequest(p)) {
                    Player target = Bukkit.getPlayer(getRequester(p.getUniqueId()));
                    if (GameController.contains(p.getUniqueId()) || GameManager.getInstance().contains(p.getUniqueId()) || GameController.contains(target.getUniqueId()) || GameManager.getInstance().contains(target.getUniqueId()) || tradeMap.containsKey(p.getUniqueId()) || getRequester(target.getUniqueId()) != null)
                        p.sendMessage("§c현재 수락가능한 상태가 아닙니다.");
                    else
                        setGUI(target, p);
                }
            }

            else if (strings[0].equalsIgnoreCase("취소")) {
                removeTrade(p);
                p.sendMessage("§c[ §f! §c] §f제거 완료");
            }
            else {
                Player target = Bukkit.getPlayer(strings[0]);
                if (GameController.contains(p.getUniqueId()) || GameManager.getInstance().contains(p.getUniqueId()) || GameController.contains(target.getUniqueId()) || GameManager.getInstance().contains(target.getUniqueId()) || tradeMap.containsKey(p.getUniqueId()) || tradeMap.containsKey(target.getUniqueId()) || getRequester(p.getUniqueId()) != null || getRequester(target.getUniqueId()) != null)
                    p.sendMessage("§c현재 수락가능한 상태가 아닙니다.");
                else {
                    tradeMap.put(p.getUniqueId(), target.getUniqueId());
                    p.sendMessage("§c[ §f! §c] §b" + target.getName() + "§f님에게 거래 요청을 넣었습니다.");
                    target.sendMessage("§6[ §f! §6] §b" + p.getName() + "§f님이 당신에게 거래 요청을 보냈습니다.");
                }
            }
        }
        else
            commandSender.sendMessage("§c[ §f! §c] §f/거래 [닉네임/수락/취소]");

        return false;
    }

    public static void removeTrade(Player p) {
        tradeMap.remove(p.getUniqueId());
        tradeMap.remove(getRequester(p.getUniqueId()));

    }
    public boolean checkRequest(Player p) {
        if (getRequester(p.getUniqueId()) != null && Bukkit.getPlayer(getRequester(p.getUniqueId())) != null)
            return true;
        else {
            p.sendMessage("§c[ §f! §c] §f수락가능한 요청이 없습니다.");
            if (getRequester(p.getUniqueId()) != null)
                tradeMap.remove(getRequester(p.getUniqueId()));
            return false;
        }
    }

    public static UUID getRequester(UUID value) {
        UUID result = null;
        for (UUID it : tradeMap.keySet()) {
            if (tradeMap.get(it).equals(value))
                result = it;
        }
        return result;
    }

    public void setGUI(Player one, Player two) {
        Inventory inv = Bukkit.createInventory(null, 27, "§0거래");
        inv.setItem(9, DayZItem.createItemStack(Material.IRON_BARS, " ", (short)0, " "));
        inv.setItem(10, DayZItem.createItemStack(Material.OAK_BUTTON, "§8[ §f! §8] §b▲ " + one.getName() + " ▲", (short)0, " ", " §8-  §f현재 상대방의 아이템을 확인중입니다.", " §8-  §f클릭시 상대방의 교환창을 잠급니다.", " "));
        inv.setItem(11, DayZItem.createItemStack(Material.IRON_BARS, " ", (short)0, " "));
        inv.setItem(12, DayZItem.createItemStack(Material.IRON_BARS, " ", (short)0, " "));
        inv.setItem(13, DayZItem.createItemStack(Material.IRON_BARS, " ", (short)0, " "));
        inv.setItem(14, DayZItem.createItemStack(Material.IRON_BARS, " ", (short)0, " "));
        inv.setItem(15, DayZItem.createItemStack(Material.IRON_BARS, " ", (short)0, " "));
        inv.setItem(16, DayZItem.createItemStack(Material.OAK_BUTTON, "§8[ §f! §8] §c▼ §6" + two.getName() + " §c▼", (short)0, " ", " §8-  §f현재 상대방의 아이템을 확인중입니다."," §8-  §f클릭시 상대방의 교환창을 잠급니다.", " "));
        inv.setItem(17, DayZItem.createItemStack(Material.IRON_BARS, " ", (short)0, " "));;
        one.openInventory(inv);
        two.openInventory(inv);
    }
}
