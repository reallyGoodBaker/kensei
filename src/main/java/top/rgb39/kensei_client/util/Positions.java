package top.rgb39.kensei_client.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Positions {
    public static Vec3 pos(Entity en, float f) {
        var ex = Mth.lerp(f, en.xo, en.getX());
        var ey = Mth.lerp(f, en.yo, en.getY()) + en.getEyeHeight();
        var ez = Mth.lerp(f, en.zo, en.getZ());

        return new Vec3(ex, ey, ez);
    }
}
