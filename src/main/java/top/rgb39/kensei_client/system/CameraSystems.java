package top.rgb39.kensei_client.system;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.rgb39.ecs.annotation.Reflect;
import top.rgb39.ecs.annotation.Slot;
import top.rgb39.ecs.annotation.System;
import top.rgb39.ecs.util.Option;
import top.rgb39.ecs.util.Option.T;
import top.rgb39.kensei_client.RenderRuntime;
import top.rgb39.kensei_client.component.CameraOffset;
import top.rgb39.kensei_client.component.PlayerStatus;
import top.rgb39.kensei_client.component.RenderLevelConfig;
import top.rgb39.kensei_client.mixin.CameraAccessor;
import top.rgb39.kensei_client.util.Cameras;

public class CameraSystems {

    @System(runtimeLabel = RenderRuntime.RENDER_LEVEL)
    void updateCamera(
        @top.rgb39.ecs.annotation.Entity long id,
        @Reflect(RenderLevelConfig.class) RenderLevelConfig conf,
        @Reflect(Minecraft.class) Minecraft minecraft,
        @Reflect(CameraOffset.class) CameraOffset off,
        @Slot(PlayerStatus.class) PlayerStatus status
    ) {
        var it = Option.it(new T<LocalPlayer>() {});

        switch (Option.some(minecraft.player).state(it)) {
            case NONE -> { return; }
            case SOME -> {
                if (it.v().getId() != id)
                    return;
            }
        }

        var f = conf.f;
        var mainCamera = conf.mainCamera;
        var dt = conf.dt;
        var player = it.v();

        CameraType type = minecraft.options.getCameraType();
        if (type.isFirstPerson()) return;

        CameraAccessor camera = (CameraAccessor) mainCamera;

        var plx = Mth.lerp(f, player.xo, player.getX());
        var ply = Mth.lerp(f, player.yo, player.getY()) + player.getEyeHeight();
        var plz = Mth.lerp(f, player.zo, player.getZ());

        // 摄像机回到眼睛
        camera.invokeSetPosition(plx, ply, plz);

        // 处理第三人称面向玩家的视角
        if (type.isMirrored()) {
            camera.invokeMove(
                    off.dx, off.dy, off.dz
            );
            return;
        }

        // 根据锁定的生物进行旋转
        float yRot = player.yRotO;
        float xRot = player.xRotO;
        Cameras.fade((int) dt, off);

        if (status.lockedEntity != 0) {
            try (Level lvl = player.level()) {
                Entity en = lvl.getEntity(status.lockedEntity);
                if (en == null) {
                    return;
                }

                var ex = Mth.lerp(f, en.xo, en.getX());
                var ey = Mth.lerp(f, en.yo, en.getY()) + en.getEyeHeight();
                var ez = Mth.lerp(f, en.zo, en.getZ());
                var facing = new Vec3(ex-plx, ey-ply, ez-plz);
                var rightYRot = (float) Mth.atan2(facing.x, facing.z);

                var rotY = (float) -Mth.wrapDegrees((rightYRot + Math.atan2(-off.dz, facing.length() - off.dx)) * 57.295776367188);
                var rotX = (float) -Mth.wrapDegrees(Math.atan((facing.y() + off.dy) / facing.length()) * 57.295776367188);

                var rightYRotDegree = (float) (-rightYRot * 57.295776367188);

                player.setYRot(rightYRotDegree);
                player.setYBodyRot(rightYRotDegree);
                player.setXRot(rotX);

                camera.invokeMove(off.dx, off.dy, off.dz);
                camera.invokeSetRotation(rotY, rotX);
                return;
            } catch (Exception ignored) {}
        }

        camera.invokeSetRotation(yRot, xRot);

        // 根据 offset 进行移动
        camera.invokeMove(off.dx, off.dy, off.dz);
    }

}
