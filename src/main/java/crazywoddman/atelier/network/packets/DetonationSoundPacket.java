package crazywoddman.atelier.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

import crazywoddman.atelier.client.ClientUtils;
import crazywoddman.atelier.network.AbstractPacket;
import crazywoddman.atelier.network.NetworkHandler;

public class DetonationSoundPacket extends AbstractPacket {
    private final int playerID;
    
    private DetonationSoundPacket(int playerID) {
        this.playerID = playerID;
    }

    public static void send(ServerPlayer holder) {
        NetworkHandler.CHANNEL.send(
            PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> holder),
            new DetonationSoundPacket(holder.getId())
        );
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.playerID);
    }
    
    public static DetonationSoundPacket decode(FriendlyByteBuf buf) {
        return new DetonationSoundPacket(buf.readVarInt());
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (ClientUtils.getMC().level.getEntity(this.playerID) instanceof Player player)
            ClientUtils.playDetonationSound(player);
    }
}