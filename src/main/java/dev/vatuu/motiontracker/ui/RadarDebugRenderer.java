package dev.vatuu.motiontracker.ui;

import dev.vatuu.motiontracker.MotionTracker;
import dev.vatuu.motiontracker.MotionTrackerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.OptionalDouble;

public class RadarDebugRenderer {

    private static final float CIRCLE_DETAIL = 16;
    private static final float CIRCLE_STEP = 360F / CIRCLE_DETAIL;

    private static final Vec3d COLOUR_RED = new Vec3d(1, 0, 0);
    private static final Vec3d COLOUR_YELLOW = new Vec3d(1, 1, 0);
    private static final Vec3d COLOUR_GREEN = new Vec3d(0, 1, 0);

    public void render(MatrixStack matrices, float tickDelta, VertexConsumerProvider.Immediate vertexConsumers, Camera cam) {
        matrices.push();
        offsetStackByCamera(matrices, cam);

        PlayerEntity entity = MinecraftClient.getInstance().player;
        Vec3d pivot = new Vec3d(
                MathHelper.lerp(tickDelta, entity.prevX, entity.getX()),
                MathHelper.lerp(tickDelta, entity.prevY, entity.getY()),
                MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ())
        );

        int maxHeight = MotionTrackerConfig.get().distances.totalHeight;
        int thresholdHeight = MotionTrackerConfig.get().distances.heightThreshold;
        int maxDistance = MotionTrackerConfig.get().distances.totalDistance;
        int innerRing = MotionTrackerConfig.get().distances.innerRingDistance;
        float reach = MotionTracker.ATTACK_RANGE;

        drawCylinder(matrices, pivot, maxDistance, maxHeight, COLOUR_GREEN, 0, vertexConsumers);
        drawCylinder(matrices, pivot, innerRing, maxHeight, COLOUR_YELLOW, CIRCLE_DETAIL / 3, vertexConsumers);
        drawCircle(matrices, pivot, pivot.add(new Vec3d(0, thresholdHeight, innerRing)), COLOUR_YELLOW, CIRCLE_DETAIL / 3, vertexConsumers.getBuffer(DebugLineStripLayer.LAYER));
        vertexConsumers.draw();
        drawCircle(matrices, pivot, pivot.add(new Vec3d(0, -thresholdHeight, innerRing)), COLOUR_YELLOW, CIRCLE_DETAIL / 3, vertexConsumers.getBuffer(DebugLineStripLayer.LAYER));
        vertexConsumers.draw();
        drawCylinder(matrices, pivot, reach, maxHeight, COLOUR_RED, CIRCLE_DETAIL / 3 * 2, vertexConsumers);
        drawCircle(matrices, pivot, pivot.add(new Vec3d(0, thresholdHeight, reach)), COLOUR_RED, CIRCLE_DETAIL / 3 * 2, vertexConsumers.getBuffer(DebugLineStripLayer.LAYER));
        vertexConsumers.draw();
        drawCircle(matrices, pivot, pivot.add(new Vec3d(0, -thresholdHeight, reach)), COLOUR_RED, CIRCLE_DETAIL / 3 * 2, vertexConsumers.getBuffer(DebugLineStripLayer.LAYER));
        vertexConsumers.draw();

        matrices.pop();
    }

    private void drawCircle(MatrixStack stack, Vec3d pivot, Vec3d offset, Vec3d colour, float rotationOffset, VertexConsumer consumer) {
        drawLineStrip(stack, offset, colour, consumer);
        for(int i = 0; i < CIRCLE_DETAIL; i++) {
            float angle = CIRCLE_STEP * i + rotationOffset;
            drawLineStrip(stack, getRotated(pivot.getX(), pivot.getZ(), offset, angle), colour, consumer);
        }
        drawLineStrip(stack, offset, colour, consumer);
    }

    private void drawCylinder(MatrixStack stack, Vec3d pivot, float offset, float height, Vec3d colour, float rotationOffset, VertexConsumerProvider.Immediate consumer) {
        Vec3d offsetUp = pivot.add(new Vec3d(0, height, offset));
        drawCircle(stack, pivot, offsetUp, colour, rotationOffset, consumer.getBuffer(DebugLineStripLayer.LAYER));
        consumer.draw();
        drawCircle(stack, pivot, offsetUp.subtract(0, height * 2, 0), colour, rotationOffset, consumer.getBuffer(DebugLineStripLayer.LAYER));
        consumer.draw();

        for(int i = 0; i < CIRCLE_DETAIL; i++) {
            float angle = CIRCLE_STEP * i + rotationOffset;
            Vec3d pos = getRotated(pivot.getX(), pivot.getZ(), offsetUp, angle);
            drawLine(stack, pos, pos.subtract(0, height * 2, 0), colour, consumer.getBuffer(DebugLineRenderer.LAYER));
        }
        consumer.draw();
    }

    private void drawLineStrip(MatrixStack stack, Vec3d pos, Vec3d color, VertexConsumer consumer) {
        consumer.vertex(stack.peek().getModel(), (float)pos.getX(), (float) pos.getY(), (float) pos.getZ())
                .color((float)color.getX(), (float)color.getY(), (float)color.getZ(), 1.0F)
                .next();
    }

    private void drawLine(MatrixStack stack, Vec3d pos, Vec3d pos2, Vec3d color, VertexConsumer consumer) {
        consumer.vertex(stack.peek().getModel(), (float)pos.getX(), (float) pos.getY(), (float) pos.getZ())
                .color((float)color.getX(), (float)color.getY(), (float)color.getZ(), 1.0F)
                .next();
        consumer.vertex(stack.peek().getModel(), (float)pos2.getX(), (float) pos2.getY(), (float) pos2.getZ())
                .color((float)color.getX(), (float)color.getY(), (float)color.getZ(), 1.0F)
                .next();
    }

    private Vec3d getRotated(double pivotX, double pivotZ, Vec3d offset, float angle) {
        float sin = MathHelper.sin((float)Math.toRadians(angle));
        float cos = MathHelper.cos((float)Math.toRadians(angle));
        double x = offset.getX() - pivotX;
        double z = offset.getZ() - pivotZ;
        float xNew = (float)(x * cos - z * sin);
        float zNew = (float)(x * sin + z * cos);
        return new Vec3d(xNew + pivotX, offset.getY(), zNew + pivotZ);
    }

    protected void offsetStackByCamera(MatrixStack stack, Camera cam) {
        stack.translate(-cam.getPos().getX(), -cam.getPos().getY(), -cam.getPos().getZ());
    }

    private static class DebugLineStripLayer extends RenderLayer {

        public static final RenderLayer LAYER = RenderLayer.of(
                "radont_line_strip",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.DEBUG_LINE_STRIP,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .shader(COLOR_SHADER)
                        .lineWidth(new LineWidth(OptionalDouble.of(2)))
                        .build(true));

        private DebugLineStripLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        }
    }

    private static class DebugLineRenderer extends RenderLayer {

        public static final RenderLayer LAYER = RenderLayer.of(
                "radont_line",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.DEBUG_LINES,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .shader(COLOR_SHADER)
                        .lineWidth(new LineWidth(OptionalDouble.of(2)))
                        .build(true));

        private DebugLineRenderer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        }
    }
}
