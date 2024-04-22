package top.rgb39.kensei_client.plugin;

import top.rgb39.ecs.arch.App;
import top.rgb39.ecs.executor.RuntimeChain;
import top.rgb39.ecs.executor.SystemChain;
import top.rgb39.ecs.plugin.Plugin;
import top.rgb39.kensei_client.InternalRuntime;

public class ExtraRuntimePlugin implements Plugin {
    @Override
    public void build(App app) {
        RuntimeChain chain = app.getRuntimeManager().getRuntimeChain();

        chain.setSystemChain(InternalRuntime.RENDER_LEVEL, new SystemChain());
        chain.setSystemChain(InternalRuntime.CLIENT_READY, new SystemChain());
        chain.setSystemChain(InternalRuntime.GUI_RENDER, new SystemChain());
    }
}
