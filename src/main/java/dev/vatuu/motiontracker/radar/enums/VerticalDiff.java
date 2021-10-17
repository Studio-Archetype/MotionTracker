package dev.vatuu.motiontracker.radar.enums;

import dev.vatuu.motiontracker.MotionTrackerConfig;

public enum VerticalDiff {
    ABOVE("above"),
    LEVEL("level"),
    BELOW("below");

    private final String key;

    VerticalDiff(String key) {
        this.key = key;
    }

    public String getTextureKey() {
        return this.key;
    }

    public static VerticalDiff getDiff(double y) {
        if(y > MotionTrackerConfig.get().distances.heightThreshold)
            return ABOVE;
        else if(y < -MotionTrackerConfig.get().distances.heightThreshold)
            return BELOW;
        return LEVEL;
    }
}
