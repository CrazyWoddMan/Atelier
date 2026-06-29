package crazywoddman.atelier.mixins;

import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.gui.ModuleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    
    @Inject(
        method = "getTooltipImage",
        at = @At("HEAD"),
        cancellable = true
    )
    private void redirectTooltipImage(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        if (IModular.isModular((Item)(Object)this))
            cir.setReturnValue(Optional.of(new ModuleTooltip(stack)));
    }
}