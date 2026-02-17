package crazywoddman.atelier.api.interfaces;

import org.joml.Vector3f;

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

    default Vector3f getRGBcolor(ItemStack stack) {
        return getFloatRGB(getColor(stack));
    }
    
    static Vector3f getFloatRGB(int color) {
        return new Vector3f(
            ((color >> 16) & 0xFF) / 255F, // red
            ((color >> 8) & 0xFF) / 255F,  // green
            (color & 0xFF) / 255F          // blue
        );
    }
}