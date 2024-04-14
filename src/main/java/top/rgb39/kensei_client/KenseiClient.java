package top.rgb39.kensei_client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import top.rgb39.ecs.arch.App;
import top.rgb39.ecs.executor.RuntimeChain;
import top.rgb39.ecs.executor.RuntimeLabel;
import top.rgb39.ecs.executor.RuntimeSchedular;
import top.rgb39.ecs.plugin.*;
import top.rgb39.ecs.util.Logger;
import top.rgb39.kensei_client.component.CameraFading;
import top.rgb39.kensei_client.component.CameraOffset;
import top.rgb39.kensei_client.component.CameraRotationFading;
import top.rgb39.kensei_client.plugin.ItemGroupLoader;
import top.rgb39.kensei_client.plugin.ItemLoader;
import top.rgb39.kensei_client.plugin.PlayerPlugin;
import top.rgb39.kensei_client.plugin.RenderPlugin;

public class KenseiClient implements ClientModInitializer {

    public static App clientApp = App.empty();

    final static Logger D = Logger.DEBUG;

    @Override
    public void onInitializeClient() {
        Logger.enableLogger(Logger.DEBUG);
        initClient(Minecraft.getInstance());
    }

    private void initClient(Minecraft mc) {
        clientApp.addPlugins(
                new ClassScannerPlugin("top/rgb39/kensei_client", "mod/kensei/client"),
                new ParameterMatchers(),
                new RuntimePlugin(),
                new RenderPlugin(),
                new Events(),
                new SystemLoaderPlugin(),
                new ItemLoader(),
                new ItemGroupLoader(),
                new PlayerPlugin()
        )
        .addSingleComponent(clientApp)
        .addSingleComponent(mc)
        .addSingleComponent(new CameraOffset())
        .addSingleComponent(new CameraFading())
        .addSingleComponent(new CameraRotationFading())
        .getRuntimeManager()
        .setScheduler(new ClientTickScheduler());

        clientApp.run();
    }

    static class ClientTickScheduler implements RuntimeSchedular {

        void setup(RuntimeChain runtimeChain, App app) {
            runtimeChain
                    .getSystemChain(RuntimeLabel.Startup)
                    .runWithOnlyReflects(app);
        }

        @Override
        public void schedule(RuntimeChain runtimeChain, App app) {
            setup(runtimeChain, app);

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                runtimeChain.scheduleOnce(app);
            });

            KenseiListeners.RENDER_LEVEL_LISTENER = () -> {
//                D.i(Arrays.join(runtimeChain.getSystemChain(RenderRuntime.RENDER_LEVEL).getSystems(), ", "));
                runtimeChain
                        .getSystemChain(RenderRuntime.RENDER_LEVEL)
                        .run(app);
            };
        }

        @Override
        public void cancel() {

        }
    }
}

