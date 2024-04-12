package top.rgb39.kensei_client.util;

import top.rgb39.kensei_client.KenseiClient;
import top.rgb39.kensei_client.component.CameraFading;
import top.rgb39.kensei_client.component.CameraOffset;
import top.rgb39.kensei_client.mixin.CameraAccessor;

public class Cameras {

    final static int d = 50;
    static CameraFading fading;

    public static void fade(int dt, CameraOffset off) {
        if (fading == null)
            fading = (CameraFading) KenseiClient.clientApp.getSingletonComponent(CameraFading.class);

        var currentTime = System.currentTimeMillis();

        if (fading.startTime + fading.duration + d < currentTime)
            return;

        var vx = fading.dx / fading.duration;
        var vy = fading.dy / fading.duration;
        var vz = fading.dz / fading.duration;

        var dx = vx * dt;
        var dy = vy * dt;
        var dz = vz * dt;

        off.dx = limMax(off.dx + dx, fading.dx);
        off.dy = limMax(off.dy + dy, fading.dy);
        off.dz = limMax(off.dz + dz, fading.dz);
    }

    public static double limMax(double num, double max) {
        return max < 0 ? Math.max(max, num) : Math.min(max, num);
    }
}
