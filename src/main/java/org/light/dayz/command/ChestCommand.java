package org.light.dayz.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.light.dayz.game.GameController;
import org.light.dayz.util.VirtualChest;
import org.light.source.Game.GameManager;

public class ChestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (GameController.contains(p.getUniqueId()) || GameManager.getInstance().contains(p.getUniqueId()))
                p.sendMessage("§4게임에 참여 중인 상태로 사용할 수 없는 명령어입니다.");
            else
                VirtualChest.selectChest(p);
        }
        return false;
    }
}
