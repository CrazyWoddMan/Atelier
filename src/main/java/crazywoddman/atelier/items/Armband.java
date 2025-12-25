package crazywoddman.atelier.items;

import java.util.function.Supplier;

import crazywoddman.atelier.accessories.ArmbandRenderer;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.models.ArmbandModel;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class Armband extends DyableAccessory {
    public Armband(Properties properties, int defaultColor) {
        super(properties, defaultColor, FMLEnvironment.dist.isClient() ? ArmbandModel::createLayer : null, FMLEnvironment.dist.isClient() ? ArmbandModel.LAYER_LOCATION : null, null);
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return () -> new ArmbandRenderer(getLayerLocation(), ArmbandModel.LEFT_TEXTURE, ArmbandModel.RIGHT_TEXTURE);
    };
}