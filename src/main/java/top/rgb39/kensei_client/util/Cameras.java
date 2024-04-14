package top.rgb39.kensei_client.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import top.rgb39.ecs.util.Logger;
import top.rgb39.kensei_client.KenseiClient;
import top.rgb39.kensei_client.component.CameraFading;
import top.rgb39.kensei_client.component.CameraOffset;
import top.rgb39.kensei_client.component.CameraRotationFading;
import top.rgb39.kensei_client.mixin.CameraAccessor;

public class Cameras {

    final static int d = 50;

    public static void fade(int dt, CameraOffset off, CameraFading fading) {
        var currentTime = System.currentTimeMillis();
        var last = currentTime - fading.startTime;
        var duration = fading.duration;
        var remain = duration - last;

        if (duration + d < last)
            return;

        var progress = ((double) dt) / remain;

        off.dx = lerp(off.dx, fading.dx, progress);
        off.dy = lerp(off.dy, fading.dy, progress);
        off.dz = lerp(off.dz, fading.dz, progress);
//        Logger.DEBUG.i("fade: %s, %s, %s".formatted(off.dx, off.dy, off.dz));
    }

    public static double[] getRotation(Vec3 facing, Vec3 offset) {
        var cameraVec = offset.subtract(facing).reverse();
        var playerYRot = Mth.atan2(facing.x, facing.z);
        var playerXRot = Mth.atan2(facing.y, facing.length());

        var rotY = Math.atan2(cameraVec.x, cameraVec.z);
        var rotX = Math.asin(cameraVec.y / cameraVec.length());

        return new double[] {
                playerYRot, playerXRot,
                rotY, rotX
        };
    }

    public static float[] fadeRotation(int dt, CameraRotationFading rotationFading, float y, float x, float oy, float ox) {
        var currentTime = System.currentTimeMillis();
        var last = currentTime - rotationFading.startTime;
        var duration = rotationFading.duration;
        var remain = duration - last;
        var progress = ((double) dt) / remain;

        oy += 360;
        y += 360;

        // 没有设置渐变
        if (duration < last || progress >= 1) {
            rotationFading.rotXO = ox;
            rotationFading.rotYO = oy;
            return new float[] { y - 360, x };
        }

        var ty = (float) lerp(rotationFading.rotYO, y, progress);
        var tx = (float) lerp(rotationFading.rotXO, x, progress);

        rotationFading.rotXO = tx;
        rotationFading.rotYO = ty;

        return new float[] { ty - 360, tx - 360 };
    }

    public static double lerp(double from, double to, double progress) {
        return from + (to - from) * Math.max(0, Math.min(progress, 1));
    }
}
