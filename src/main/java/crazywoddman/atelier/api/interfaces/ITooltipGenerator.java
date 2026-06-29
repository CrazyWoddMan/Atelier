package crazywoddman.atelier.api.interfaces;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public interface ITooltipGenerator {
    default void generateTooltip(List<Component> tooltip, int lines) {
        String desc = ((Item)this).getDescriptionId() + ".desc";

        for (int i = 0; i < lines; i++)
            tooltip.add(Component.translatable(desc + i));
    }
}