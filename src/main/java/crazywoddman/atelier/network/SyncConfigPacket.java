package crazywoddman.atelier.network;

import crazywoddman.atelier.config.Config;
import crazywoddman.atelier.items.accessories.KneePads;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncConfigPacket {
    private final byte kneePadsProtection;
    
    public SyncConfigPacket(byte kneePadsProtection) {
        this.kneePadsProtection = kneePadsProtection;
    }
    
    public static SyncConfigPacket fromConfig() {
        return new SyncConfigPacket(
            (byte)Math.round(Config.SERVER.kneePadsProtection.get().floatValue() * 100)
        );
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.kneePadsProtection);
    }
    
    public static SyncConfigPacket decode(FriendlyByteBuf buf) {
        return new SyncConfigPacket(
            buf.readByte()
        );
    }
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> KneePads.protection = this.kneePadsProtection);
        ctx.get().setPacketHandled(true);
    }
}