package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.renderers.BandageRenderer;
import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.items.templates.IFabricAccessory;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.sounds.SoundEvents;

public class Bandage extends DyableAccessory implements IFabricAccessory {
    public Bandage() {
        super(new Properties().stacksTo(16), 16777215);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(64, 64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -3.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, -5.0F, -0.1F, -0.0436F, 0.0F, 0.0F));
            head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 13).addBox(-1.0F, -2.0F, -1.3F, 3.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -4.0F, 4.6F, 0.0F, -0.5672F, 0.9599F));
            head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(10, 13).addBox(-2.0F, -2.0F, -1.3F, 3.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -4.0F, 4.6F, 0.0F, 0.5672F, -0.9599F));

            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, 0.5F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(-0.69F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
		    partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, 0.5F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(-0.69F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        });
    }

    @Override
    public boolean hideUnderHelmet() {
        return false;
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return BandageRenderer::new;
    };

    @Override
    public SoundEventData getEquipSound() {
        return new SoundEventData(SoundEvents.ARMOR_EQUIP_GENERIC, 1, 1);
    }
}