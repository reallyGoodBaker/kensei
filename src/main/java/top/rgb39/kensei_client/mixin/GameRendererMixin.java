package top.rgb39.kensei_client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.rgb39.kensei_client.KenseiClient;
import top.rgb39.kensei_client.KenseiListeners;
import top.rgb39.kensei_client.component.RenderLevelConfig;
import top.rgb39.kensei_client.component.camera.CameraNear;

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
            float f, long l, CallbackInfo ci
    ) {
        if (lastFrameTime == 0)
            lastFrameTime = System.currentTimeMillis();
        var currentTime = System.currentTimeMillis();
        var dt = currentTime - lastFrameTime;
        lastFrameTime = currentTime;

        RenderLevelConfig conf = (RenderLevelConfig) KenseiClient.clientApp.getSingletonComponent(RenderLevelConfig.class);
        conf.f = f;
        conf.l = l;
        conf.dt = dt;
        conf.mainCamera = mainCamera;
        KenseiListeners.RENDER_LEVEL.run();
    }

    @Shadow
    private float zoom;
    @Shadow
    private float zoomX;
    @Shadow
    private float zoomY;

    @Inject(
            method = "getProjectionMatrix",
            at = @At("HEAD"),
            cancellable = true
    )
    void projectionMatrix(double d, CallbackInfoReturnable<Matrix4f> cir) {
        cir.cancel();
        Minecraft mc = Minecraft.getInstance();
        CameraNear near = (CameraNear) KenseiClient.clientApp.getSingletonComponent(CameraNear.class);
        float v = Math.max(near.enable ? near.value : 0.05f, 0.05f);
        Matrix4f matrix4f = new Matrix4f();

        if (this.zoom != 1.0F) {
            matrix4f.translate(this.zoomX, -this.zoomY, 0.0F);
            matrix4f.scale(this.zoom, this.zoom, 1.0F);
        }

        var returnVal = matrix4f.perspective((float)(d * 0.01745329238474369), (float)mc.getWindow().getWidth() / (float)mc.getWindow().getHeight(), v, mc.gameRenderer.getDepthFar());
        cir.setReturnValue(returnVal);
    }

}