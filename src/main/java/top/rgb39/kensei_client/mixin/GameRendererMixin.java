package top.rgb39.kensei_client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.rgb39.kensei_client.KenseiClient;
import top.rgb39.kensei_client.KenseiListeners;
import top.rgb39.kensei_client.component.RenderLevelConfig;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Unique
    long lastFrameTime = 0;

    @Final
    @Shadow
    private Camera mainCamera;

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
                    shift = At.Shift.AFTER
            )
    )
    void adjustCamera(
            float f, long l, PoseStack poseStack, CallbackInfo ci
    ) {
        if (lastFrameTime == 0)
            lastFrameTime = System.currentTimeMillis();
        var currentTime = System.currentTimeMillis();
        var dt = currentTime - lastFrameTime;
        lastFrameTime = currentTime;

        RenderLevelConfig conf = (RenderLevelConfig) KenseiClient.clientApp.getSingletonComponent(RenderLevelConfig.class);
        conf.f = f;
        conf.l = l;
        conf.poseStack = poseStack;
        conf.dt = dt;
        conf.mainCamera = mainCamera;
        KenseiListeners.RENDER_LEVEL_LISTENER.run();
    }

}