package top.rgb39.kensei_client.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import top.rgb39.kensei_client.annotation.CustomItem;

public class GeckoItemModel extends GeoModel<GeckoItem> {

    @Override
    public ResourceLocation getModelResource(GeckoItem animatable) {
        CustomItem itemInfo = animatable.getClass().getAnnotation(CustomItem.class);

        if (itemInfo == null) {
            return null;
        }

        return new ResourceLocation(
                itemInfo.namespace(),
                String.format("geo/%s.geo.json", itemInfo.name())
        );
    }

    @Override
    public ResourceLocation getTextureResource(GeckoItem animatable) {
        CustomItem itemInfo = animatable.getClass().getAnnotation(CustomItem.class);

        if (itemInfo == null) {
            return null;
        }

        return new ResourceLocation(
                itemInfo.namespace(),
                String.format("textures/item/%s.png", itemInfo.name())
        );
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoItem animatable) {
        CustomItem itemInfo = animatable.getClass().getAnnotation(CustomItem.class);

        if (itemInfo == null) {
            return null;
        }

        return new ResourceLocation(
                itemInfo.namespace(),
                String.format("animations/%s.animation.json", itemInfo.name())
        );
    }
}
