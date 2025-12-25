package crazywoddman.atelier.items;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.item.ArmorItem;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.templates.DyableArmor;
import crazywoddman.atelier.models.ArmorVestBModel;

import java.util.function.Supplier;

public class ArmorVestB extends DyableArmor {

    public ArmorVestB() {
        super(
            AtelierArmorMaterials.PHANTOM_SILK,
            ArmorItem.Type.CHESTPLATE,
            new Properties(),
            8618876,
            Atelier.MODID + ":textures/models/wearable/armor_vest_b.png"
        );
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return ArmorVestBModel::createLayer;
    };

    @Override
    public ModelLayerLocation getLayerLocation() {
        return ArmorVestBModel.LAYER_LOCATION;
    };
}