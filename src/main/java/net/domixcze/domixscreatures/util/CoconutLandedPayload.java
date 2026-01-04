package net.domixcze.domixscreatures.util;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record CoconutLandedPayload(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<CoconutLandedPayload> ID =
            new CustomPayload.Id<>(Identifier.of("domixs_creatures", "coconut_landed"));

    public static final PacketCodec<RegistryByteBuf, CoconutLandedPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, CoconutLandedPayload::pos,
                    CoconutLandedPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}