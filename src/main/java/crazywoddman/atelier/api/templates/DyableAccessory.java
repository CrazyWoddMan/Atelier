package crazywoddman.atelier.api.templates;

import java.util.List;

import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public abstract class DyableAccessory extends Item implements IWearableAccessory, IDyeable {
    private final int[] defaultColors;

    public DyableAccessory(Properties properties, int... defaultColors) {
        super(properties);
        this.defaultColors = defaultColors;
    }

    public DyableAccessory(int... defaultColors) {
        this(new Properties().stacksTo(1), defaultColors);
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