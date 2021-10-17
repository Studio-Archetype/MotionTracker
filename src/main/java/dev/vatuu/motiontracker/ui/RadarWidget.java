package dev.vatuu.motiontracker.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.vatuu.motiontracker.MotionTracker;
import dev.vatuu.motiontracker.MotionTrackerConfig;
import dev.vatuu.motiontracker.radar.RadarContent;
import dev.vatuu.motiontracker.radar.RadarSegment;
import dev.vatuu.motiontracker.radar.enums.RadarDirection;
import dev.vatuu.motiontracker.radar.enums.RadarDistance;
import dev.vatuu.motiontracker.radar.enums.VerticalDiff;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RadarWidget {

    private static final Identifier TEX_PLAYER = MotionTracker.id("textures/arrow.png");
    private static final Identifier TEX_BASE = MotionTracker.id("textures/base.png");
    private static final Identifier TEX_BASE_SOFT = MotionTracker.id("textures/base_softer.png");

    private final RadarContent content;

    public RadarWidget() {
        this.content = new RadarContent();
    }

    public void tick() {
        content.update();
    }

    public void render(MatrixStack stack) {
        stack.push();

        stack.scale(.1F, .1F, 1);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        drawTexture(stack, MotionTrackerConfig.get().alternativeBase ? TEX_BASE : TEX_BASE_SOFT);           // Draw the frame
        if(content.hasCenter())
            drawTexture(stack, getTexture(RadarDistance.CENTER, null, content.getYDiff())); // Draw the center circle
        for(RadarDirection dir : RadarDirection.values()) {                                         // Draw the directional segments
            RadarSegment segment = content.getSegment(dir);
            if(segment.hasInner())
                drawTexture(stack, getTexture(RadarDistance.INNER, dir, segment.getYDiff()));       // Draw inner ring
            if(segment.hasOuter())
                drawTexture(stack, getTexture(RadarDistance.OUTER, dir, null));                 // Draw the outer ring
        }
        drawTexture(stack, TEX_PLAYER);                                                             // Draw the player icon

        RenderSystem.disableBlend();

        stack.pop();
    }

    private void drawTexture(MatrixStack stack, Identifier id) {
        RenderSystem.setShaderTexture(0, id);
        DrawableHelper.drawTexture(stack, MotionTrackerConfig.get().positioning.offsetX * 10, MotionTrackerConfig.get().positioning.offsetY * 10, 0, 0, 1024, 1024, 1024, 1024);
    }

    private Identifier getTexture(RadarDistance distance, RadarDirection direction, VerticalDiff diff) {
        StringBuilder builder = new StringBuilder("textures/").append(distance.getTextureKey()).append("/");

        if(distance == RadarDistance.CENTER) {
            builder.append(distance.getTextureKey()).append("_").append(diff.getTextureKey()).append(".png");
            return MotionTracker.id(builder.toString());
        }

        if(distance == RadarDistance.INNER)
            builder.append(diff.getTextureKey()).append("/");
        builder.append(direction.getTextureKey()).append(".png");

        return MotionTracker.id(builder.toString());
    }
}
