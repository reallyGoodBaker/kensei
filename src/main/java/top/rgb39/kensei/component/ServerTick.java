package top.rgb39.kensei.component;

import top.rgb39.ecs.annotation.Component;

@Component(singleton = true)
public class ServerTick {
    public float dt; // 上一帧到这一帧的时间差（以秒为单位）
    public int skipped; // 跳过的帧数
}
