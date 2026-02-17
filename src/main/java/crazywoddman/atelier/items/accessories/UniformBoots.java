package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class UniformBoots extends DyableAccessory {
    public UniformBoots() {
        super(new Properties().stacksTo(1), 8606770);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(32, 32, partdefinition -> {
            PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
            right_leg.addOrReplaceChild("boot_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.125F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(1.875F, 10.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));
            right_leg.addOrReplaceChild("boot_r2", CubeListBuilder.create().texOffs(0, 16).addBox(0.875F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.39F)), PartPose.offsetAndRotation(1.875F, 11.35F, -2.0F, -3.1416F, 0.0F, 3.1416F));
            PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
            left_leg.addOrReplaceChild("boot_r3", CubeListBuilder.create().texOffs(6, 16).addBox(-2.875F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.39F)), PartPose.offsetAndRotation(-1.875F, 11.35F, -2.0F, -3.1416F, 0.0F, 3.1416F));
            left_leg.addOrReplaceChild("boot_r4", CubeListBuilder.create().texOffs(0, 8).addBox(-3.875F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(-1.875F, 10.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));
        });
    }
}