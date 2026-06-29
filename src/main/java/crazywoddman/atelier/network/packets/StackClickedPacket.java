package crazywoddman.atelier.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.SlotAccessHelper;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.network.AbstractPacket;
import crazywoddman.atelier.network.NetworkHandler;

public class StackClickedPacket extends AbstractPacket {
    public static final int NO_PREVIEW = -1;
    private final int slot;
    private final SimpleSlot module;
    private final int preview;
    
    private StackClickedPacket(int slot, SimpleSlot module, int preview) {
        this.slot = slot;
        this.module = module;
        this.preview = preview;
    }

    public static void send(Slot slot, SimpleSlot module, int preview) {
        NetworkHandler.CHANNEL.sendToServer(new StackClickedPacket(slot.index, module, preview));
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.slot);
        this.module.toNetwork(buf);
        buf.writeByte(this.preview);
    }
    
    public static StackClickedPacket decode(FriendlyByteBuf buf) {
        return new StackClickedPacket(
            buf.readByte(),
            SimpleSlot.fromNetwork(buf),
            buf.readByte()
        );
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player.containerMenu == null)
            return;

        Slot slot = player.containerMenu.getSlot(this.slot);
        ItemStack stack = slot.getItem();
        ItemStack carried = player.containerMenu.getCarried();
        boolean carriedDevided = false;

        if (!carried.isEmpty() || this.preview > NO_PREVIEW) {
            ItemStack chosen = IModular.getStackInSlot(stack, this.module);
            Item carriedItem = carried.getItem();

            if (!chosen.isEmpty() && chosen.getItem() instanceof IContainerItem && !(carriedItem instanceof IContainerItem)) {
                Consumer<ItemStack> setter = s -> IModular.insert(stack, s, this.module);
                IContainerItem.insert(
                    chosen,
                    carried,
                    SlotAccessHelper.make(player.containerMenu::getCarried, player.containerMenu::setCarried),
                    Math.max(0, this.preview)
                );
                setter.accept(chosen);

                return;
            }

            if (carried.getCount() > 1) {
                if (!IModular.getStackInSlot(stack, this.module).isEmpty()) {
                    return;
                } else {
                    carried.shrink(1);
                    carriedDevided = true;
                }
            }
        }

        ItemStack output = IModular.insert(stack, carried.copyWithCount(1), this.module);

        if (!carriedDevided)
            player.containerMenu.setCarried(output);
    }
}