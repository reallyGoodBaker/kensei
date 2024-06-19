package top.rgb39.kensei_client.plugin;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tom.cpm.api.ICPMPlugin;
import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.api.ICommonAPI;
import com.tom.cpm.shared.animation.AnimationEngine;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import top.rgb39.ecs.annotation.System;
import top.rgb39.kensei_client.InternalRuntime;

import java.io.InputStream;

public class PlayerPlugin implements ICPMPlugin {

    static IClientAPI api;
    static IClientAPI.PlayerRenderer<Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> renderer;
    static IClientAPI.LocalModel model;

    @Override
    public void initClient(IClientAPI api) {
        PlayerPlugin.api = api;
        renderer = api.createPlayerRenderer(Model.class, ResourceLocation.class, RenderType.class, MultiBufferSource.class, GameProfile.class);
        EntityRendererRegistry.register(EntityType.PLAYER, PlayerPlugin.PlayerRenderer::new);
    }

    @System(runtimeLabel = InternalRuntime.CLIENT_READY)
    void whenReady() {
        try {
            InputStream is = Minecraft.getInstance().getResourceManager().open(new ResourceLocation("kensei", "cpm/rigged_player_slim.cpmmodel"));
            model = api.loadModel("slim", is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initCommon(ICommonAPI api) {}

    @Override
    public String getOwnerModId() {
        return "kensei";
    }

    public static class PlayerRenderer extends LivingEntityRenderer<Player, PlayerModel<Player>> {

        public PlayerRenderer(EntityRendererProvider.Context pContext) {
            super(pContext, new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false), 0);
        }

        @Override
        public ResourceLocation getTextureLocation(Player entity) {
            return renderer.getDefaultTexture();
        }

        @Override
        public void render(Player pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
            renderer.setRenderModel(model);
            renderer.setRenderType(RenderType::entityTranslucent);
            //Pose model using renderer.getAnimationState(), setActivePose(name) or setActiveGesture(name)
            renderer.preRender(pBuffer, AnimationEngine.AnimationMode.PLAYER);
            if(renderer.getDefaultTexture() != null) {
                super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
            } else {
                renderNameTag(pEntity, pEntity.getDisplayName(), pMatrixStack, pBuffer, pPackedLight, pPartialTicks);
            }
            renderer.postRender();
        }
    }

}
