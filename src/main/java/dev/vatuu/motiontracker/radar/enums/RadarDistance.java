package dev.vatuu.motiontracker.radar.enums;

public enum RadarDistance {

    CENTER("center"),
    INNER("inner"),
    OUTER("outer");

    private final String key;

    RadarDistance(String key) {
        this.key = key;
    }

    public String getTextureKey() {
        return this.key;
    }
}
