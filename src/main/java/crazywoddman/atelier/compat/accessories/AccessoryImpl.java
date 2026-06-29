package crazywoddman.atelier.compat.accessories;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.item.ItemStack;

class AccessoryImpl implements Accessory {
    private final IWearableAccessory accessory;

    AccessoryImpl(IWearableAccessory accessory) {
        this.accessory = accessory;
    }

    @Override
    public int maxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public void onEquipFromUse(ItemStack stack, SlotReference reference) {}

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        this.accessory.onEquip(stack, reference.entity(), SimpleSlot.of(reference.slotName(), reference.slot()));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        this.accessory.onUnequip(stack, reference.entity(), SimpleSlot.of(reference.slotName(), reference.slot()));
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        this.accessory.wearTick(stack, reference.entity(), SimpleSlot.of(reference.slotName(), reference.slot()));
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        return this.accessory.canEquipFromUse(stack);
    }
}
