package crazywoddman.atelier.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.client.ClientUtils;
import crazywoddman.atelier.client.LocalPlayerVars;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.data.QuickAccessSlot;
import crazywoddman.atelier.items.templates.FilterItem;
import crazywoddman.atelier.network.AbstractPacket;
import crazywoddman.atelier.network.NetworkHandler;


public class EquipmentUpdatePacket extends AbstractPacket {
    private final SimpleSlot slot;

    private EquipmentUpdatePacket(SimpleSlot slot) {
        this.slot = slot;
    }

    public static void send(ServerPlayer player, SimpleSlot slot) {
        NetworkHandler.CHANNEL.sendTo(
            new EquipmentUpdatePacket(slot),
            player.connection.connection,
            NetworkDirection.PLAY_TO_CLIENT
        );
    }
    
    @Override
    public void encode(FriendlyByteBuf buf) {
        this.slot.toNetwork(buf);
    }
    
    public static EquipmentUpdatePacket decode(FriendlyByteBuf buf) {
        return new EquipmentUpdatePacket(SimpleSlot.fromNetwork(buf));
    }
    
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (this.slot.name.equals("face")) {
            CompatHelper.getSlotContainer(ClientUtils.getLocalPlayer(), this.slot.name).ifPresent(face -> {
                for (byte i = 0; i < face.getContainerSize(); i++) {
                    ItemStack stack = face.getItem(i);

                    if (stack.is(AtelierTags.Items.TINTED_VISION)) {
                        LocalPlayerVars.tintedVision = true;
                        LocalPlayerVars.faceItemSlot = i;
                        FilterItem.equipped = stack.is(AtelierTags.Items.GASMASKS) && IModular.getModule(stack, IModular.GAS_FILTER).isPresent();
                        return;
                    }
                }

                LocalPlayerVars.tintedVision = false;
                LocalPlayerVars.faceItemSlot = 0;
                FilterItem.equipped = false;
            });
        }

        QuickAccessSlot.check(slot);
    }
}