package crazywoddman.atelier.items;

import java.util.Map;

import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.models.BeltModel;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class Belt extends DyableAccessory implements IModular {
    public Belt(Properties properties, int defaultColor) {
        super(
            properties,
            defaultColor,
            FMLEnvironment.dist.isClient() ? BeltModel::createLayer : null,
            FMLEnvironment.dist.isClient() ? BeltModel.LAYER_LOCATION : null,
            FMLEnvironment.dist.isClient() ? BeltModel.TEXTURE : null,
            FMLEnvironment.dist.isClient() ? BeltModel.OVERLAY : null
        );
    }

    @Override
    public Map<String, Integer> getModules() {
        return Map.of("belt_pouch", 2);
    }
}