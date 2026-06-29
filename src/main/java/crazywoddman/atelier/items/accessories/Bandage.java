package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.client.renderers.BandageRenderer;
import crazywoddman.atelier.data.ServerUtils;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class Bandage extends DyableAccessory {

    public Bandage() {
        super(new Properties().stacksTo(Atelier.ACCESSORIES_LOADED ? 16 : 1), WHITE);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.ZERO);
            head.addOrReplaceChild("bandage_r1", CubeListBuilder.create().texOffs(0, 15).addBox(-2.0F, -2.0F, 0.7F, 3.0F, 2.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 4.3F, 0.0F, 0.3491F, -0.9599F));
            head.addOrReplaceChild("bandage_r2", CubeListBuilder.create().texOffs(0, 13).addBox(-1.0F, -2.0F, 0.7F, 3.0F, 2.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 4.3F, 0.0F, -0.3491F, 0.9599F));
            head.addOrReplaceChild("bandage_r3", CubeListBuilder.create().texOffs(0, 2).addBox(-2.0F, -2.0F, 0.7F, 3.0F, 2.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -4.5F, 4.3F, 0.0F, 0.3491F, -0.9599F));
            head.addOrReplaceChild("bandage_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 0.7F, 3.0F, 2.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -4.5F, 4.3F, 0.0F, -0.3491F, 0.9599F));
            head.addOrReplaceChild("bandage_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -3.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, -4.5F, -0.1F, -0.0436F, 0.0F, 0.0F));
            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 39).addBox(-4.0F, 0.5F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(-0.6F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, 0.5F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(-0.6F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        });
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return BandageRenderer::new;
    };

    @Override
    public EquipSound equipSound() {
        return EquipSound.GENERIC;
    }

    @Override
    public boolean hideUnderHelmet() {
        return false;
    }

    @Override
    public void onEquip(ItemStack stack, LivingEntity entity, SimpleSlot slot) {
        if (entity instanceof ServerPlayer player && slot.name.equals(IWearableAccessory.HAT))
            ServerUtils.grantAdvancement(player, "bandana_wear");
    }
}