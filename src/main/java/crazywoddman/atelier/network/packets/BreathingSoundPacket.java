package crazywoddman.atelier.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

import crazywoddman.atelier.client.ClientUtils;
import crazywoddman.atelier.network.AbstractPacket;
import crazywoddman.atelier.network.NetworkHandler;

public class BreathingSoundPacket extends AbstractPacket {
    private final int playerID, slot;
    
    private BreathingSoundPacket(int playerID, int slot) {
        this.playerID = playerID;
        this.slot = slot;
    }

    public static void send(ServerPlayer holder, int gasmaskSlot) {
        NetworkHandler.CHANNEL.send(
            PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> holder),
            new BreathingSoundPacket(holder.getId(), gasmaskSlot)
        );
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.playerID);
        buf.writeByte(this.slot);
    }
    
    public static BreathingSoundPacket decode(FriendlyByteBuf buf) {
        return new BreathingSoundPacket(buf.readVarInt(), buf.readByte());
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ClientUtils.playBreathingSound(this.playerID, this.slot);
    }
}