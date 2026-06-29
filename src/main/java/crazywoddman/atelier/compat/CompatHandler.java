package crazywoddman.atelier.compat;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.data.AtelierSounds;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.data.WearablesCapability.WearableState;
import crazywoddman.atelier.items.templates.AbstractGasMask;
import crazywoddman.atelier.network.packets.EquipmentUpdatePacket;
import crazywoddman.atelier.network.packets.WearCapToClientPacket;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class CompatHandler {

    public abstract void registerRenderer(Item item, Supplier<IAccessoryRenderer> renderer);

    public abstract Optional<Container> getSlotContainer(LivingEntity entity, String slot);

    public abstract boolean shouldRender(LivingEntity entity, SimpleSlot slot);

    public abstract Pair<SimpleSlot, ItemStack>[] findEquipped(LivingEntity entity, Predicate<ItemStack> filter);

    public abstract void breakEvent(LivingEntity entity, SimpleSlot slot);

    public abstract void onCommonSetup();

    public abstract void registerItem(Item item);
    
    public abstract void registerEvents();

    protected static boolean canEquipWearable(ItemStack stack, SimpleSlot slot, LivingEntity entity) {
        if (slot.name.equals(IWearableAccessory.FACE)) {
            Container container = CompatHelper.getSlotContainer(entity, slot.name).get();

            for (int i = 0; i < container.getContainerSize(); i++) {
                if (i == slot.index)
                    continue;
            
                ItemStack equipped = container.getItem(i);
                
                if (equipped.isEmpty())
                    continue;

                ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
                
                if (!equipped.is(AtelierTags.Items.create(key.getNamespace(), "compatible/" + key.getPath())))
                    return false;
            }
        }

        return true;
    }

    protected static void onWearableChange(ItemStack prevStack, ItemStack newStack, SimpleSlot slot, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player))
            return;

        switch (slot.name) {
            case "hat", "head" -> {
                if (prevStack.is(AtelierTags.Items.NVD) && (!newStack.is(AtelierTags.Items.NVD) || IContainerItem.getFullness(newStack) == 0)) {
                    WearablesCapability.get(player).ifPresent(cap -> {
                        if (cap.nvdActive) {
                            cap.nvdActive = false;
                            WearCapToClientPacket.send(player, WearableState.NVD, true);
                            AtelierSounds.play(AtelierSounds.NVD_OFF, player, null);
                        }
                    });
                }
            }
            case IWearableAccessory.FACE -> AbstractGasMask.onChange(newStack, player);
        }

        EquipmentUpdatePacket.send(player, slot);
    }
}
