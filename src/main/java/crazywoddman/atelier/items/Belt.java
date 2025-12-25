package crazywoddman.atelier.items;

import java.util.Map;
import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.models.BeltModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

public class Belt extends DyableAccessory implements IModular {
    public Belt() {
        super(
            new Properties().stacksTo(4),
            8606770,
            ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/models/wearable/belt.png"),
            ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/models/wearable/belt_overlay.png")
        );
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return BeltModel::createLayer;
    }

    @Override
    public ModelLayerLocation getLayerLocation() {
        return BeltModel.LAYER_LOCATION;
    }

    @Override
    public Map<String, Integer> getModules() {
        return Map.of("belt_pouch", 2);
    }
}