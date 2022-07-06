package org.light.dayz.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.light.source.DeathMatch;

public class BugCommand implements CommandExecutor {

    private DeathMatch Plugin;

    public BugCommand(DeathMatch Plugin) {
        this.Plugin = Plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            p.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
            Bukkit.getScheduler().runTaskLater(Plugin, () -> p.getInventory().setHelmet(new ItemStack(Material.AIR)), 1L);
        }
        return false;
    }
}
