package crazywoddman.atelier.items;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.item.ArmorItem;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.templates.DyableArmor;
import crazywoddman.atelier.items.AtelierItems.AtelierArmorMaterials;
import crazywoddman.atelier.models.ArmorVestCModel;

import java.util.function.Supplier;

public class ArmorVestC extends DyableArmor {

    public ArmorVestC() {
        super(
            AtelierArmorMaterials.PHANTOM_SILK, ArmorItem.Type.CHESTPLATE,
            new Properties(), 8618876,
            Atelier.MODID + ":textures/models/wearable/armor_vest_c.png"
        );
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return ArmorVestCModel::createLayer;
    };

    @Override
    public ModelLayerLocation getLayerLocation() {
        return ArmorVestCModel.LAYER_LOCATION;
    };
}