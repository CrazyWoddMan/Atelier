package crazywoddman.atelier.network;

import java.util.function.Function;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.network.packets.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int packetId = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    
    public static void register() {
        register(EquipmentUpdatePacket.class, EquipmentUpdatePacket::decode, NetworkDirection.PLAY_TO_CLIENT);
        register(SyncModulesPacket.class, SyncModulesPacket::decode, NetworkDirection.PLAY_TO_CLIENT);
        register(QuickAccessPacket.class, QuickAccessPacket::decode, NetworkDirection.PLAY_TO_SERVER);
        register(StackClickedPacket.class, StackClickedPacket::decode, NetworkDirection.PLAY_TO_SERVER);
        register(ContainerItemPacket.class, ContainerItemPacket::decode, NetworkDirection.PLAY_TO_SERVER);
        register(WearCapToServerPacket.class, WearCapToServerPacket::decode, NetworkDirection.PLAY_TO_SERVER);
        register(WearCapToClientPacket.class, WearCapToClientPacket::decode, NetworkDirection.PLAY_TO_CLIENT);
        register(BreathingSoundPacket.class, BreathingSoundPacket::decode, NetworkDirection.PLAY_TO_CLIENT);
        register(DetonationSoundPacket.class, DetonationSoundPacket::decode, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <T extends AbstractPacket> void register(Class<T> clazz, Function<FriendlyByteBuf, T> decoder, NetworkDirection direction) {
        CHANNEL.messageBuilder(clazz, packetId++, direction)
            .encoder(AbstractPacket::encode)
            .decoder(decoder)
            .consumerMainThread((packet, context) -> packet.handle(context))
            .add();
    }
}