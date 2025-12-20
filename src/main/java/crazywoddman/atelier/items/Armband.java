package crazywoddman.atelier.items;

import java.util.function.Supplier;

import crazywoddman.atelier.accessories.ArmbandRenderer;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.models.ArmbandModel;
import io.wispforest.accessories.api.client.AccessoryRenderer;

public class Armband extends DyableAccessory {
    public Armband(Properties properties, int defaultColor) {
        super(properties, defaultColor, ArmbandModel::createLayer, ArmbandModel.LAYER_LOCATION, null);
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return () -> new ArmbandRenderer(getLayerLocation(), ArmbandModel.LEFT_TEXTURE, ArmbandModel.RIGHT_TEXTURE);
    };
}