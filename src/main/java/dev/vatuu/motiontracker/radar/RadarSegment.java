package dev.vatuu.motiontracker.radar;

import dev.vatuu.motiontracker.radar.enums.VerticalDiff;

public class RadarSegment {

    private boolean hasInner = false, hasOuter = false;
    private VerticalDiff yDiff;

    public void reset() {
        hasInner = hasOuter = false;
        yDiff =  null;
    }

    public boolean hasInner() {
        return this.hasInner;
    }

    public boolean hasOuter() {
        return this.hasOuter;
    }

    public VerticalDiff getYDiff() {
        return this.yDiff;
    }

    public void setInner() {
        this.hasInner = true;
    }

    public void setOuter() {
        this.hasOuter = true;
    }

    public void setYDiff(VerticalDiff diff) {
        this.yDiff = diff;
    }
}
