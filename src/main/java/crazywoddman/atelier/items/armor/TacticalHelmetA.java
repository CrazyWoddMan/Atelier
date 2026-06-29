package crazywoddman.atelier.items.armor;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableArmor;
import crazywoddman.atelier.items.AtelierArmorMaterials;

import java.util.function.Supplier;

public class TacticalHelmetA extends DyableArmor {

    public TacticalHelmetA() {
        super(
            AtelierArmorMaterials.PHANTOM_CLOTH,
            ArmorItem.Type.HELMET,
            new Properties(),
            PHANTOM_CLOTH
        );
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		    head.addOrReplaceChild("helmet_r1", CubeListBuilder.create().texOffs(0, 24).addBox(-6.0F, -0.4F, -1.8F, 12.0F, 7.0F, 7.0F, new CubeDeformation(-0.8F)), PartPose.offsetAndRotation(0.0F, -5.4F, 0.65F, -0.0873F, 0.0F, 0.0F));
		    head.addOrReplaceChild("helmet_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -2.4F, -6.8F, 12.0F, 12.0F, 12.0F, new CubeDeformation(-1.0F)), PartPose.offsetAndRotation(0.0F, -7.7F, 0.85F, -0.0873F, 0.0F, 0.0F));
        });
    };
}