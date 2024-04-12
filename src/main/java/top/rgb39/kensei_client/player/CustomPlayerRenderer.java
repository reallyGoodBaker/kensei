package top.rgb39.kensei_client.player;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CustomPlayerRenderer extends GeoEntityRenderer<PlayerEntity> {

    public CustomPlayerRenderer(EntityRendererProvider.Context renderManager, GeoModel<PlayerEntity> model) {
        super(renderManager, model);
    }
}
