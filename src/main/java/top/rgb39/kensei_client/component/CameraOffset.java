package top.rgb39.kensei_client.component;

import top.rgb39.ecs.annotation.Component;

@Component(singleton = true)
public class CameraOffset {
    public double dx = 1;
    public double dy;
    public double dz = 3;
    public double telescopicScale = 0.5;
}
