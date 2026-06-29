package crazywoddman.atelier.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.data.WearablesCapability.WearableState;
import crazywoddman.atelier.network.NetworkHandler;

public class WearCapToClientPacket extends WearablesCapabilityPacket {
    private final int playerID;
    
    private WearCapToClientPacket(int playerID, WearableState state, WearablesCapability capability) {
        super(state, capability);
        this.playerID = playerID;
    }

    public static void send(ServerPlayer to, ServerPlayer capHolder, WearableState state) {
        capHolder.getCapability(WearablesCapability.WEARABLES_CAPABILITY).ifPresent(cap -> 
            NetworkHandler.CHANNEL.sendTo(
                new WearCapToClientPacket(capHolder.getId(), state, cap),
                to.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            )
        );
    }

    public static void send(ServerPlayer capHolder, WearableState state, boolean sendToHolder) {
        capHolder.getCapability(WearablesCapability.WEARABLES_CAPABILITY).ifPresent(cap ->
            NetworkHandler.CHANNEL.send(
                (sendToHolder ? PacketDistributor.TRACKING_ENTITY_AND_SELF : PacketDistributor.TRACKING_ENTITY).with(() -> capHolder),
                new WearCapToClientPacket(capHolder.getId(), state, cap)
            )
        );
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.playerID);
        super.encode(buf);
    }
    
    public static WearCapToClientPacket decode(FriendlyByteBuf buf) {
        int playerID = buf.readVarInt();
        WearableState state = WearableState.fromNetwork(buf);
        return new WearCapToClientPacket(playerID, state, WearablesCapability.fromNetwork(state, buf));
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (Minecraft.getInstance().level.getEntity(this.playerID) instanceof Player player) {
            player.getCapability(WearablesCapability.WEARABLES_CAPABILITY).ifPresent(cap ->
                this.state.copy(this.capability, cap)
            );
        }
    }
}