package crazywoddman.atelier.items.templates;

import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.items.AtelierItems;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

// TODO: compress to Bandage
public interface IFabricAccessory extends IWearableAccessory {
    
    @Override
    default boolean canUnequip(ItemStack stack, SlotReference reference) {
        boolean canUnequip = IWearableAccessory.super.canUnequip(stack, reference);

        if (!reference.entity().level().isClientSide && canUnequip) {
            ItemStack item = new ItemStack(AtelierItems.FABRIC.get());
            CompoundTag tag = stack.getTag();

            if (tag != null)
                item.setTag(tag);

            reference.setStack(item);
        }
        
        return canUnequip;
    }

    @Override
    default void onUnequip(ItemStack stack, SlotReference reference) {
        IWearableAccessory.super.onUnequip(stack, reference);
        LivingEntity entity = reference.entity();

        if (!entity.level().isClientSide && !reference.getStack().isEmpty() && entity instanceof Player player && player.containerMenu.getCarried() == stack) {
            ItemStack item = new ItemStack(AtelierItems.FABRIC.get());
            CompoundTag tag = stack.getTag();

            if (tag != null)
                item.setTag(tag);

            player.containerMenu.setCarried(item);
        }
    }
}