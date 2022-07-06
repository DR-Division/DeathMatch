package org.light.source.Command;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.light.source.Game.GameManager;
import org.light.source.Game.KillDeathManager;
import org.light.source.Singleton.InventoryFactory;
import org.light.source.Singleton.KillDeathObject;
import org.light.source.Singleton.RankObject;

import java.util.*;

public class KillDeathCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player target = (Player) commandSender;
            if (s.equalsIgnoreCase("킬뎃") || s.equalsIgnoreCase("kd")) {
                //자신 킬뎃 확인
                KillDeathObject object = KillDeathManager.getInstance().getValue(target.getUniqueId());
                target.sendMessage(" ");
                if (object.getDeath() != 0)
                    target.sendMessage(" §4§lKill §7: §c" + object.getKill() + "  §8§lDeath §7: §f" + object.getDeath() + "   §cK§7/§8D §7: " + Math.floor(((double) object.getKill() / object.getDeath()) * 10.0) / 10.0);
                else
                    target.sendMessage(" §4§lKill §7: §c" + object.getKill() + "  §8§lDeath §7: §f" + object.getDeath() + "   §cK§7/§8D §7: " + Math.floor(((double) object.getKill() / 1) * 10.0) / 10.0);
                target.sendMessage(" ");
            }
            else if (s.equalsIgnoreCase("랭크") || s.equalsIgnoreCase("rank")) {
                //gui 띄워주기
                if (GameManager.getInstance().contains(target.getUniqueId()))
                    target.sendMessage("§c게임 참여 도중에는 확인이 불가능합니다.");
                else
                    createInventory(target);
            }
            return true;
        }
        return false;
    }

    public void createInventory(Player p) {
        int i;
        ItemStack dummy, kill, kd;
        dummy = createDummyItem();
        kill = InventoryFactory.createItemStack(Material.BLUE_STAINED_GLASS_PANE, "§b킬 랭킹 페이지", new String[]{"", " §7- §b킬 랭킹 페이지 입니다.", " "}, (short) 0);
        kd = InventoryFactory.createItemStack(Material.RED_STAINED_GLASS_PANE, "§c킬뎃 랭킹 페이지", new String[]{"", " §7- §c킬뎃 랭킹 페이지 입니다.", " "}, (short) 0);
        Inventory inventory = InventoryFactory.createInventory("§0랭크", 54);
        for (i = 45; i < 54; i++)
            inventory.setItem(i, dummy);
        inventory.setItem(47, kill);
        inventory.setItem(51, kd);
        i = 0;
        for (ItemStack stack : createKillRank())
            inventory.setItem(i++, stack);
        p.openInventory(inventory);
    }

    public ItemStack createDummyItem() {
        return InventoryFactory.createItemStack(Material.GRAY_STAINED_GLASS_PANE, "§f", null, (short) 0);
    }

    public static ObjectArrayList<ItemStack> createKillRank() {
        int i = 0;
        Object2ObjectOpenHashMap<UUID, KillDeathObject> objects = KillDeathManager.getInstance().getList();
        ObjectArrayList<RankObject> list = new ObjectArrayList<>();
        ObjectArrayList<ItemStack> stackList = new ObjectArrayList<>();
        for (UUID object : objects.keySet())
            list.add(new RankObject(object, objects.get(object)));
        //kill
        list.sort(Comparator.comparingInt(o -> o.kill));
        Collections.reverse(list);
        for (RankObject object : list) {
            int kill, death;
            String name;
            name = object.name == null ? "§cOffline" : "§b" + object.name;
            kill = object.kill;
            death = object.death;
            stackList.add(InventoryFactory.createItemStack(Material.SIGN, name, new String[]{" ", " §7- §4Kill §7: §c" + kill + " §4§lKills", " §7- §8Death §7: §f" + death + " §c§lDeaths", " "}, (short) 0));
            i++;
            if (i == 45)
                break;

        }
        return stackList;
    }

    public static ObjectArrayList<ItemStack> createKillDeathRank() {
        int i = 0;
        Object2ObjectOpenHashMap<UUID, KillDeathObject> objects = KillDeathManager.getInstance().getList();
        ObjectArrayList<RankObject> list = new ObjectArrayList<>();
        ObjectArrayList<ItemStack> stackList = new ObjectArrayList<>();
        for (UUID object : objects.keySet())
            list.add(new RankObject(object, objects.get(object)));
        //kill
        list.sort((o1, o2) -> {
            int death, death1;
            death = o1.death == 0 ? 1 : o1.death;
            death1 = o2.death == 0 ? 1 : o2.death;
            return Double.compare((double) o1.kill / death, (double) o2.kill / death1);
        });
        Collections.reverse(list);
        for (RankObject object : list) {
            int kill, death;
            double kd;
            String name;
            name = object.name == null ? "§cOffline" : "§b" + object.name;
            kill = object.kill;
            death = object.death;
            if (death == 0)
                death = 1;
            kd = Math.floor(((double) kill / death) * 10) / 10.0;
            stackList.add(InventoryFactory.createItemStack(Material.SIGN, name, new String[]{" ", " §7- §4Kill §7: §c" + kill + " §4§lKills", " §7- §8Death §7: §f" + death + " §c§lDeaths", " §7- §cK§7/§8D §7: §c" + kd, " "}, (short) 0));
            i++;
            if (i == 45)
                break;

        }

        return stackList;
    }
}
