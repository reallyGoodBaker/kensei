package top.rgb39.common;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record AnimPacket(String anim) implements CustomPacketPayload {
    public static Type<AnimPacket> type = CustomPacketPayload.createType("kensei:anim");

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return type;
    }

    public static void registerC2S() {
        PayloadTypeRegistry.playC2S().register(
                AnimPacket.type,
                StreamCodec.of((ByteBuf buf, AnimPacket str) -> {
                    var bytes = str.anim().getBytes();
                    var len = bytes.length;
                    buf.writeInt((short) len);
                    buf.writeBytes(bytes);
                }, buf -> {
                    var len = buf.readInt();
                    var bytes = new byte[len];
                    buf.readBytes(bytes);

                    return new AnimPacket(new String(bytes));
                })
        );
    }
}