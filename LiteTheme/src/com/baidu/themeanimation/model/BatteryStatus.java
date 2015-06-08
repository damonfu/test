
package com.baidu.themeanimation.model;

public class BatteryStatus {
    public final int status;
    public final int level;
    public final int plugged;
    public final int health;

    public BatteryStatus(int status, int level, int plugged, int health) {
        this.status = status;
        this.level = level;
        this.plugged = plugged;
        this.health = health;
    }
}
