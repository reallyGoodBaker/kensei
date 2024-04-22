package top.rgb39.kensei_client.component.camera;

import top.rgb39.ecs.annotation.Component;

@Component(singleton = true)
public class Overlook {
    public boolean enable;
    public double dy = 1;
    public double dx;
    public double dz = 2;
}
