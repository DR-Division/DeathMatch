package org.light.source.Game;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class NoKnockbackObject {

    private static NoKnockbackObject object;
    private Object2ObjectOpenHashMap<UUID, Boolean> knockBack;

    static {
        object = new NoKnockbackObject();
    }

    private NoKnockbackObject(){
        knockBack = new Object2ObjectOpenHashMap<>();
    }

    public static NoKnockbackObject getInstance(){
        return object;
    }

    public void setKnockBackState(Player p, boolean value){
        knockBack.put(p.getUniqueId(), value);
    }

    public boolean getKnockbackState(Player p){
        return knockBack.get(p.getUniqueId()) == null ? true : knockBack.get(p.getUniqueId());
    }
}

