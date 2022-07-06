package org.light.source.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class HologramCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (strings.length == 2 && (strings[0].equalsIgnoreCase("생성") || strings[0].equalsIgnoreCase("삭제"))) {
                String text = ChatColor.translateAlternateColorCodes('&', strings[1].replace("%", " "));
                if (strings[0].equalsIgnoreCase("생성")) {
                    ArmorStand stand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
                    stand.setCustomNameVisible(true);
                    stand.setCustomName(text);
                    stand.setInvulnerable(true);
                    stand.setGravity(false);
                    stand.setVisible(false);
                    p.sendMessage("§b아머 스탠드가 정상적으로 생성 되었습니다.");
                }
                else {
                    for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(), 15,15,15)) {
                        if (entity.getCustomName() != null && entity.getCustomName().contains(text) && entity instanceof ArmorStand) {
                            entity.remove();
                            p.sendMessage("§c해당 단어가 포함되는 아머스탠드가 제거되었습니다.");
                        }
                    }
                }
            }
            else
                p.sendMessage("§6[ §f! §6] §f/hd <생성/삭제> <문장/포함되는 단어> (띄어쓰기는 %로)");
            return true;
        }
        return false;
    }
}
