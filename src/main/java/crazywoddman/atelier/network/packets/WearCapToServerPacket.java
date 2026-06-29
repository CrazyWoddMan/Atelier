package crazywoddman.atelier.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import crazywoddman.atelier.client.LocalPlayerVars;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.data.WearablesCapability.WearableState;
import crazywoddman.atelier.network.NetworkHandler;

public class WearCapToServerPacket extends WearablesCapabilityPacket {
    
    private WearCapToServerPacket(WearableState state, WearablesCapability capability) {
        super(state, capability);
    }

    public static void send(WearableState state) {
        NetworkHandler.CHANNEL.sendToServer(new WearCapToServerPacket(state, LocalPlayerVars.wearables));
    }
    
    public static WearCapToServerPacket decode(FriendlyByteBuf buf) {
        WearableState state = WearableState.fromNetwork(buf);
        return new WearCapToServerPacket(state, WearablesCapability.fromNetwork(state, buf));
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        WearablesCapability.get(ctx.get().getSender()).ifPresent(cap -> {
            this.state.copy(this.capability, cap);
            WearCapToClientPacket.send(player, state, false);
        });
    }
}