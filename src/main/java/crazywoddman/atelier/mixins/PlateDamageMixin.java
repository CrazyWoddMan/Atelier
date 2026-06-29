package crazywoddman.atelier.mixins;

import org.spongepowered.asm.mixin.Mixin;

import crazywoddman.atelier.data.ArmorPlates;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

@Mixin(Item.class)
public abstract class PlateDamageMixin implements IForgeItem {

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ArmorPlates.get(stack.getItem()).flatMap(ArmorPlates.Plate::getDurability).orElse(IForgeItem.super.getMaxDamage(stack));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return IForgeItem.super.isDamageable(stack) || ArmorPlates.get(stack.getItem()).flatMap(ArmorPlates.Plate::getDurability).isPresent();
    }
}