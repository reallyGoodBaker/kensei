package top.rgb39.kensei_client.system;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import top.rgb39.ecs.annotation.Reflect;
import top.rgb39.ecs.annotation.Slot;
import top.rgb39.ecs.annotation.System;
import top.rgb39.ecs.util.Option;
import top.rgb39.ecs.util.Option.T;
import top.rgb39.kensei_client.InternalRuntime;
import top.rgb39.kensei_client.component.*;
import top.rgb39.kensei_client.component.camera.CameraFading;
import top.rgb39.kensei_client.component.camera.CameraOffset;
import top.rgb39.kensei_client.component.camera.CameraRotationFading;
import top.rgb39.kensei_client.component.camera.Overlook;
import top.rgb39.kensei_client.mixin.CameraAccessor;
import top.rgb39.kensei_client.util.Cameras;

public class CameraSystems {

    @System(runtimeLabel = InternalRuntime.RENDER_LEVEL)
    void updateCamera(
        @top.rgb39.ecs.annotation.Entity long id,
        @Reflect(RenderLevelConfig.class) RenderLevelConfig conf,
        @Reflect(Minecraft.class) Minecraft minecraft,
        @Reflect(CameraOffset.class) CameraOffset off,
        @Reflect(CameraFading.class) CameraFading fading,
        @Reflect(CameraRotationFading.class) CameraRotationFading rotationFading,
        @Reflect(Overlook.class) Overlook overlook,
        @Slot(TargetLock.class) TargetLock lock
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
        var camera = (CameraAccessor) mainCamera;
        var dt = conf.dt;
        var player = it.v();

        CameraType type = minecraft.options.getCameraType();
        if (type.isFirstPerson()) return;

        var plx = Mth.lerp(f, player.xo, player.getX());
        var ply = Mth.lerp(f, player.yo, player.getY()) + player.getEyeHeight();
        var plz = Mth.lerp(f, player.zo, player.getZ());
        var offsetMoveVec = new Vec3(off.dx, off.dy, off.dz);

        if (lock.targetId != 0 && overlook.enable) {
            offsetMoveVec = offsetMoveVec.add(overlook.dx, overlook.dy, overlook.dz);
        }

        var cameraOffset = offset(mainCamera, offsetMoveVec);

        // 摄像机回到眼睛
        camera.invokeSetPosition(plx, ply, plz);

        // 处理第三人称面向玩家的视角
        if (type.isMirrored()) {
            move(camera, offsetMoveVec);
            return;
        }

        // 根据锁定的生物进行旋转
        float yRot = player.yRotO;
        float xRot = player.xRotO;
        Cameras.fade((int) dt, off, fading);
        var camRotX = mainCamera.getXRot();
        var camRotY = mainCamera.getYRot();

        if (lock.targetId == 0) {
            rot(camera, Cameras.fadeRotation((int) dt, rotationFading, yRot, xRot, camRotY, camRotX));
            move(camera, offsetMoveVec);
            return;
        }

        Entity en;

        try (Level lvl = player.level()) {
            en = lvl.getEntity((int) lock.targetId);
            if (en == null) {
                lvl.close();
                return;
            }
        } catch (Exception ignored) {
            return;
        }

        var ex = Mth.lerp(f, en.xo, en.getX());
        var ey = Mth.lerp(f, en.yo, en.getY()) + en.getEyeHeight();
        var ez = Mth.lerp(f, en.zo, en.getZ());
        var enPosVec = new Vec3(ex, ey, ez);
        var facing = new Vec3(ex - plx, ey - ply, ez - plz);
        var entityCameraVec = cameraOffset.subtract(facing);
        var rots = Cameras.getRotation(facing, cameraOffset);
        var playerYRot = wrapDegree(rots[0]);
        var playerXRot = wrapDegree(rots[1]);
        var rotY = wrapDegree(rots[2]);
        var rotX = wrapDegree(rots[3]);

        player.setYRot(playerYRot);
        player.setYBodyRot(playerYRot);
        player.setXRot(playerXRot);

        var fovScale = 70d / minecraft.options.fov().get();

        if (fovScale > 1) {
            entityCameraVec = entityCameraVec.multiply(fovScale, fovScale, fovScale);
        }

        var dist = facing.length();
        if (dist < 5) {
            var scale = 1 - (5 - dist) * off.telescopicScale / 5;
            entityCameraVec = entityCameraVec.multiply(scale, scale, scale);
        }

        pos(camera, entityCameraVec.add(enPosVec));
        rot(camera, Cameras.fadeRotation((int) dt, rotationFading, rotY, rotX, camRotY, camRotX));
    }

    float wrapDegree(double rad) {
        return (float) -Mth.wrapDegrees(rad * 57.295776367188);
    }

    void move(CameraAccessor camera, Vec3 offset) {
        camera.invokeMove(-offset.z, offset.y, -offset.x);
    }

    void pos(CameraAccessor camera, Vec3 offset) {
        camera.invokeSetPosition(offset.x, offset.y, offset.z);
    }

    void rot(CameraAccessor camera, float... args) {
        camera.invokeSetRotation(args[0], args[1]);
    }

    // modified from Minecraft
    Vec3 offset(Camera camera, Vec3 offset) {
        var d = -offset.z;
        var e = offset.y;
        var f = -offset.x;

        var forwards = camera.getLookVector();
        var up = camera.getUpVector();
        var left = camera.getLeftVector();

        double g = (double)forwards.x() * d + (double)up.x() * e + (double)left.x() * f;
        double h = (double)forwards.y() * d + (double)up.y() * e + (double)left.y() * f;
        double i = (double)forwards.z() * d + (double)up.z() * e + (double)left.z() * f;

        return new Vec3(g, h, i);
    }

    public static Vector2f worldToScreen(Vec3 worldPos, Camera cam, int fov, double ws) {
        var camPos = cam.getPosition();
        var pointVec = worldPos.subtract(camPos);
        var camDir = cam.getLookVector();
        var camUp = cam.getUpVector();
        var camLeft = cam.getLeftVector();
        var pointVec3f = pointVec.toVector3f();

        var x = camLeft.dot(pointVec3f);
        var y = camUp.dot(pointVec3f);
        var z = camDir.dot(pointVec3f);

        var scale = fov / z * ws;

        return new Vector2f((float) (x * scale), (float) (y * scale));
    }
}
