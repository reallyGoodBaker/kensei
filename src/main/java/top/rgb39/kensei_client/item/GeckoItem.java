package top.rgb39.kensei_client.item;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

import java.util.function.Consumer;

public class GeckoItem extends Item implements GeoItem, AnimationController.AnimationStateHandler<GeoAnimatable> {
    private final AnimatableInstanceCache instanceCache = new SingletonAnimatableInstanceCache(this);

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        GeoItem.super.createGeoRenderer(consumer);
    }

    public GeckoItem(Properties properties) {
        super(properties);
    }

    public GeckoItem() {
        super(new Item.Properties());
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "controller",
                0,
                this::predicate
        ));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> animationState) {
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }

    @Override
    public PlayState handle(AnimationState state) {
        return null;
    }
}
