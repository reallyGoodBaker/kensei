package top.rgb39.kensei_client.system;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.rgb39.ecs.annotation.System;
import top.rgb39.ecs.annotation.*;
import top.rgb39.ecs.arch.App;
import top.rgb39.ecs.executor.RuntimeLabel;
import top.rgb39.ecs.util.Logger;
import top.rgb39.kensei_client.component.CameraFading;
import top.rgb39.kensei_client.component.CameraRotationFading;
import top.rgb39.kensei_client.component.PlayerStatus;
import top.rgb39.kensei_client.events.LockEvent;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Players {

    Minecraft mc;

    @System(runtimeLabel = RuntimeLabel.Startup)
    void listenPlayer(
            @Reflect(App.class) App app
    ) {
        ClientEntityEvents.ENTITY_LOAD.register((en, w) -> {
            if (!"entity.minecraft.player".equals(en.getType().toString())) {
                return;
            }

            app.addEntity(
                    en.getId(),
                    new PlayerStatus()
            );
        });

        mc = Minecraft.getInstance();
    }

    void unlock(CameraFading fading, PlayerStatus status, CameraRotationFading rotation) {
        var current = java.lang.System.currentTimeMillis();
        status.lockedEntity = 0;
        fading.dx = 1;
        fading.dz = 3;
        fading.startTime = current;
        rotation.startTime = current;
        rotation.duration = 120;
    }

    void lock(CameraFading fading, CameraRotationFading rotation, PlayerStatus status, Minecraft mc, long playerId, LockEntityPredicate predicate) {
        if (mc.player != null && mc.player.getId() == playerId) {
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
        var current = java.lang.System.currentTimeMillis();
        fading.dx = 1.6;
        fading.dz = 1.8;
        fading.startTime = current;
        status.lockedEntity = predicate.targetEntity.getId();
        rotation.startTime = current + 100;
        rotation.duration = 160;
    }

    @System(runtimeLabel = RuntimeLabel.BeforeUpdate)
    void toggleLock(
            @Read(LockEvent.class) Stream<LockEvent> lockEvents,
            @Entity long playerId,
            @Slot(PlayerStatus.class) PlayerStatus status,
            @Reflect(CameraFading.class) CameraFading fading,
            @Reflect(CameraRotationFading.class) CameraRotationFading rotationFading
    ) {
        if (lockEvents.findAny().isEmpty()) return;
        if (status.lockedEntity != 0) {
            unlock(fading, status, rotationFading);
            return;
        }

        try (Level lvl = mc.level) {
            if (Objects.isNull(lvl))
                return;

            var pl = lvl.getEntity((int) playerId);
            if (Objects.isNull(pl))
                return;

            var blockPos = pl.blockPosition();
            var predicate = new LockEntityPredicate(pl);

            lvl.getEntities(
                    pl,
                    new AABB(blockPos.offset(-10, -3, -10), blockPos.offset(10, 3, 10)),
                    predicate
            );

            if (Objects.isNull(predicate.targetEntity))
                return;

            lock(fading, rotationFading, status, mc, playerId, predicate);
        } catch(Exception ignored) {}
    }

    static class LockEntityPredicate implements Predicate<net.minecraft.world.entity.Entity> {

        net.minecraft.world.entity.Entity pl;
        Vec3 view;
        Vec3 pos;

        double match = -1;
        net.minecraft.world.entity.Entity targetEntity;

        LockEntityPredicate(net.minecraft.world.entity.Entity pl) {
            this.pl = pl;
            view = pl.getLookAngle();
            pos = pl.position();
        }

        @Override
        public boolean test(net.minecraft.world.entity.Entity entity) {
            if (entity.is(pl) || EntityType.ITEM.equals(entity.getType())) return false;
            Vec3 posOffset = entity.position().subtract(pos);
            var m = view.dot(posOffset) / (view.length() * posOffset.length());
            if (m > match && m > 0.707106781) {
                match = m;
                targetEntity = entity;
            }
            return true;
        }
    }

    @System(runtimeLabel = RuntimeLabel.BeforeUpdate)
    void unlockUnusual(
            @Entity long id,
            @Slot(PlayerStatus.class) PlayerStatus status,
            @Reflect(CameraFading.class) CameraFading fading,
            @Reflect(CameraRotationFading.class) CameraRotationFading rotation
    ) {
        ClientLevel level = mc.level;
        assert level != null;
        if (status.lockedEntity == 0) return;

        var entity = level.getEntity(status.lockedEntity);
        if (Objects.isNull(entity)) {
            unlock(fading, status, rotation);
            return;
        }

        var player = level.getEntity((int) id);
        if (player != null && entity.distanceToSqr(player) > 225) {
            unlock(fading, status, rotation);
        }
    }
}