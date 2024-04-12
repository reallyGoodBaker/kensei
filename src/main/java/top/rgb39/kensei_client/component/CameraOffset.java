package top.rgb39.kensei_client.component;

import top.rgb39.ecs.annotation.Component;

@Component(singleton = true)
public class CameraOffset {
    public double dx = -2.4;
    public double dy;
    public double dz = -0.4;
}
