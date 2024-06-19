package top.rgb39.kensei_client.system;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import top.rgb39.ecs.annotation.Reflect;
import top.rgb39.ecs.annotation.System;
import top.rgb39.ecs.annotation.Write;
import top.rgb39.ecs.executor.RuntimeLabel;
import top.rgb39.ecs.plugin.EventWriter;
import top.rgb39.kensei_client.InternalRuntime;
import top.rgb39.kensei_client.KenseiClient;
import top.rgb39.kensei_client.component.TargetLock;
import top.rgb39.kensei_client.component.camera.Overlook;
import top.rgb39.kensei_client.events.KeyDown;
import top.rgb39.kensei_client.events.KeyUp;
import top.rgb39.kensei_client.events.LockEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeyboardSystems {

    final static String CATEGORY = "category.kensei.keybinding";
    final static String LOCK = "key.kensei.lock";
    final static String OVERLOOK = "key.kensei.overlook";

    @System(runtimeLabel = RuntimeLabel.Startup)
    void setupKeybindings() {
        registerKeyboardEvent(
                KeyBindingHelper.registerKeyBinding(new KeyMapping(
                        LOCK,
                        InputConstants.Type.KEYSYM,
                        InputConstants.KEY_G,
                        CATEGORY
                )),
                (writer, flow) -> writer.write(new LockEvent())
        );

        registerKeyboardEvent(
                KeyBindingHelper.registerKeyBinding(new KeyMapping(
                        OVERLOOK,
                        InputConstants.Type.KEYSYM,
                        InputConstants.KEY_V,
                        CATEGORY
                )),
                (writer, flow) -> {
                    var overlook = (Overlook) KenseiClient.clientApp.getSingletonComponent(Overlook.class);
                    overlook.enable = !overlook.enable;
                }
        );
    }

    private static final Map<Integer, KeyboardEventInfo> infos = new ConcurrentHashMap<>();
    private static final Set<Integer> pressing = new CopyOnWriteArraySet<>();

    public static void fireEvents(
            @Write EventWriter writer
    ) {
        for (Map.Entry<Integer, KeyboardEventInfo> entry : infos.entrySet()) {
            var info = entry.getValue();
            var key = entry.getKey();
            var mapping = info.mapping();
            var keyDown = info.keyDown();
            var keyUp = info.keyUp();

            AtomicBoolean prevent = new AtomicBoolean(false);
            EventFlow flow = () -> prevent.set(true);

            if (mapping.isDown()) {
                if (!pressing.contains(key)) {
                    writer.write(new KeyDown(key));
                    var size = keyDown.size();
                    if (size > 0) {
                        for (int i = size - 1; i >= 0 && !prevent.get(); i--) {
                            keyDown.get(i).resolve(writer, flow);
                        }
                    }
                }
                pressing.add(key);
            } else {
                if (pressing.contains(key)) {
                    writer.write(new KeyUp(key));
                    var size = keyUp.size();
                    if (size > 0) {
                        for (int i = size - 1; i >= 0 && !prevent.get(); i--) {
                            keyUp.get(i).resolve(writer, flow);
                        }
                    }
                }
                pressing.remove(key);
            }
        }
    }

    public static void registerKeyboardEvent(
            int key, KeyMapping mapping,
            EventResolver keyDown,
            EventResolver keyUp
    ) {
        KeyboardEventInfo info;
        if ((info = infos.get(key)) == null) {
            info = new KeyboardEventInfo(
                mapping,
                new Vector<>(),
                new Vector<>()
            );

            infos.put(key, info);
        }

        if (keyDown != null)
            info.keyDown.add(keyDown);

        if (keyUp != null)
            info.keyUp.add(keyUp);
    }

    public static void registerKeyboardEvent(
            int key, KeyMapping mapping,
            EventResolver keyDown
    ) {
        registerKeyboardEvent(key, mapping, keyDown, null);
    }

    public static void registerKeyboardEvent(
            KeyMapping mapping,
            EventResolver keyDown
    ) {
        registerKeyboardEvent(mapping.getDefaultKey().getValue(), mapping, keyDown, null);
    }

    public static void registerKeyboardEvent(
            KeyMapping mapping,
            EventResolver keyDown,
            EventResolver keyUp
    ) {
        registerKeyboardEvent(mapping.getDefaultKey().getValue(), mapping, keyDown, keyUp);
    }

    record KeyboardEventInfo(
            KeyMapping mapping,
            List<EventResolver> keyDown,
            List<EventResolver> keyUp
    ) { }

    public interface EventFlow {
        void prevent();
    }

    public interface EventResolver {
        void resolve(EventWriter writer, EventFlow flow);
    }

    @System(runtimeLabel = InternalRuntime.CLIENT_READY)
    void setupNativeKeyOverride(
            @Reflect(Minecraft.class) Minecraft client
    ) {
        registerKeyboardEvent(client.options.keyDrop,
                (writer, flow) -> {
                    TargetLock lock = (TargetLock) KenseiClient.clientApp.getComponent(
                            client.player.getId(),
                            TargetLock.class
                    );
                    if (lock.targetId != 0) {
                        flow.prevent();
                    }
                }
        );
    }
}
