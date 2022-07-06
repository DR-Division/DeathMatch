package org.light.source.Singleton;

import java.util.UUID;

public class RankObject {

    public int kill;
    public int death;
    public UUID UUID;
    public String name;

    public RankObject(UUID uuid, KillDeathObject object){
        this.UUID = uuid;
        this.kill = object.getKill();
        this.death = object.getDeath();
        this.name = object.getName();
    }

}
