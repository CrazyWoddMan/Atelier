package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.items.AtelierItems;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class Fabric extends DyableAccessory {

    public Fabric() {
        super(new Properties(), 16777215);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return null;
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return null;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        super.onEquip(stack, reference);
        ItemStack item = new ItemStack((reference.slotName().equals("face") ? AtelierItems.BANDANA : AtelierItems.BANDAGE).get());
        CompoundTag tag = stack.getTag();

        if (tag != null)
            item.setTag(tag);

        reference.setStack(item);
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        return false;
    }

    @Override
    public SoundEventData getEquipSound() {
        return null;
    }
}