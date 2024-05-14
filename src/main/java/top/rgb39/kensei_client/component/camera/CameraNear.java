package top.rgb39.kensei_client.component.camera;

import top.rgb39.ecs.annotation.Component;

@Component(singleton = true)
public class CameraNear {
    public boolean enable = false;
    public float value = 0.05f;
}
