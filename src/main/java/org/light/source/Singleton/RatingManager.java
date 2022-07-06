package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.light.source.Game.GameManager;
import org.light.source.Game.UserMananger;

public class RatingManager {

    private static RatingManager manager;
    private String[] strings; //3개짜리
    private int[] kills; //얘두

    static {
        manager = new RatingManager();
    }

    private RatingManager(){
        strings = new String[3];
        kills = new int[3];
    }

    public static RatingManager getInstance(){
        return manager;
    }

    public String getFirst(){
        return strings[0];
    }

    public int getFirstKill(){
        return kills[0];
    }

    public String getSecond(){
        return strings[1];
    }

    public int getSecondKill(){
        return kills[1];
    }

    public String getThird(){
        return strings[2];
    }

    public int getThirdKill(){
        return kills[2];
    }

    public void updateRank(){
        clear();
        for (UserMananger manager : GameManager.getInstance().getUsers()){
            Player target = Bukkit.getServer().getPlayer(manager.getUUID());
            if (target == null || !target.isOnline()) return;
            int kill = manager.getKills();
            if (strings[0] == null || kills[0] < kill){
                if (strings[0] != null){
                    if (strings[1] == null){
                        strings[1] = strings[0];
                        kills[1] = kills[0];
                    }
                    else{
                        if (kills[1] < kill){
                            String tempval = strings[1];
                            int temp = kills[1];
                            if (strings[2] == null || kills[2] < temp) {
                                strings[2] = tempval;
                                kills[2] = temp;
                            }
                            strings[1] = strings[0];
                            kills[1] = kills[0];

                        }
                        else{
                            if (strings[2] == null || kills[2] < kill) {
                                strings[2] = strings[0];
                                kills[2] = kills[0];
                            }
                        }
                    }
                }
                strings[0] = target.getName();
                kills[0] = kill;
            }
            else if (strings[1] == null || kills[1] < kill){
                if (strings[1] != null){
                    if (strings[2] == null || kills[2] < kills[1]) {
                        strings[2] = strings[1];
                        kills[2] = kills[1];
                    }
                }
                strings[1] = target.getName();
                kills[1] = kill;
            }
            else if (strings[2] == null || kills[2] < kill){
                strings[2] = target.getName();
                kills[2] = kill;
            }
        }
    }

    public static int getLV(int kill){
        return kill / DataManager.getInstance().getKilltolevel();
    }

    public static String changeNick(String value){
        if (value == null)
            return "§4X";
        else
            return value;
    }

    public void clear(){
        strings[0] = null;
        strings[1] = null;
        strings[2] = null;
        kills[0] = 0;
        kills[1] = 0;
        kills[2] = 0;
    }
}
