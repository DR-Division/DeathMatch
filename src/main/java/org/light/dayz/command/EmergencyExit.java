package org.light.dayz.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.light.dayz.event.ChestInteraction;
import org.light.dayz.game.GameController;
import org.light.dayz.runnable.EmergencyExitRunnable;
import org.light.dayz.util.Regen;
import org.light.source.DeathMatch;
import org.light.source.Singleton.DataManager;

public class EmergencyExit implements CommandExecutor {

    private DeathMatch Plugin;

    public EmergencyExit(DeathMatch Plugin) {
        this.Plugin = Plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (GameController.contains(p.getUniqueId()) && DataManager.getInstance().getLocation() != null) {
                if (ChestInteraction.exitTime.containsKey(p.getUniqueId()))
                    p.sendMessage("§c[ §f! §c] §c이미 탈출 중입니다.");
                else if (Regen.isExitPlayer(p.getUniqueId()))
                    p.sendMessage("§c[ §f! §c] §b이미 사용한 명령어입니다.");
                else {
                    Regen.addExitPlayer(p.getUniqueId());
                    ChestInteraction.exitTime.put(p.getUniqueId(), 0);
                    p.sendMessage("§c[ §f! §c] §f10 블록 이상 떨어지지 마세요.");
                    new EmergencyExitRunnable(p.getLocation(), p.getUniqueId()).runTaskTimer(Plugin, 0L, 20L);
                }

            }
            return true;
        }
        return false;
    }
}
