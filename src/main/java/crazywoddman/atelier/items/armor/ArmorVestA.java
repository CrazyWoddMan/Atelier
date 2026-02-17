package crazywoddman.atelier.items.armor;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.item.ArmorItem;
import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableArmor;
import crazywoddman.atelier.items.AtelierArmorMaterials;

import java.util.function.Supplier;

public class ArmorVestA extends DyableArmor {
    
    public ArmorVestA() {
        super(
            AtelierArmorMaterials.PHANTOM_SILK,
            ArmorItem.Type.CHESTPLATE,
            new Properties(),
            0x768282
        );
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(64, 64, partdefinition -> {
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 56).addBox(-4.0F, 2.75F, -2.95F, 8.0F, 7.0F, 1.0F)
		    .texOffs(0, 50).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.5F))
		    .texOffs(0, 41).addBox(-4.5F, 5.75F, -2.5F, 9.0F, 4.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        });
    };
}