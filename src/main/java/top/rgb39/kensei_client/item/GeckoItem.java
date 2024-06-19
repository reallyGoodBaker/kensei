package top.rgb39.kensei_client.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GeckoItem extends Item implements GeoItem, AnimationController.AnimationStateHandler<GeoAnimatable> {
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            public GeckoItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null)
                    renderer = new GeckoItemRenderer();

                return renderer;
            }
        });
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
