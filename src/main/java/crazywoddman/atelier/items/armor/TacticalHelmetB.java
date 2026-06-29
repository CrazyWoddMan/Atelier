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

public class TacticalHelmetB extends DyableArmor {

    public TacticalHelmetB() {
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
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
            head.addOrReplaceChild("helmet2_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -1.4F, -5.8F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.5F, -0.0873F, 0.0F, 0.0F));
            head.addOrReplaceChild("helmet2_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -2.4F, -5.8F, 10.0F, 6.0F, 10.0F), PartPose.offsetAndRotation(0.0F, -7.0F, 0.6F, -0.0873F, 0.0F, 0.0F));
        });
    };
}