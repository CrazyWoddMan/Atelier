package crazywoddman.atelier.items;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.accessories.ArmbandRenderer;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.models.ArmbandModel;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

public class Armband extends DyableAccessory {
    public Armband() {
        super(
            new Properties().stacksTo(16),
            16777215,
            null
        );
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return ArmbandModel::createLayer;
    }

    @Override
    public ModelLayerLocation getLayerLocation() {
        return ArmbandModel.LAYER_LOCATION;
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return () -> new ArmbandRenderer(
            getLayerLocation(),
            ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/models/wearable/armband_left.png"),
            ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/models/wearable/armband_right.png")
        );
    };
}