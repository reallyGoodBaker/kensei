package top.rgb39.kensei_client.system;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import top.rgb39.ecs.annotation.System;
import top.rgb39.ecs.annotation.Write;
import top.rgb39.ecs.executor.RuntimeLabel;
import top.rgb39.ecs.plugin.EventWriter;
import top.rgb39.kensei_client.events.KeyDown;
import top.rgb39.kensei_client.events.KeyUp;
import top.rgb39.kensei_client.events.LockEvent;

public class KeyboardSystems {

    final static String CATEGORY = "category.kensei.keybinding";
    final static String LOCK = "key.kensei.lock";

    private KeyMapping lock;

    private boolean lockPressing = false;

    @System(runtimeLabel = RuntimeLabel.Startup)
    void setupKeybindings() {
        lock = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                LOCK,
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_G,
                CATEGORY
        ));
    }

    @System(runtimeLabel = RuntimeLabel.Event)
    void fireEvents(
            @Write EventWriter writer
    ) {
        if (lock.isDown()) {
            if (!lockPressing) {
                writer.write(new KeyDown(InputConstants.KEY_G));
                writer.write(new LockEvent());
            }
            lockPressing = true;
        } else {
            if (lockPressing) {
                writer.write(new KeyUp(InputConstants.KEY_G));
            }
            lockPressing = false;
        }
    }

}
