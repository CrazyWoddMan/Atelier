package crazywoddman.atelier.api.templates;

import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IDyable;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

public class DyableAccessory extends AccessoryWearable implements IDyable {
    private final int defaultColor;

    public DyableAccessory(Properties properties, int defaultColor, Supplier<LayerDefinition> layerSupplier, ModelLayerLocation layerLocation, ResourceLocation texture, ResourceLocation overlay) {
        super(properties, layerSupplier, layerLocation, texture, overlay);
        this.defaultColor = defaultColor;
    }

    public DyableAccessory(Properties properties, int defaultColor, Supplier<LayerDefinition> layerSupplier, ModelLayerLocation layerLocation, ResourceLocation texture) {
        super(properties, layerSupplier, layerLocation, texture);
        this.defaultColor = defaultColor;
    }

    @Override
    public int getDefaultColor() {
        return defaultColor;
    }
}