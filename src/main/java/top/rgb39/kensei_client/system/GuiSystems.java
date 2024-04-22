package top.rgb39.kensei_client.system;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import top.rgb39.ecs.annotation.Reflect;
import top.rgb39.ecs.annotation.System;
import top.rgb39.kensei_client.InternalRuntime;
import top.rgb39.kensei_client.KenseiClient;
import top.rgb39.kensei_client.component.GuiRenderConfig;
import top.rgb39.kensei_client.component.TargetLock;
import top.rgb39.kensei_client.util.Positions;

public class GuiSystems {

    @System(runtimeLabel = InternalRuntime.GUI_RENDER)
    void renderCrosshair(@Reflect(GuiRenderConfig.class) GuiRenderConfig conf) {
        var screenWidth = conf.screenWidth;
        var screenHeight = conf.screenHeight;
        var guiGraphics = conf.guiGraphics;
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var cam = minecraft.gameRenderer.getMainCamera();
        int fov = minecraft.options.fov().get();
        var lock = (TargetLock) KenseiClient.clientApp.getComponent(player.getId(), TargetLock.class);
        var camType = minecraft.options.getCameraType();
        var ws = minecraft.getWindow().getGuiScale();
        var nws = (float) (1 / ws);

        if (
                camType.isFirstPerson() ||
                camType.isMirrored() ||
                lock.targetId != 0
        ) {
            return;
        }

        var centerX = screenWidth / 2;
        var centerY = screenHeight / 2;

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        var hitResult = minecraft.hitResult;
        var hitType = hitResult.getType();
        if (hitType == HitResult.Type.MISS) {
            pose.last().pose().translate(centerX, centerY, 0).scale(nws);
            guiGraphics.fill(-11, -2, 11, 2, 0x22000000);
            guiGraphics.fill(-2, -11, 2, 11, 0x22000000);
            guiGraphics.fill(-10, -1, 10, 1, 0x44ffffff);
            guiGraphics.fill(-1, -10, 1, 10, 0x44ffffff);
            pose.popPose();
            return;
        }

        var hitPos = hitResult.getLocation();
        var screenPoint = CameraSystems.worldToScreen(hitPos, cam, fov, ws);

        if (hitType == HitResult.Type.BLOCK) {
            var x = centerX - (int) screenPoint.x;
            var y = centerY - (int) screenPoint.y;

            pose.last().pose().translate(x - 5, y, 0).scale(nws);
            guiGraphics.fill(-11, -2, 11, 2, 0x88000000);
            guiGraphics.fill(-2, -11, 2, 11, 0x88000000);
            guiGraphics.fill(-10, -1, 10, 1, 0xddffffff);
            guiGraphics.fill(-1, -10, 1, 10, 0xddffffff);
            pose.popPose();
            return;
        }

        if (hitType == HitResult.Type.ENTITY) {
            var x = centerX - (int) screenPoint.x;
            var y = centerY - (int) screenPoint.y;

            pose.last().pose().translate(x - 5, y, 0).scale(nws);
            guiGraphics.fill(-11, -2, 11, 2, 0x88000000);
            guiGraphics.fill(-2, -11, 2, 11, 0x88000000);
            guiGraphics.fill(-10, -1, 10, 1, 0xddffff44);
            guiGraphics.fill(-1, -10, 1, 10, 0xddffff44);
            pose.popPose();
        }
    }

    @System(runtimeLabel = InternalRuntime.GUI_RENDER)
    void renderHud(@Reflect(GuiRenderConfig.class) GuiRenderConfig conf) {
        var screenWidth = conf.screenWidth;
        var screenHeight = conf.screenHeight;
        var guiGraphics = conf.guiGraphics;
        var f = conf.f;
        var centerX = screenWidth / 2;
        var centerY = screenHeight / 2;
        var pose = guiGraphics.pose();
        var minecraft = Minecraft.getInstance();
        var cam = minecraft.gameRenderer.getMainCamera();
        int fov = minecraft.options.fov().get();
        var camType = minecraft.options.getCameraType();
        var ws = minecraft.getWindow().getGuiScale();
        var nws = (float) (1 / ws);
        var hitResult = minecraft.hitResult;
        var hitType = hitResult.getType();
        var lock = (TargetLock) KenseiClient.clientApp.getComponent(minecraft.player.getId(), TargetLock.class);

        if (camType.isMirrored()) {
            return;
        }

        if (
                lock.targetId == 0 &&
                (hitType != HitResult.Type.ENTITY || !(hitResult instanceof EntityHitResult))
        ) {
            return;
        }

        pose.pushPose();
        var e = lock.targetId != 0
                ? minecraft.level.getEntity((int) lock.targetId)
                : ((EntityHitResult) hitResult).getEntity();

        if (!(e instanceof LivingEntity en)) {
            return;
        }

        var hudPoint = CameraSystems.worldToScreen(Positions.pos(en, f), cam, fov, ws);
        var hudX = centerX - hudPoint.x;
        var hudY = centerY - hudPoint.y;
        var maxHealth = en.getMaxHealth();
        var health = Math.min(en.getHealth() / (maxHealth == 0 ? Double.MAX_VALUE : maxHealth), 1);
        pose.last().pose().translate(hudX + 40, hudY - 20, 0).scale(nws);
        guiGraphics.fill(-8, -8, 148, 48, 0x88000000);
        guiGraphics.fill(0, 20, 140, 40, 0x88880000);
        guiGraphics.fill(0, 20, (int) (140 * health), 40, 0xff008800);
        pose.popPose();
        pose.pushPose();
        pose.last().pose().translate(hudX + 40, hudY - 20, 0).scale(nws * 2);
        guiGraphics.drawString(minecraft.font, en.getDisplayName(), 0, 0, 0xffffffff);
        var healthLabel = "%.1f/%.1f".formatted(Math.min(en.getHealth(), en.getMaxHealth()), en.getMaxHealth());
        guiGraphics.drawString(minecraft.font, healthLabel, (70 - minecraft.font.width(healthLabel)) / 2, 11, 0xffffffff);
        pose.popPose();
    }
}
