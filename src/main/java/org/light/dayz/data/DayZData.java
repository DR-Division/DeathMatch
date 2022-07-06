package org.light.dayz.data;

public class DayZData {

    private int accumulateMoney;
    private int kill;

    public DayZData() {
        accumulateMoney = 0;
        kill = 0;
    }

    public int getAccumulateMoney() {
        return accumulateMoney;
    }

    public void setAccumulateMoney(int accumulateMoney) {
        this.accumulateMoney = accumulateMoney;
    }

    public int getKill() {
        return kill;
    }

    public void setKill(int kill) {
        this.kill = kill;
    }

}
