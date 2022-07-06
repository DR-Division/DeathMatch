package org.light.source.Command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.light.dayz.game.GameController;
import org.light.source.DeathMatch;
import org.light.source.Game.GameManager;
import org.light.source.Game.UserMananger;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Singleton.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandController implements CommandExecutor {

    private final DeathMatch plugin;
    private static final String first;

    static {
        first = "§c[ §fDeathMatch §6] ";
    }

    public CommandController(DeathMatch plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length >= 1 && correctArg(args[0])) {
                if (args[0].equalsIgnoreCase("참여")) {
                    if (GameManager.getInstance().contains(p.getUniqueId()) || GameController.contains(p.getUniqueId())) {
                        p.sendMessage(first + "§c이미 참여중입니다.");
                    }
                    else {
                        GameManager.getInstance().addPlayer(p);
                        MinimizeLogger.getInstance().appendLog(p.getName() + "님이 데스매치에 참여함");
                    }
                }
                else if (args[0].equalsIgnoreCase("정보")) {
                    currentGameInfo(p);
                }
                else if (args[0].equalsIgnoreCase("나가기")) {
                    if (GameManager.getInstance().contains(p.getUniqueId())) {
                        GameManager.getInstance().removePlayer(p);
                        MinimizeLogger.getInstance().appendLog(p.getName() + "님이 데스매치에서 퇴장함");
                    }
                    else {
                        p.sendMessage(first + "§6게임에 참여하지 않으셨습니다.");
                    }
                }
                else {
                    if (p.hasPermission("DeathMatch.Control")) {
                        if (args[0].equalsIgnoreCase("관리")) {
                            if (args.length >= 2 && (args[1].equalsIgnoreCase("확인") || args[1].equalsIgnoreCase("추방"))) {
                                if (args[1].equalsIgnoreCase("확인")) {
                                    currentDataInfo(p);
                                }
                                else if (args[1].equalsIgnoreCase("추방")) {
                                    if (args.length == 3) {
                                        Player target = Bukkit.getServer().getPlayer(args[2]);
                                        if (target == null || !GameManager.getInstance().contains(target.getUniqueId()))
                                            p.sendMessage(first + "§4해당 플레이어는 온라인이 아니거나 게임에 참여하지 않은 상태입니다.");
                                        else {
                                            GameManager.getInstance().removePlayer(target);
                                            MinimizeLogger.getInstance().appendLog(p.getName() + "님이 " + target.getName() + "님을 추방함");
                                        }
                                    }
                                    else {
                                        p.sendMessage(first + "§c추방할 플레이어를 입력해주세요");
                                    }
                                }
                            }
                            else {
                                p.sendMessage(first + "§b/데스매치 관리 <확인/추방>");
                            }
                        }
                        else if (args[0].equalsIgnoreCase("설정")) {
                            if (args.length >= 2 && (args[1].equalsIgnoreCase("라운드") || args[1].equalsIgnoreCase("킬") || args[1].equalsIgnoreCase("시간") || args[1].equalsIgnoreCase("최소인원") || args[1].equalsIgnoreCase("위치") || args[1].equalsIgnoreCase("월드추가") || args[1].equalsIgnoreCase("월드삭제") || args[1].equalsIgnoreCase("월드이동"))) {
                                if (args[1].equalsIgnoreCase("라운드")) {
                                    if (args.length != 3)
                                        p.sendMessage(first + "§c/" + s + " 설정 라운드 <수치>");
                                    else {
                                        try {
                                            int value = Integer.parseInt(args[2]);
                                            if (value <= 0)
                                                p.sendMessage(first + "§c수치는 0이하의 값으로 설정할 수 없습니다..");
                                            else {
                                                DataManager.getInstance().setRounds(value);
                                                p.sendMessage(first + "§f데스매치 라운드 수가 §6" + value + "§f라운드로 지정되었습니다.");
                                            }
                                        }
                                        catch (NumberFormatException e) {
                                            p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                        }
                                    }
                                }
                                else if (args[1].equalsIgnoreCase("킬")) {
                                    if (args.length != 3)
                                        p.sendMessage(first + "§c/" + s + " 설정 킬 <수치>");
                                    else {
                                        try {
                                            int value = Integer.parseInt(args[2]);
                                            if (value <= 1)
                                                p.sendMessage(first + "§c수치는 1이하의 값으로 설정할 수 없습니다..");
                                            else {
                                                DataManager.getInstance().setKilltolevel(value);
                                                p.sendMessage(first + "§f데스매치 레벨업당 킬수가 §6" + value + "§f킬로 지정되었습니다.");
                                            }
                                        }
                                        catch (NumberFormatException e) {
                                            p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                        }
                                    }
                                }
                                else if (args[1].equalsIgnoreCase("시간")) {
                                    if (args.length != 3)
                                        p.sendMessage(first + "§c/" + s + " 설정 시간 <수치>");
                                    else {
                                        try {
                                            int value = Integer.parseInt(args[2]);
                                            if (value <= 9)
                                                p.sendMessage(first + "§c수치는 9이하의 값으로 설정할 수 없습니다..");
                                            else {
                                                DataManager.getInstance().setTime(value);
                                                p.sendMessage(first + "§f데스매치 시간이 §6" + value + "§f초로 지정되었습니다.");
                                            }
                                        }
                                        catch (NumberFormatException e) {
                                            p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                        }
                                    }
                                }
                                else if (args[1].equalsIgnoreCase("최소인원")) {
                                    if (args.length != 3)
                                        p.sendMessage(first + "§c/" + s + " 설정 최소인원 <수치>");
                                    else {
                                        try {
                                            int value = Integer.parseInt(args[2]);
                                            if (value <= 1)
                                                p.sendMessage(first + "§c수치는 1이하의 값으로 설정할 수 없습니다..");
                                            else {
                                                DataManager.getInstance().setMinimumUser(value);
                                                p.sendMessage(first + "§f데스매치 최소인원이 §6" + value + "§f명 으로 지정되었습니다.");
                                            }
                                        }
                                        catch (NumberFormatException e) {
                                            p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                        }
                                    }
                                }
                                else if (args[1].equalsIgnoreCase("위치")) {
                                    if (args.length != 3)
                                        p.sendMessage(first + "§c/" + s + " 설정 위치 <1~21>, 1은 스폰");
                                    else {
                                        try {
                                            int value = Integer.parseInt(args[2]);
                                            if (value < 1 || value > 21)
                                                p.sendMessage(first + "§c위치 값은 1~21이 와야합니다.");
                                            else {
                                                DataManager.getInstance().setLocations(p.getLocation(), value);
                                                p.sendMessage(first + "§f데스매치 구역 " + value + "이 §6" + locationToString(p.getLocation()) + "§f으로 지정되었습니다.");
                                            }
                                        }
                                        catch (NumberFormatException e) {
                                            p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                        }
                                    }
                                }
                                else if (args[1].equalsIgnoreCase("월드추가")) {
                                    if (args.length != 3)
                                        p.sendMessage(first + "§c/" + s + " 설정 월드추가 이름");
                                    else {
                                        if (WorldManager.getInstance().containWorld(args[2]))
                                            p.sendMessage(first + "§c이미 추가된 월드입니다.");
                                        else {
                                            WorldManager.getInstance().addWorld(args[2]);
                                            p.sendMessage(first + "§f월드 §b" + args[2] + "§f가 추가되었습니다. 리로드시 월드가 로드됩니다.");
                                        }
                                    }
                                }
                                else if (args[1].equalsIgnoreCase("월드삭제")) {
                                    if (args.length != 3)
                                        p.sendMessage(first + "§c/" + s + " 설정 월드삭제 이름");
                                    else {
                                        if (WorldManager.getInstance().containWorld(args[2])) {
                                            WorldManager.getInstance().removeWorld(args[2]);
                                            p.sendMessage(first + "§f월드 §b" + args[2] + "§f가 제거되었습니다.");
                                        }
                                        else
                                            p.sendMessage(first + "§c존재하지 않는 월드입니다.");

                                    }
                                }
                                else if (args[1].equalsIgnoreCase("월드이동")) {
                                    if (args.length != 3)
                                        p.sendMessage(first + "§b/" + s + " 설정 월드이동 <월드>");
                                    else {
                                        if (Bukkit.getWorld(args[2]) != null)
                                            p.teleport(new Location(Bukkit.getWorld(args[2]), 0, 60, 0));
                                        else
                                            p.sendMessage(first + "§c존재하지 않는 월드입니다.");
                                    }
                                }
                            }
                            else {
                                p.sendMessage(first + "§c/" + s + " 설정 <라운드/킬/시간/최소인원/위치/월드추가/월드삭제/월드이동/저장/리로드> <값/1~21>");
                            }
                        }
                        else if (args[0].equalsIgnoreCase("강제종료")) {
                            if (GameManager.getInstance().isGaming()) {
                                Bukkit.broadcastMessage(first + "§4관리자에 의해 데스매치가 강제종료 되었습니다.");
                                GameManager.getInstance().stop();
                                MinimizeLogger.getInstance().appendLog(p.getName() + "님이 데스매치를 강제종료함");
                            }
                        }
                        else if (args[0].equalsIgnoreCase("리로드")) {
                            DataManager.getInstance().flushLocation();
                            FileManager.getInstance().load();
                            CrackShotApi.generateWeaponMap();
                            p.sendMessage(first + "§f콘피그 파일 및 무기 목록이 리로드 되었습니다.");
                        }
                        else if (args[0].equalsIgnoreCase("저장")) {
                            FileManager.getInstance().save();
                            p.sendMessage(first + "§c파일이 성공적으로 저장되었습니다.");
                        }
                    }
                    else {
                        info(p, s);
                    }
                }
            }
            else {
                info(p, s);
            }
        }
        else {
            sender.sendMessage(first + "§c콘솔은 사용 불가능한 명령어 입니다.");
        }
        return true;
    }

    public void info(Player p, String label) {
        p.sendMessage(first + "§f/" + label + " <참여/정보/나가기"
                + (p.hasPermission("DeathMatch.Control") ? "/관리/설정/강제종료/리로드/저장>" : ">"));
    }

    public boolean correctArg(String value) {
        return value.equalsIgnoreCase("참여") || value.equalsIgnoreCase("정보") || value.equalsIgnoreCase("나가기") || value.equalsIgnoreCase("관리") || value.equalsIgnoreCase("설정") || value.equalsIgnoreCase("강제종료") || value.equalsIgnoreCase("리로드") || value.equalsIgnoreCase("저장");
    }

    public void currentDataInfo(Player p) {
        p.sendMessage(" ");
        p.sendMessage(first + "§bRounds §7: §6" + DataManager.getInstance().getRounds() + "§f라운드");
        p.sendMessage(first + "§cKillToLevel §7: §6" + DataManager.getInstance().getKilltolevel() + "§f킬");
        p.sendMessage(first + "§aMaxTime §7: §6" + DataManager.getInstance().getTime() + "§f초");
        p.sendMessage(first + "§8MinimumUser §7: §6" + DataManager.getInstance().getMinimumUser() + "§f명");
        p.sendMessage(first + "§6킬 지속시간 §7: §6" + DataManager.getInstance().getKillMaintain() + "§f초");
        p.sendMessage(first + "§6리롤 가능 횟수 §7: §6" + DataManager.getInstance().getMaxReroll() + "§f회");
        p.sendMessage(first + "§6리롤 비용 §7: §6" + DataManager.getInstance().getReRollMoney() + "§f원");
        p.sendMessage(first + "§f참여 보상 §7: §6" + DataManager.getInstance().getJoinMoney() + "§f원");
        p.sendMessage(first + "§c1위 보상 §7: §6" + DataManager.getInstance().getFirstReward() + "§f원");
        p.sendMessage(first + "§b2위 보상 §7: §6" + DataManager.getInstance().getSecondReward() + "§f원");
        p.sendMessage(first + "§a3위 보상 §7: §6" + DataManager.getInstance().getThirdReward() + "§f원");
        if (DataManager.getInstance().getLocations() == null)
            p.sendMessage(first + "§7Location §7: §c설정되지 않음");
        else {
            for (int i = 1; i < DataManager.getInstance().getLocationAmount(); i++) {
                p.sendMessage(first + "§7Location §6" + (i + 1) + " §7: " + locationToString(DataManager.getInstance().getLocations()[i]));
            }
            p.sendMessage(first + "§6SpawnLocation [ 1 ]  §7: " + locationToString(DataManager.getInstance().getLocation()));
        }
        p.sendMessage(" ");
    }

    public void currentGameInfo(Player p) {
        p.sendMessage(" ");
        if (!GameManager.getInstance().isGaming()) {
            p.sendMessage(first + "§b게임 상태 §7: §6유저 대기중..");
            p.sendMessage(" ");
            p.sendMessage(first + "§7참여 인원 §7: §b" + GameManager.getInstance().getUserCount());
            for (UserMananger mananger : GameManager.getInstance().getUsers())
                p.sendMessage("  §7-  §f" + Bukkit.getServer().getPlayer(mananger.getUUID()).getName());
        }
        else {
            p.sendMessage(first + "§b게임 상태 §7: §c게임 진행중");
            p.sendMessage(" ");
            p.sendMessage(first + "§7참여 인원 §7: §b" + GameManager.getInstance().getUserCount());
            for (UserMananger mananger : GameManager.getInstance().getUsers())
                p.sendMessage("  §7-  §f" + Bukkit.getServer().getPlayer(mananger.getUUID()).getName());
        }
        p.sendMessage(" ");
    }

    public String locationToString(Location loc) {
        return "§7[ §fX : " + Math.round(loc.getX()) + ", Y : " + Math.round(loc.getY()) + ", Z : " + Math.round(loc.getZ()) + ", World : " + loc.getWorld().getName() + " §7]";
    }

}