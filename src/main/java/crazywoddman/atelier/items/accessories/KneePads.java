package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableArmor;
import crazywoddman.atelier.items.AtelierArmorMaterials;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class KneePads extends DyableArmor {

    public KneePads() {
        super(AtelierArmorMaterials.PHANTOM_SILK, Type.LEGGINGS, new Properties(), 8618876);
    }

    public static byte protection;

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(16, 16, partdefinition -> {
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(3, 11).addBox(-2.0F, 2.0F, -2.9F, 4.0F, 4.0F, 1.0F)
            .texOffs(0, 0).addBox(-2.0F, 2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(3, 11).addBox(-2.0F, 2.0F, -2.9F, 4.0F, 4.0F, 1.0F)
            .texOffs(0, 0).addBox(-2.0F, 2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        });
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_IRON;
    }
}