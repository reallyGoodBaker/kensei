package top.rgb39.kensei_client.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class EnhancedPlayerModel extends GeoModel<GeoAnimatable> {

    @Override
    public ResourceLocation getModelResource(GeoAnimatable animatable) {
        return new ResourceLocation("kensei", "geo/humanoid.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable animatable) {
        return new ResourceLocation("kensei", "textures/entity/alex-r18.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return new ResourceLocation("kensei", "animations/humanoid.animation.json");
    }
}
