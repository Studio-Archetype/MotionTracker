package dev.vatuu.motiontracker.mixin;

import dev.vatuu.motiontracker.MotionTrackerConfig;
import dev.vatuu.motiontracker.ui.RadarWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper {

    @Unique
    private RadarWidget radar;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        this.radar = new RadarWidget();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerInventory;getMainHandStack()Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE))
    private void tick(CallbackInfo info) {
        if(MotionTrackerConfig.get().showRadar)
            radar.tick();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    private void render(MatrixStack stack, float delta, CallbackInfo info) {
        if(MotionTrackerConfig.get().showRadar)
            radar.render(stack);
    }
}
