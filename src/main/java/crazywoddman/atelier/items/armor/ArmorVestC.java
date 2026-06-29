package crazywoddman.atelier.items.armor;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.item.ArmorItem;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableArmor;
import crazywoddman.atelier.items.AtelierArmorMaterials;

import java.util.function.Supplier;

public class ArmorVestC extends DyableArmor {

    public ArmorVestC() {
        super(
            AtelierArmorMaterials.PHANTOM_CLOTH,
            ArmorItem.Type.CHESTPLATE,
            new Properties(),
            PHANTOM_CLOTH
        );
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition ->
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-4.5F, 5.25F, -2.3F, 9.0F, 4.0F, 5.0F, new CubeDeformation(0.5F))
            .texOffs(0, 17).addBox(-4.0F, -0.25F, -2.5F, 8.0F, 10.0F, 5.0F, new CubeDeformation(0.5F))
            .texOffs(26, 23).addBox(-0.7F, 1.0F, -3.25F, 2.0F, 8.0F, 1.0F), PartPose.ZERO)
        );
    };
}