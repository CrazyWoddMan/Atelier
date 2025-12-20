package crazywoddman.atelier.items;

import java.util.function.Supplier;

import crazywoddman.atelier.accessories.PouchRenderer;
import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.models.PouchModel;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BundleItem;

public class Pouch extends BundleItem implements IWearableAccessory, IDyable {
    private final int defaultColor;

    public Pouch(Properties properties, int defaultColor) {
        super(properties);
        this.defaultColor = defaultColor;
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return PouchRenderer::new;
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return PouchModel::createLayer;
    }

    @Override
    public ModelLayerLocation getLayerLocation() {
        return PouchModel.LAYER_LOCATION;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return null;
    }

    @Override
    public int getDefaultColor() {
        return defaultColor;
    }
}