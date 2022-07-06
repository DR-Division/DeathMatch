package org.light.source.Singleton;

import java.util.UUID;

public class KillDeathObject {

    private int kill;
    private int death;
    private String name;

    public KillDeathObject(String name, int kill, int death){
        this.name = name;
        this.kill = kill;
        this.death = death;
    }

    public KillDeathObject setKill(int kill){
        this.kill = kill;
        return this;
    }

    public int getKill(){
        return kill;
    }

    public KillDeathObject setDeath(int death){
        this.death = death;
        return this;
    }

    public int getDeath(){
        return death;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
