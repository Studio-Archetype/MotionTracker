package dev.vatuu.motiontracker.radar.enums;

public enum RadarDirection {
    NORTH(-22.5F, 22.5F, "n"),
    NORTH_EAST(22.5F, 67.5F, "ne"),
    EAST(67.5F, 112.5F, "e"),
    SOUTH_EAST(112.5F, 157.5F, "se"),
    SOUTH(157.5F, -157.5F, "s"),
    SOUTH_WEST(-157.5F, -112.5F, "sw"),
    WEST(-112.5F, -67.5F, "w"),
    NORTH_WEST(-67.5F, -22.5F, "nw");

    private final float minDegree, maxDegree;
    private final String key;

    RadarDirection(float min, float max, String key) {
        this.minDegree = min;
        this.maxDegree = max;
        this.key = key;
    }

    public String getTextureKey() {
        return this.key;
    }

    public static RadarDirection getFromAngle(double angle) {
        for(RadarDirection dir : RadarDirection.values())
            if(angle >= dir.minDegree && angle <= dir.maxDegree)
                return dir;
        return SOUTH;
    }
}