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

public class ArmorVestB extends DyableArmor {
    
    public ArmorVestB() {
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
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, -0.25F, -2.5F, 8.0F, 10.0F, 5.0F, new CubeDeformation(0.5F, 0.5F, 0))
		    .texOffs(0, 9).addBox(-4.5F, 5.75F, -2.5F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.ZERO)
        );
    };
}