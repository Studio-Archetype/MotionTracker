package dev.vatuu.motiontracker.radar;

import dev.vatuu.motiontracker.MotionTracker;
import dev.vatuu.motiontracker.MotionTrackerConfig;
import dev.vatuu.motiontracker.radar.enums.RadarDirection;
import dev.vatuu.motiontracker.radar.enums.RadarDistance;
import dev.vatuu.motiontracker.radar.enums.VerticalDiff;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RadarContent {

    private static final Predicate<Entity> SELECTOR = e -> e.getType() == EntityType.PLAYER;

    private final Map<RadarDirection, RadarSegment> cardinalDistance = new HashMap<>();

    private boolean hasCenter = false;
    private VerticalDiff centerDiff;

    public RadarContent() {
        for(RadarDirection value :RadarDirection.values())
            cardinalDistance.put(value, new RadarSegment());
    }

    public void update() {
        reset();
        PlayerEntity player = MinecraftClient.getInstance().player;
        for(Entity e : getEntitiesInRange(player)) {
            Vec3d offsetPos = e.getPos().subtract(player.getPos());
            RadarDistance distance = getDistance(player, e.getPos());
            RadarSegment segment = getSegment(getDirection(offsetPos, MathHelper.wrapDegrees(player.getYaw())));

            if(this.centerDiff == VerticalDiff.LEVEL && segment.getYDiff() == VerticalDiff.LEVEL && segment.hasOuter())
                continue;

            if(distance == RadarDistance.CENTER && this.centerDiff != VerticalDiff.LEVEL) {
                this.hasCenter = true;
                this.centerDiff = VerticalDiff.getDiff(offsetPos.getY());
            } else if(distance == RadarDistance.INNER && segment.getYDiff() != VerticalDiff.LEVEL) {
                segment.setInner();
                segment.setYDiff(VerticalDiff.getDiff(offsetPos.getY()));
            } else if(distance == RadarDistance.OUTER && !segment.hasOuter())
                segment.setOuter();
        }
    }

    public boolean hasCenter() {
        return this.hasCenter;
    }

    public VerticalDiff getYDiff() {
        return this.centerDiff;
    }

    public RadarSegment getSegment(RadarDirection direction) {
        return this.cardinalDistance.get(direction);
    }

    private List<Entity> getEntitiesInRange(PlayerEntity p) {
        World w = p.getEntityWorld();
        List<Entity> entities = w.getOtherEntities(p, getBoundingBox(p.getPos()), SELECTOR);
        return entities.stream()
                .filter(e -> distancePlaneX(p.getX(), p.getZ(), e.getX(), e.getZ()) <= Math.pow(MotionTrackerConfig.get().distances.totalDistance, 2))
                .collect(Collectors.toList());
    }

    private Box getBoundingBox(Vec3d pos) {
        int radius = MotionTrackerConfig.get().distances.totalDistance;
        int height = MotionTrackerConfig.get().distances.totalHeight;
        return new Box(pos.subtract(radius, height, radius), pos.add(radius, height, radius));
    }

    private RadarDirection getDirection(Vec3d pos, float yaw) {
        double angle = MathHelper.wrapDegrees(Math.toDegrees(MathHelper.atan2(pos.getZ(), pos.getX())) - 90.0F - yaw);
        return RadarDirection.getFromAngle(angle);
    }

    private RadarDistance getDistance(PlayerEntity e, Vec3d offset) {
        double distance = distancePlaneX(e.getX(), e.getZ(), offset.getX(), offset.getZ());
        double centerRange = MotionTracker.ATTACK_RANGE;
        if(distance <= centerRange)
            return RadarDistance.CENTER;
        if(distance <= MotionTrackerConfig.get().distances.innerRingDistance)
            return RadarDistance.INNER;

        return RadarDistance.OUTER;
    }

    private double distancePlaneX(double x1, double z1, double x2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2));
    }

    private void reset() {
        this.hasCenter = false;
        this.centerDiff = null;
        cardinalDistance.values().forEach(RadarSegment::reset);
    }
}
