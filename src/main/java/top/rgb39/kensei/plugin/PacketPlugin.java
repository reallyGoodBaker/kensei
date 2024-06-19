package top.rgb39.kensei.plugin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;
import top.rgb39.common.AnimPacket;
import top.rgb39.ecs.arch.App;
import top.rgb39.ecs.plugin.Plugin;
import top.rgb39.ecs.util.Logger;

public class PacketPlugin implements Plugin {

    @Override
    public void build(App app) {
        AnimPacket.registerC2S();
        ServerPlayNetworking.registerGlobalReceiver(
                AnimPacket.type,
                (payload, context) -> {
                    CpmPlugin.api.playAnimation(Player.class, context.player(), payload.anim(), -1);
                }
        );
    }
}
