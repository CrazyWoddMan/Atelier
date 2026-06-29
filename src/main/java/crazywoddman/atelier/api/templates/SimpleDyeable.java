package crazywoddman.atelier.api.templates;

import java.util.List;

import crazywoddman.atelier.api.interfaces.IDyeable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SimpleDyeable extends Item implements IDyeable {
    private final int[] defaultColors;

    public SimpleDyeable(Properties properties, int... defaultColors) {
        super(properties);
        this.defaultColors = defaultColors;
    }

    public SimpleDyeable(int... defaultColors) {
        this(new Properties(), defaultColors);
    }

    @Override
    public int[] getDefaultColors() {
        return this.defaultColors;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        showColorTooltip(stack, tooltip, flag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return caludronClean(context);
    }
}