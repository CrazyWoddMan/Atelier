package crazywoddman.atelier.api.interfaces;
import java.util.function.Supplier;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public interface IWearable {
    Supplier<LayerDefinition> createLayer();
    ModelLayerLocation getLayerLocation();
}