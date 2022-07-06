package org.light.dayz.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.light.source.Game.GameManager;

public class TrashCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (!GameManager.getInstance().contains(p.getUniqueId()))
                p.openInventory(Bukkit.createInventory(null, 54, "§0쓰레기통"));
            return true;
        }
        return false;
    }
}
