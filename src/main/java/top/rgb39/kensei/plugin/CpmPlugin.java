package top.rgb39.kensei.plugin;

import com.tom.cpm.api.ICPMPlugin;
import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.api.ICommonAPI;

public class CpmPlugin implements ICPMPlugin {

    public static ICommonAPI api;

    @Override
    public void initClient(IClientAPI api) {}

    @Override
    public void initCommon(ICommonAPI api) {
        CpmPlugin.api = api;
    }

    @Override
    public String getOwnerModId() {
        return "kensei";
    }
}
