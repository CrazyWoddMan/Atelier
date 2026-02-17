package crazywoddman.atelier.api.templates;

import java.util.List;

import crazywoddman.atelier.api.interfaces.ITooltipGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class SimpleItem extends Item implements ITooltipGenerator {
    private final int tooltipLines;

    public SimpleItem(Properties properties, int tooltipLines) {
        super(new Properties());
        this.tooltipLines = tooltipLines;
    }

    public SimpleItem(Properties properties) {
        this(properties, 0);
    }

    public SimpleItem(int tooltipLines) {
        this(new Properties(), tooltipLines);
    }

    public SimpleItem() {
        this(0);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (tooltipLines != 0)
            tooltip.addAll(generateTooltip(tooltipLines));
    }
}