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

//        Minecraft minecraft = Minecraft.getInstance();
//        CameraType type = minecraft.options.getCameraType();
//        if (type.isFirstPerson()) return;
//
//        CameraOffset off = (CameraOffset) KenseiClient.clientApp.getSingletonComponent(CameraOffset.class);
//        CameraAccessor camera = (CameraAccessor) mainCamera;
//        LocalPlayer player = minecraft.player;
//        if (player == null) {
//            return;
//        }
//
//        var plx = Mth.lerp(f, player.xo, player.getX());
//        var ply = Mth.lerp(f, player.yo, player.getY()) + player.getEyeHeight();
//        var plz = Mth.lerp(f, player.zo, player.getZ());
//
//        // 摄像机回到眼睛
//        camera.invokeSetPosition(plx, ply, plz);
//
//        // 处理第三人称面向玩家的视角
//        if (type.isMirrored()) {
//            camera.invokeMove(
//                    off.dx, off.dy, off.dz
//            );
//            return;
//        }
//
//        // 根据锁定的生物进行旋转
//        PlayerStatus status = (PlayerStatus) KenseiClient.clientApp.getComponent(player.getId(), PlayerStatus.class);
//
//        float yRot = player.yRotO;
//        float xRot = player.xRotO;
//        Cameras.fade((int) dt, off);
//
//        if (status.lockedEntity != 0) {
//            try (Level lvl = player.level()) {
//                Entity en = lvl.getEntity(status.lockedEntity);
//                if (en == null) {
//                    return;
//                }
//
//                var ex = Mth.lerp(f, en.xo, en.getX());
//                var ey = Mth.lerp(f, en.yo, en.getY()) + en.getEyeHeight();
//                var ez = Mth.lerp(f, en.zo, en.getZ());
//                var facing = new Vec3(ex-plx, ey-ply, ez-plz);
//                var rightYRot = (float) Mth.atan2(facing.x, facing.z);
//
//                var rotY = (float) -Mth.wrapDegrees((rightYRot + Math.atan2(-off.dz, facing.length() - off.dx)) * 57.295776367188);
//                var rotX = (float) -Mth.wrapDegrees(Math.atan((facing.y() + off.dy) / facing.length()) * 57.295776367188);
//
//                var rightYRotDegree = (float) (-rightYRot * 57.295776367188);
//
//                player.setYRot(rightYRotDegree);
//                player.setYBodyRot(rightYRotDegree);
//                player.setXRot(rotX);
//
//                camera.invokeMove(off.dx, off.dy, off.dz);
//                camera.invokeSetRotation(rotY, rotX);
//                return;
//            } catch (Exception ignored) {}
//        }
//
//        camera.invokeSetRotation(yRot, xRot);
//
//        // 根据 offset 进行移动
//        camera.invokeMove(off.dx, off.dy, off.dz);
    }

}