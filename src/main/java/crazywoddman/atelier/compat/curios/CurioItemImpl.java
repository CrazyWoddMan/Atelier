package crazywoddman.atelier.compat.curios;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CurioItemImpl implements ICurioItem {
    private final IWearableAccessory accessory;

    CurioItemImpl(IWearableAccessory accessory) {
        this.accessory = accessory;
    }

    @Override
    public void playRightClickEquipSound(LivingEntity entity, ItemStack stack) {}

    @Override
    public void onEquip(String slot, int index, LivingEntity entity, ItemStack stack) {
        this.accessory.onEquip(stack, entity, SimpleSlot.of(slot, index));
    }

    @Override
    public void onUnequip(String slot, int index, LivingEntity entity, ItemStack stack) {
        this.accessory.onUnequip(stack, entity, SimpleSlot.of(slot, index));
    }

    @Override
    public void curioTick(String slot, int index, LivingEntity entity, ItemStack stack) {
        this.accessory.wearTick(stack, entity, SimpleSlot.of(slot, index));
    }

    @Override
    public boolean canRightClickEquip(ItemStack stack) {
        return this.accessory.canEquipFromUse(stack);
    }
}
