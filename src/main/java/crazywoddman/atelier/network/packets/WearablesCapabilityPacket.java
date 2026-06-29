package crazywoddman.atelier.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.data.WearablesCapability.WearableState;
import crazywoddman.atelier.network.AbstractPacket;

public abstract class WearablesCapabilityPacket extends AbstractPacket {
    final WearableState state;
    final WearablesCapability capability;
    
    WearablesCapabilityPacket(WearableState state, WearablesCapability capability) {
        this.state = state;
        this.capability = capability;
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        this.state.toNetwork(this.capability, buf);
    }
}