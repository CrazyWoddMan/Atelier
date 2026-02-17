package crazywoddman.atelier.api.interfaces;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public interface IWearable {
    Supplier<LayerDefinition> createLayer();

    default ResourceLocation getTextureKey() {
        return ForgeRegistries.ITEMS.getKey((Item)this);
    }

    default ResourceLocation getModelKey() {
        return getTextureKey();
    }

    static Optional<ResourceLocation> getOverlayTexture(ResourceLocation key) {
        ResourceLocation path = IWearable.getModelTexture(IWearable.getModelTexturePath(key).toString() + "_overlay");
        return Minecraft.getInstance().getResourceManager().getResource(path).isPresent()
            ? Optional.of(path)
            : Optional.empty();
    }

    private static ResourceLocation getModelTexturePath(ResourceLocation key) {
        return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "textures/wearable/" + key.getPath());
    }

    private static ResourceLocation getModelTexture(String path) {
        return ResourceLocation.tryParse(path + ".png");
    }

    static ResourceLocation getModelTexture(ResourceLocation key) {
        return getModelTexture(getModelTexturePath(key).toString());
    }
}