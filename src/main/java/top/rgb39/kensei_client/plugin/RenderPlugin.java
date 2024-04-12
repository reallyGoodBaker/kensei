package top.rgb39.kensei_client.plugin;

import top.rgb39.ecs.arch.App;
import top.rgb39.ecs.executor.RuntimeChain;
import top.rgb39.ecs.executor.SystemChain;
import top.rgb39.ecs.plugin.Plugin;
import top.rgb39.kensei_client.RenderRuntime;

public class RenderPlugin implements Plugin {
    @Override
    public void build(App app) {
        RuntimeChain chain = app.getRuntimeManager().getRuntimeChain();

        chain.setSystemChain(RenderRuntime.RENDER_LEVEL, new SystemChain());
    }
}
