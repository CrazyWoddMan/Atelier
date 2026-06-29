package crazywoddman.atelier.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import crazywoddman.atelier.api.SlotAccessHelper;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.gui.ClientContainerItemTooltip;
import crazywoddman.atelier.network.AbstractPacket;
import crazywoddman.atelier.network.NetworkHandler;

public class ContainerItemPacket extends AbstractPacket {
    private final int slot, index;
    
    private ContainerItemPacket(int slot, int index) {
        this.slot = slot;
        this.index = index;
    }

    public static void send(Slot slot) {
        NetworkHandler.CHANNEL.sendToServer(new ContainerItemPacket(slot.index, ClientContainerItemTooltip.chosen));
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.slot);
        buf.writeByte(this.index);
    }
    
    public static ContainerItemPacket decode(FriendlyByteBuf buf) {
        return new ContainerItemPacket(
            buf.readVarInt(),
            buf.readByte()
        );
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player == null || player.containerMenu == null)
            return;

        
        Slot slot;

        try {
            slot = player.containerMenu.getSlot(this.slot);
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        ItemStack stack = slot.getItem();
        SlotAccess carried = SlotAccessHelper.make(player.containerMenu::getCarried, player.containerMenu::setCarried);
        IContainerItem.insert(stack, carried.get(), carried, index);
    }
}