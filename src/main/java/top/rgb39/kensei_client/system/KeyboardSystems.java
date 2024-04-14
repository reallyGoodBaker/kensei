package top.rgb39.kensei_client.system;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import top.rgb39.ecs.annotation.System;
import top.rgb39.ecs.annotation.Write;
import top.rgb39.ecs.executor.RuntimeLabel;
import top.rgb39.ecs.plugin.EventWriter;
import top.rgb39.ecs.util.Logger;
import top.rgb39.ecs.util.Option;
import top.rgb39.kensei_client.events.KeyDown;
import top.rgb39.kensei_client.events.KeyUp;
import top.rgb39.kensei_client.events.LockEvent;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class KeyboardSystems {

    final static String CATEGORY = "category.kensei.keybinding";
    final static String LOCK = "key.kensei.lock";

    private static final Set<KeyboardEventInfo> infos = new CopyOnWriteArraySet<>();
    private final Set<Integer> pressing = new CopyOnWriteArraySet<>();

    @System(runtimeLabel = RuntimeLabel.Startup)
    void setupKeybindings() {
        var lock = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                LOCK,
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_G,
                CATEGORY
        ));

        registerKeyboardEvent(InputConstants.KEY_G, lock, writer -> writer.write(new LockEvent()), null);
    }

    @System(runtimeLabel = RuntimeLabel.Event)
    void fireEvents(
            @Write EventWriter writer
    ) {
        for (KeyboardEventInfo info : infos) {
            var mapping = info.mapping();
            var key = info.key();
            var keyDown = info.keyDown();
            var keyUp = info.keyUp();

            if (mapping.isDown()) {
                if (!pressing.contains(key)) {
                    writer.write(new KeyDown(key));
                    keyDown.resolve(writer);
                }
                pressing.add(key);
            } else {
                if (pressing.contains(key)) {
                    writer.write(new KeyUp(key));
                    keyUp.resolve(writer);
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
        infos.add(new KeyboardEventInfo(
                mapping,
                key,
                keyDown == null ? defaultEventResolver : keyDown,
                keyUp == null ? defaultEventResolver : keyUp
        ));
    }

    static final EventResolver defaultEventResolver = writer -> {};

    record KeyboardEventInfo(
            KeyMapping mapping,
            int key,
            EventResolver keyDown,
            EventResolver keyUp
    ) { }

    public interface EventResolver {
        void resolve(EventWriter writer);
    }

}
