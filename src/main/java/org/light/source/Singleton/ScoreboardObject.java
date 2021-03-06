package org.light.source.Singleton;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.light.dayz.game.GameController;
import org.light.source.Game.GameManager;

import java.util.ArrayList;

public class ScoreboardObject {

    private static ScoreboardObject instance;
    private ScoreboardManager boardManager;
    private Scoreboard wait;
    private Objective readyObject;
    private ObjectArrayList<String> current;
    static {
        instance = new ScoreboardObject();
    }

    private ScoreboardObject(){
        current = new ObjectArrayList<>();
        boardManager = Bukkit.getScoreboardManager();
        wait = boardManager.getNewScoreboard();
        readyObject = wait.registerNewObjective("test", "dummy");
        readyObject.setDisplaySlot(DisplaySlot.SIDEBAR);
        readyObject.setDisplayName("§f§oDeath§7§oMatch");
    }

    public static ScoreboardObject getInstance(){
        return instance;
    }

    public void setScoreboard(Player p){
        p.setScoreboard(wait);
    }

    public Scoreboard getObject(){
        return wait;
    }
    public void sendScoreboard(int value){
        ArrayList<String> Score = new ArrayList<>();
        if (value == 1){
            //시작전
            Score.add("§8»       §6온라인 §7: §e" + Bukkit.getServer().getOnlinePlayers().size() + "§f명");
            Score.add("§8»       §c데이즈 §7: §7" + GameController.getSize() + "§f명");
            Score.add("                         ");
            Score.add("§8»       §b진행도 §7: §6대기중..");
            Score.add("§8»       §a참여자 §7: §6" + GameManager.getInstance().getUserCount() + "§f명");
            Score.add("                        ");
        }
        else if (value == 2){
            //시작 대기준
            Score.add("§8»       §6온라인 §7: §e" + Bukkit.getServer().getOnlinePlayers().size() + "§f명");
            Score.add("§8»       §c데이즈 §7: §7" + GameController.getSize() + "§f명");
            Score.add("                         ");
            Score.add("§8»       §b진행도 §7: §e준비중..");
            Score.add("§8»       §a참여자 §7: §6" + GameManager.getInstance().getUserCount() + "§f명");
            Score.add("                        ");
        }
        else if (value == 3){
            //시작
            Score.add("§8»       §6온라인 §7: §e" + Bukkit.getServer().getOnlinePlayers().size() + "§f명");
            Score.add("§8»       §a참여자 §7: §6" + GameManager.getInstance().getUserCount() + "§f명");
            Score.add("§8»       §c데이즈 §7: §7" + GameController.getSize() + "§f명");
            Score.add("                         ");
            Score.add("§fLV. §6"  + RatingManager.getLV(RatingManager.getInstance().getFirstKill()) + " §b" + checkLength(RatingManager.changeNick(RatingManager.getInstance().getFirst())));
            Score.add("§fLV. §6"  + RatingManager.getLV(RatingManager.getInstance().getSecondKill()) + " §c" + checkLength(RatingManager.changeNick(RatingManager.getInstance().getSecond())));
            Score.add("§fLV. §6"  + RatingManager.getLV(RatingManager.getInstance().getThirdKill()) + " §6" + checkLength(RatingManager.changeNick(RatingManager.getInstance().getThird())));
            Score.add("                        ");
        }
        if (current.isEmpty() || !current.containsAll(Score)){
            int i = 0;
            current.clear();
            current.addAll(Score);
            for (String entry : readyObject.getScoreboard().getEntries())
                readyObject.getScoreboard().resetScores(entry);
            for (String val : Score) {
                readyObject.getScore(val).setScore(Score.size() - i);
                i++;
            }
        }
    }


    public String checkLength(String value){
        if (value.length() >= 40)
            return "§cLOOOONG..";
        return value;
    }
}
