package crazywoddman.atelier.api.templates;

import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import net.minecraft.world.item.Item;

public abstract class DyableAccessory extends Item implements IWearableAccessory, IDyable {
    private final int defaultColor;

    public DyableAccessory(Properties properties, int defaultColor) {
        super(properties);
        this.defaultColor = defaultColor;
    }

    @Override
    public int getDefaultColor() {
        return defaultColor;
    }
}