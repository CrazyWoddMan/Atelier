package crazywoddman.atelier.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Iterator;
import java.util.function.Supplier;

import crazywoddman.atelier.data.QuickAccessSlot;
import crazywoddman.atelier.network.AbstractPacket;
import crazywoddman.atelier.network.NetworkHandler;

public class QuickAccessPacket extends AbstractPacket {
    private final QuickAccessSlot slot;
    
    private QuickAccessPacket(QuickAccessSlot slot) {
        this.slot = slot;
    }

    public static void send(int index) {
        Iterator<QuickAccessSlot> it = QuickAccessSlot.CACHE.iterator();

        for (int i = 0; i < index; i++)
            it.next();

        NetworkHandler.CHANNEL.sendToServer(new QuickAccessPacket(it.next()));
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        this.slot.toNetwork(buf);
    }
    
    public static QuickAccessPacket decode(FriendlyByteBuf buf) {
        return new QuickAccessPacket(QuickAccessSlot.fromNetwork(buf));
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player == null)
            return;

        this.slot.use(player);
    }
}