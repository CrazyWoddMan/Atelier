package crazywoddman.atelier.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

import crazywoddman.atelier.api.ModulesDataProvider;

public class SyncModulesDataPacket {
    private final int entityId;
    private final CompoundTag data;

    public SyncModulesDataPacket(int entityId, CompoundTag data) {
        this.entityId = entityId;
        this.data = data;
    }

    public static void encode(SyncModulesDataPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.entityId);
        buffer.writeNbt(packet.data);
    }

    public static SyncModulesDataPacket decode(FriendlyByteBuf buffer) {
        return new SyncModulesDataPacket(buffer.readVarInt(), buffer.readNbt());
    }

    public static void handle(SyncModulesDataPacket packet, Supplier<Context> context) {
        context.get().enqueueWork(() ->
            ModulesDataProvider.get((LivingEntity)Minecraft.getInstance().level.getEntity(packet.entityId)).deserializeNBT(packet.data)
        );

        context.get().setPacketHandled(true);
    }
}