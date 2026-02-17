package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ServiceCap extends DyableAccessory {
    public ServiceCap() {
        super(new Properties().stacksTo(1), 7568253);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(64, 64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("service_cap_r1", CubeListBuilder.create().texOffs(16, 25).addBox(-6.0F, -1.0F, -5.9F, 12.0F, 2.0F, 12.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, -8.4F, 0.0F, -0.1309F, 0.0F, 0.0F));
            head.addOrReplaceChild("service_cap_r2", CubeListBuilder.create().texOffs(0, 25).addBox(-5.0F, -2.4F, -2.7F, 10.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.7F, -6.0F, 0.0873F, 0.0F, 0.0F));
            head.addOrReplaceChild("service_cap_r3", CubeListBuilder.create().texOffs(0, 13).addBox(-5.0F, -2.3F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.7F, 0.0F, -0.0436F, 0.0F, 0.0F));
        });
    }
}