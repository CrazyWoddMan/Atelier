package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class UniformHat extends DyableAccessory {
    
    public UniformHat() {
        super(new Properties().stacksTo(1), 7568253);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(32, 32, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.5F, 0.0F, 0.0F)), PartPose.offsetAndRotation(0.0F, -6.8F, -5.5F, 0.0F, 3.1416F, 0.0F));
            head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.5F, 0.0F, 0.5F))
            .texOffs(0, 10).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.7F, 0.2F, 0.7F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, 3.1416F, 0.0F));
        });
    }
}