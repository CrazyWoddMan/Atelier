package crazywoddman.atelier.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crazywoddman.atelier.api.interfaces.IContainerItem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Shadow
    protected Slot hoveredSlot;
    
    @Redirect(
        method = "renderTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
        )
    )
    private boolean redirectIsEmpty(ItemStack stack) {
        return stack.isEmpty() || this.hoveredSlot != null && IContainerItem.get(this.hoveredSlot.getItem().getItem()).isPresent();
    }
}
