package org.light.source.Game;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.light.source.Singleton.KillDeathObject;

import java.util.HashMap;
import java.util.UUID;

public class KillDeathManager {

    private static KillDeathManager instance;
    private Object2ObjectOpenHashMap<UUID, KillDeathObject> list;

    static {
        instance = new KillDeathManager();
    }

    private KillDeathManager() {
        list = new Object2ObjectOpenHashMap<>();
    }

    public static KillDeathManager getInstance() {
        return instance;
    }

    public KillDeathObject setValue(String name, UUID uid, int kill, int death) {
        if (list.containsKey(uid))
            list.get(uid)
                    .setKill(kill)
                    .setDeath(death);
        else
            list.put(uid, new KillDeathObject(name, kill, death));
        return list.get(uid);
    }

    public KillDeathObject getValue(UUID uuid){
        if (list.containsKey(uuid))
            return list.get(uuid);
        else
            return setValue("Offline", uuid,0,0);
    }

    public Object2ObjectOpenHashMap<UUID, KillDeathObject> getList(){
        return list;
    }

}
