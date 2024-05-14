package top.rgb39.kensei_client.component;

import net.minecraft.client.Camera;
import top.rgb39.ecs.annotation.Component;

@Component(singleton = true)
public class RenderLevelConfig {
    public float f;
    public long l;
    public long dt;
    public Camera mainCamera;
}
