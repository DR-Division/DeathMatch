package org.light.source.Singleton;

import org.bukkit.Location;

public class DataManager {

    private int rounds;
    private int killtolevel;
    private int time; //초단위
    private int minimum;
    private Location[] locations; //2개
    private static DataManager manager;
    private int joinMoney;
    private int firstReward;
    private int secondReward;
    private int thirdReward;
    private int killMaintain;
    private int maxReroll;
    private int reRollMoney;
    private int waitTime;

    static {
        manager = new DataManager();
    }

    private DataManager(){
        super();
        killtolevel = 0;
        rounds = 0;
        time = 0;
        minimum = 0;
        joinMoney = 0;
        firstReward = 0;
        secondReward = 0;
        thirdReward = 0;
        killMaintain = 1;
        maxReroll = 0;
        reRollMoney = 0;
        waitTime = 0;
        locations = new Location[21];
    }

    public static DataManager getInstance(){
        return manager;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setKilltolevel(int killtolevel) {
        this.killtolevel = killtolevel;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getRounds() {
        return rounds;
    }

    public int getKilltolevel() {
        return killtolevel;
    }

    public int getTime() {
        return time;
    }

    public Location getLocation() {
        if (locations == null)
            return null;
        return locations[0];
    }
    public Location[] getLocations() {
        if (locations == null)
            return null;
        if (getLocationAmount() % 2 != 1 || getLocationAmount() == 1)
            return null;
        return locations;
    }

    public void setLocations(Location location, int index) {
        this.locations[index-1] = location;
    }

    public int getMinimumUser() {
        return minimum;
    }

    public void setMinimumUser(int minimum) {
        this.minimum = minimum;
    }

    public int getLocationAmount(){
        int i = 0, count = 0;
        while (locations[i] != null){
            count++;
            i++;
        }
        return count;
    }

    public void flushLocation(){
        locations = new Location[locations.length];
    }

    public int getMaxReroll() {
        return maxReroll;
    }

    public void setMaxReroll(int maxReroll) {
        this.maxReroll = maxReroll;
    }

    public int getReRollMoney() {
        return reRollMoney;
    }

    public void setReRollMoney(int reRollMoney) {
        this.reRollMoney = reRollMoney;
    }

    public int getJoinMoney(){
        return joinMoney;
    }

    public void setJoinMoney(int reward){
        joinMoney = reward;
    }

    public int getFirstReward(){
        return firstReward;
    }

    public void setFirstReward(int reward){
        firstReward = reward;
    }

    public int getSecondReward(){
        return secondReward;
    }

    public void setSecondReward(int reward){
        secondReward = reward;
    }

    public int getThirdReward(){
        return thirdReward;
    }

    public void setThirdReward(int reward){
        thirdReward = reward;
    }

    public int getKillMaintain() {
        return killMaintain;
    }

    public void setKillMaintain(int killMaintain) {
        this.killMaintain = killMaintain;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

}
