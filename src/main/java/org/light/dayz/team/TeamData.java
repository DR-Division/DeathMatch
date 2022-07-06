package org.light.dayz.team;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.light.source.Game.ScoreboardObject;

public class TeamData {

    private final Team dayzTeam;

    public TeamData() {
         this.dayzTeam = ScoreboardObject.getInstance().getObject().registerNewTeam("dayz");
         setTeam(this.dayzTeam);
    }

    private void setTeam(Team team){
        team.setColor(ChatColor.RED);
        team.setAllowFriendlyFire(true);
        team.setCanSeeFriendlyInvisibles(true);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    public void removePlayer(Player p){
        this.dayzTeam.removeEntry(p.getName());
    }

    public void addPlayer(Player p){
        this.dayzTeam.addEntry(p.getName());
    }
}
