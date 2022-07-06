package org.light.source.Game;

import org.light.source.Singleton.DataManager;

import java.util.HashMap;
import java.util.UUID;

public class UserMananger {

    private UUID uuid;
    private int kills;
    private long lastKillTime;
    private int killMaintain;
    private int reRoll;
    private int calcResultMoney;
    private HashMap<UUID, Integer> damage;

    public UserMananger(UUID uuid) {
        this.uuid = uuid;
        kills = 0;
        killMaintain = 0;
        lastKillTime = 0;
        reRoll = 0;
        calcResultMoney = 0;
        damage = new HashMap<>();
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getKillMaintain() {
        return killMaintain;
    }

    public void setKillMaintain(int killMaintain) {
        this.killMaintain = killMaintain;
    }

    public long getLastKillTime() {
        return lastKillTime;
    }

    public void setLastKillTime(long lastKillTime) {
        this.lastKillTime = lastKillTime;
    }

    public int getReRoll() {
        return reRoll;
    }

    public void setReRoll(int reRoll) {
        this.reRoll = reRoll;
    }

    public int getCalcResultMoney() {
        return calcResultMoney;
    }

    public void setCalcResultMoney(int calcResultMoney) {
        this.calcResultMoney = calcResultMoney;
    }

    public boolean calcKillStay() {
        return System.currentTimeMillis() - getLastKillTime() <= DataManager.getInstance().getKillMaintain() * 1000L;
    }

    public HashMap<UUID, Integer> getDamageMap() {
        return damage;
    }

    public void reset(){
        kills = 0;
        killMaintain = 0;
        lastKillTime = 0;
        reRoll = 0;
        calcResultMoney = 0;
        damage.clear();
    }

}
