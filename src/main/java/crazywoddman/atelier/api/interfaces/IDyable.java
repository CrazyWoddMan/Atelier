package crazywoddman.atelier.api.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

public interface IDyable extends DyeableLeatherItem {
    int getDefaultColor();

    @Override
    default int getColor(ItemStack stack) {
        CompoundTag compound = stack.getTagElement("display");
        return compound != null && compound.contains("color", 99) ? compound.getInt("color") : getDefaultColor();
    }
}