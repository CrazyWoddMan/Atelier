package crazywoddman.atelier.api.interfaces;

import java.util.function.Supplier;

import crazywoddman.atelier.accessories.SimpleWearableRenderer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraft.resources.ResourceLocation;

public interface IWearableAccessory extends IWearable {
    /**
     * @return AccessoryRenderer for the item's model. Implementation of {@link #getTextureLocation()} and {@link #getOverlayLocation()} doesn't matter if this method is overridden
     */
    default Supplier<AccessoryRenderer> getRenderer() {
        return () -> new SimpleWearableRenderer(
            getLayerLocation(), 
            getTextureLocation(),
            getOverlayLocation()
        );
    }

    /**
     * @return {@link ResourceLocation} of the main texture of the model (the one that should be able to change color if {@link IDyable} is implemented)
     */
    ResourceLocation getTextureLocation();

    /**
     * @return ResourceLocation of the overlay texture that doesn't affected by dye. Should be null if {@link IDyable} isn't implemented
     */
    default ResourceLocation getOverlayLocation() {
        return null;
    };
}