package org.light.source.Singleton;

import org.bukkit.Location;

public class AfkObject {

    private int checkTime;
    private Location location;

    public AfkObject(Location location) {
        checkTime = DataManager.getInstance().getWaitTime();
        this.location = location;
    }

    public void setCheckTime(int value) {
        checkTime = value;
    }

    public int getCheckTime() {
        return checkTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location value) {
        location = value;
    }

    public void resetValue(Location location){
        setCheckTime(DataManager.getInstance().getWaitTime());
        setLocation(location);
    }
}
