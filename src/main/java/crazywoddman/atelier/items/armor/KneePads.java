package crazywoddman.atelier.items.armor;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableArmor;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.items.AtelierArmorMaterials;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class KneePads extends DyableArmor {

    public KneePads() {
        super(AtelierArmorMaterials.BIOPLASTIC, Type.LEGGINGS, new Properties(), BIOPOLYMER, DEFAULT_LEATHER_COLOR);
    }

    @Override
    public WearableTexture getTexture() {
        return new WearableTexture(Atelier.MODID, "pad");
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition -> {
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 27).addBox(-2.0F, 2.0F, -2.9F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 24).addBox(-2.0F, 2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 27).addBox(-2.0F, 2.0F, -2.9F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		    .texOffs(0, 24).addBox(-2.0F, 2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        });
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_IRON;
    }
}