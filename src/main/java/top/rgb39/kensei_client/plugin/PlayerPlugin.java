package top.rgb39.kensei_client.plugin;

import com.mojang.authlib.GameProfile;
import com.tom.cpm.api.ICPMPlugin;
import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.api.ICommonAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;

public class PlayerPlugin implements ICPMPlugin {

    static IClientAPI.PlayerRenderer<Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> renderer;

    @Override
    public void initClient(IClientAPI api) {
        renderer = api.createPlayerRenderer(Model.class, ResourceLocation.class, RenderType.class, MultiBufferSource.class, GameProfile.class);
        try {
            InputStream is = Minecraft.getInstance().getResourceManager().open(new ResourceLocation("kensei", "cpm/rigged_player_slim.cpmmodel"));
            renderer.setLocalModel(api.loadModel("slim", is));
            renderer.postRender();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initCommon(ICommonAPI api) {
    }

    @Override
    public String getOwnerModId() {
        return "kensei";
    }
}
