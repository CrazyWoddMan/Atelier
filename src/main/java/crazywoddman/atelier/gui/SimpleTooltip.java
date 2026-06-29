package crazywoddman.atelier.gui;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class SimpleTooltip implements TooltipComponent {
    public final ItemStack stack;

    public SimpleTooltip(ItemStack stack) {
        this.stack = stack;
    }
}