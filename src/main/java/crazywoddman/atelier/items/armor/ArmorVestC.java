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

public class ArmorVestC extends DyableArmor {

    public ArmorVestC() {
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
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 43).mirror().addBox(-4.75F, 5.0F, -3.45F, 3.0F, 3.0F, 1.0F).mirror(false)
            .texOffs(0, 39).addBox(-4.5F, 5.25F, -2.3F, 9.0F, 4.0F, 5.0F, new CubeDeformation(0.5F))
            .texOffs(0, 48).addBox(-4.0F, -0.25F, -2.5F, 8.0F, 10.0F, 5.0F, new CubeDeformation(0.5F))
            .texOffs(26, 55).addBox(-0.7F, 1.0F, -3.25F, 2.0F, 8.0F, 1.0F)
            .texOffs(29, 51).addBox(1.75F, 5.0F, -3.45F, 3.0F, 3.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 0.0F))
            .addOrReplaceChild("vestC_r1", CubeListBuilder.create().texOffs(28, 47).addBox(-1.0F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F)
            .texOffs(28, 42).mirror().addBox(-7.5F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F).mirror(false), PartPose.offsetAndRotation(2.75F, 5.0F, -3.45F, -0.2618F, 0.0F, 0.0F));

            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(43, 54).addBox(-3.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(43, 44).addBox(-1.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
            });
    };
}