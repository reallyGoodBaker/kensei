package top.rgb39.kensei_client.component;

import top.rgb39.ecs.annotation.Component;

@Component(singleton = true)
public class CameraFading {
    public long startTime;
    public int duration = 300;
    public double dx;
    public double dy;
    public double dz;
}
