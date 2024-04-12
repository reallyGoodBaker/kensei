package top.rgb39.kensei_client.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Invoker("setPosition")
    void invokeSetPosition(double x, double y, double z);

    @Invoker("move")
    void invokeMove(double x, double y, double z);

    @Invoker("getMaxZoom")
    double invokeGetMaxZoom(double distance);

    @Invoker("setRotation")
    void invokeSetRotation(float yRot, float xRot);
}
