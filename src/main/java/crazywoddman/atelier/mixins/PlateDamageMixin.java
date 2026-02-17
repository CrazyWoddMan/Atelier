package crazywoddman.atelier.mixins;

import org.spongepowered.asm.mixin.Mixin;

import crazywoddman.atelier.recipes.AtelierRecipes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;

@Mixin(Item.class)
public abstract class PlateDamageMixin implements IForgeItem {

    @Override
    public int getMaxDamage(ItemStack stack) {
        return AtelierRecipes
            .getPlateDurability(stack.getItem())
            .orElse(IForgeItem.super.getMaxDamage(stack));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return IForgeItem.super.isDamageable(stack) || AtelierRecipes
            .getPlateDurability(stack.getItem())
            .map(durability -> durability > 0)
            .orElse(false);
    }
}