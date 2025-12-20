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
        @SuppressWarnings("deprecation")
        int damage = ((Item)(Object)this).getMaxDamage();
        
        return damage == 0 && AtelierRecipes.isPlate(stack.getItem())
        ? AtelierRecipes
            .getPlateRecipe(stack)
            .map(recipe ->
                recipe
                .getDurability()
                .orElse(0)
            )
            .orElse(0)
        : damage;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        boolean damageable = ((Item)(Object)this).canBeDepleted();

        return damageable || (
            AtelierRecipes.isPlate(stack.getItem())
            && AtelierRecipes
                .getPlateRecipe(stack)
                .map(recipe ->
                    recipe
                    .getDurability()
                    .map(durabillity -> durabillity > 0)
                    .orElse(false)
                )
                .orElse(false)
        );
    }
}