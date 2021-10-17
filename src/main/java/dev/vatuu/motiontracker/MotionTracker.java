package dev.vatuu.motiontracker;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.vatuu.motiontracker.ui.RadarDebugRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MotionTracker implements ClientModInitializer, ModMenuApi {

    public static final String MOD_ID = "motion_tracker";
    public static final int ATTACK_RANGE = 3;

    private RadarDebugRenderer renderer;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(MotionTrackerConfig.class, JanksonConfigSerializer::new);
        renderer = new RadarDebugRenderer();
        WorldRenderEvents.AFTER_ENTITIES.register(l ->  {
            if(MotionTrackerConfig.get().enableDebugRenderer && MotionTrackerConfig.get().showRadar) {
                VertexConsumerProvider.Immediate provider = VertexConsumerProvider.immediate(new BufferBuilder(256));
                renderer.render(l.matrixStack(), l.tickDelta(), provider, l.camera());
            }
        });
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return p -> {
            ConfigScreenProvider<MotionTrackerConfig> provider = (ConfigScreenProvider<MotionTrackerConfig>) AutoConfig.getConfigScreen(MotionTrackerConfig.class, p);
            provider.setOptionFunction((gen, field) -> "config." + MOD_ID + "." + field.getName());
            return provider.get();
        };
    }
}
