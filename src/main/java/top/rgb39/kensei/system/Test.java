package top.rgb39.kensei.system;

import net.minecraft.server.MinecraftServer;
import top.rgb39.ecs.annotation.Reflect;
import top.rgb39.ecs.annotation.System;
import top.rgb39.ecs.executor.RuntimeLabel;
import top.rgb39.ecs.util.Logger;

public class Test {

    @System(runtimeLabel = RuntimeLabel.Startup)
    void getReflects(
            @Reflect(MinecraftServer.class) MinecraftServer server
    ) {
        Logger.DEBUG.i(Logger.FontColors.BLUE, "server: %b\n", server);
    }

}
