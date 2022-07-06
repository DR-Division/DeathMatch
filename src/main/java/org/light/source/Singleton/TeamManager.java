package org.light.source.Singleton;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.light.source.Game.ScoreboardObject;

public class TeamManager {

    private static final TeamManager instance;
    private Team joinTeam;

    static {
        instance = new TeamManager();
    }

    private TeamManager(){
        for (Team team : ScoreboardObject.getInstance().getObject().getTeams()) {
            if (team.getName().contains("join"))
                joinTeam = team;
        }
        if (joinTeam == null)
            joinTeam = ScoreboardObject.getInstance().getObject().registerNewTeam("join");
        setTeam(joinTeam);
        //팀 구현 필요
    }
    public static TeamManager getInstance(){
        return instance;
    }

    private void setTeam(Team team){
        team.setColor(ChatColor.AQUA);
        team.setAllowFriendlyFire(true);
        team.setCanSeeFriendlyInvisibles(true);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    public void removePlayer(Player p){
        this.joinTeam.removeEntry(p.getName());
    }

    public void addPlayer(Player p){
        this.joinTeam.addEntry(p.getName());
    }
}
