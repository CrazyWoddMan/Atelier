package crazywoddman.atelier.api.templates;

import crazywoddman.atelier.api.interfaces.IDyable;
import net.minecraft.resources.ResourceLocation;

public abstract class DyableAccessory extends AccessoryWearable implements IDyable {
    private final int defaultColor;

    public DyableAccessory(Properties properties, int defaultColor, ResourceLocation texture, ResourceLocation overlay) {
        super(properties, texture, overlay);
        this.defaultColor = defaultColor;
    }

    public DyableAccessory(Properties properties, int defaultColor, ResourceLocation texture) {
        super(properties, texture);
        this.defaultColor = defaultColor;
    }

    @Override
    public int getDefaultColor() {
        return defaultColor;
    }
}