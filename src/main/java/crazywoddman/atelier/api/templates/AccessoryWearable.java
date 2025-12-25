package crazywoddman.atelier.api.templates;

import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public abstract class AccessoryWearable extends Item implements IWearableAccessory {
    private final ResourceLocation texture;
    private final ResourceLocation overlay;

    public AccessoryWearable(Properties properties, ResourceLocation texture, ResourceLocation overlay) {
        super(properties);
        this.texture = texture;
        this.overlay = overlay;
    }

    public AccessoryWearable(Properties properties, ResourceLocation texture) {
        super(properties);
        this.texture = texture;
        this.overlay = null;
    }

    @Override
    public abstract Supplier<LayerDefinition> createLayer();

    @Override
    public abstract ModelLayerLocation getLayerLocation();

    @Override
    public ResourceLocation getTextureLocation() {
        return this.texture;
    }

    @Override
    public ResourceLocation getOverlayLocation() {
        return this.overlay;
    }
}