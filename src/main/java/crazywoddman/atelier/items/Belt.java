package crazywoddman.atelier.items;

import java.util.Map;

import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.models.BeltModel;

public class Belt extends DyableAccessory implements IModular {
    public Belt(Properties properties, int defaultColor) {
        super(
            properties,
            defaultColor,
            BeltModel::createLayer,
            BeltModel.LAYER_LOCATION,
            BeltModel.TEXTURE,
            BeltModel.OVERLAY
        );
    }

    @Override
    public Map<String, Integer> getModules() {
        return Map.of("belt_pouch", 2);
    }
}