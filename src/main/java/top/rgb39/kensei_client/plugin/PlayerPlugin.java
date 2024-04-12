package top.rgb39.kensei_client.plugin;

import top.rgb39.ecs.annotation.Reflect;
import top.rgb39.ecs.annotation.System;
import top.rgb39.ecs.arch.App;
import top.rgb39.ecs.executor.RuntimeLabel;
import top.rgb39.ecs.plugin.Plugin;

public class PlayerPlugin implements Plugin {

    public static final Long CLIENT_ID = -1L;

    @Override
    public void build(App app) {
//        Registry.register(
//                Registries.ENTITY_TYPE, "minecraft:player",
//                FabricEntityTypeBuilder
//                        .create(MobCategory.CREATURE, PlayerEntity::new)
//                        .build()
//        );
    }

    @System(runtimeLabel = RuntimeLabel.Startup)
    void init(
        @Reflect(App.class) App app
    ) {
//        app.addEntity(
//                CLIENT_ID,
//                new CameraOffset(),
//                new CameraFading()
//        );
    }
}
