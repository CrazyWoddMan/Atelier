package crazywoddman.atelier.api.templates;

import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class AccessoryWearable extends Item implements IWearableAccessory {
    private final Supplier<LayerDefinition> layerSupplier;
    private final ModelLayerLocation layerLocation;
    private final ResourceLocation texture;
    private final ResourceLocation overlay;

    public AccessoryWearable(Properties properties, Supplier<LayerDefinition> layerSupplier, ModelLayerLocation layerLocation, ResourceLocation texture, ResourceLocation overlay) {
        super(properties);
        this.layerSupplier = layerSupplier;
        this.layerLocation = layerLocation;
        this.texture = texture;
        this.overlay = overlay;
    }

    public AccessoryWearable(Properties properties, Supplier<LayerDefinition> layerSupplier, ModelLayerLocation layerLocation, ResourceLocation texture) {
        super(properties);
        this.layerSupplier = layerSupplier;
        this.layerLocation = layerLocation;
        this.texture = texture;
        this.overlay = null;
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return this.layerSupplier;
    }

    @Override
    public ModelLayerLocation getLayerLocation() {
        return this.layerLocation;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return this.texture;
    }

    @Override
    public ResourceLocation getOverlayLocation() {
        return this.overlay;
    }
}