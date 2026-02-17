package crazywoddman.atelier.network;

import crazywoddman.atelier.Atelier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    protected static int packetId = 0;
    
    public static void register() {
        CHANNEL.messageBuilder(SyncModulesDataPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SyncModulesDataPacket::encode)
            .decoder(SyncModulesDataPacket::decode)
            .consumerMainThread(SyncModulesDataPacket::handle)
            .add();
        CHANNEL.messageBuilder(SyncConfigPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SyncConfigPacket::encode)
            .decoder(SyncConfigPacket::decode)
            .consumerMainThread(SyncConfigPacket::handle)
            .add();
    }
}