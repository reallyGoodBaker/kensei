package top.rgb39.kensei_client.component.camera;

import top.rgb39.ecs.annotation.Component;

@Component(singleton = true)
public class CameraRotationFading {
    public long startTime;
    public int duration = 120;
    public float rotXO;
    public float rotYO;
}
