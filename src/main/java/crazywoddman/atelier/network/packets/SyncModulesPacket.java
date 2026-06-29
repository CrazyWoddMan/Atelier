package crazywoddman.atelier.network.packets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.network.AbstractPacket;
import crazywoddman.atelier.network.NetworkHandler;
import net.minecraft.world.item.Item;

public class SyncModulesPacket extends AbstractPacket {
    private final Map<Item, List<String>> modules;

    private SyncModulesPacket(Map<Item, List<String>> modules) {
        this.modules = modules;
    }

    public static void send(Connection connection) {
        NetworkHandler.CHANNEL.sendTo(
            new SyncModulesPacket(IModular.CACHE),
            connection,
            NetworkDirection.PLAY_TO_CLIENT
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.modules.size());
        
        this.modules.forEach((item, modules) -> {
            buf.writeId(BuiltInRegistries.ITEM, item);
            buf.writeByte(modules.size());
            
            for (String module : modules)
                buf.writeUtf(module);
        });
    }

    @SuppressWarnings("deprecation")
    public static SyncModulesPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Map<Item, List<String>> map = new HashMap<>();
        
        for (int i = 0; i < size; i++) {
            Item item = buf.readById(BuiltInRegistries.ITEM);
            List<String> modules = new ArrayList<>();
            int count = buf.readByte();
            
            for (int m = 0; m < count; m++)
                modules.add(buf.readUtf());
            
            map.put(item, modules);
        }
        
        return new SyncModulesPacket(map);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        IModular.CACHE.clear();
        IModular.CACHE.putAll(this.modules);
    }
}