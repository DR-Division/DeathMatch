package org.light.source.Game;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardObject {

    private static final ScoreboardObject instance;
    private Scoreboard board;

    private ScoreboardObject() {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
    }
    static {
        instance = new ScoreboardObject();
    }

    public static ScoreboardObject getInstance() {
        return instance;
    }

    public Scoreboard getObject() {
        return board;
    }
}
