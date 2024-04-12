package top.rgb39.kensei;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import top.rgb39.ecs.arch.App;
import top.rgb39.ecs.executor.RuntimeChain;
import top.rgb39.ecs.executor.RuntimeLabel;
import top.rgb39.ecs.executor.RuntimeSchedular;
import top.rgb39.ecs.util.Logger;
import top.rgb39.kensei.component.ServerTick;

public class Kensei implements ModInitializer {

    @Override
    public void onInitialize() {
        Logger.enableLogger(Logger.ECS);
        Logger.enableLogger(Logger.DEBUG);

        start();
    }

    void start() {
        App app = App.create(
            "top/rgb39/kensei",
            "mod/kensei/server"
        );

        app.getRuntimeManager().setScheduler(new ServerTickScheduler());

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            app.addSingleComponent(server)
                .addSingleComponent(app)
                .run();
        });

    }

    static class ServerTickScheduler implements RuntimeSchedular {

        long now = System.currentTimeMillis();

        @Override
        public void schedule(RuntimeChain runtimeChain, App app) {
            runtimeChain
                    .getSystemChain(RuntimeLabel.Startup)
                    .runWithOnlyReflects(app);

            ServerTick tick = (ServerTick) app.getSingletonComponent(ServerTick.class);

            ServerTickEvents.END_WORLD_TICK.register(world -> {
                long current = System.currentTimeMillis();
                long dt = current - now;
                now = current;

                tick.dt = dt;

                runtimeChain.scheduleOnce(app);
            });
        }

        @Override
        public void cancel() {

        }
    }

}
