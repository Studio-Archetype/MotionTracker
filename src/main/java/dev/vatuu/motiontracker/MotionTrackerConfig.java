package dev.vatuu.motiontracker;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = MotionTracker.MOD_ID)
public class MotionTrackerConfig implements ConfigData {

    public boolean showRadar = true;
    public boolean alternativeBase = false;
    public boolean enableDebugRenderer = false;

    @ConfigEntry.Gui.CollapsibleObject
    public Distances distances = new Distances();

    @ConfigEntry.Gui.CollapsibleObject
    public Positioning positioning = new Positioning();

    public static MotionTrackerConfig get() {
        return AutoConfig.getConfigHolder(MotionTrackerConfig.class).getConfig();
    }

    public static class Positioning {
        @ConfigEntry.BoundedDiscrete(min = 0, max = 64)
        public int offsetX = 16;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 64)
        public int offsetY = 16;
    }

    public static class Distances {

        @ConfigEntry.BoundedDiscrete(min = 1, max = 15)
        public int innerRingDistance = 6;

        @ConfigEntry.BoundedDiscrete(min = 16, max = 32)
        public int totalDistance = 16;

        @ConfigEntry.BoundedDiscrete(min = 3, max = 8)
        public int heightThreshold = 3;

        @ConfigEntry.BoundedDiscrete(min = 8, max = 16)
        public int totalHeight = 8;
    }
}
