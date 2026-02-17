package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Uniform extends DyableAccessory {
    public Uniform() {
        super(new Properties().stacksTo(1), 7568253);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(64, 64, partdefinition -> {
            PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -1.0F, 0.575F, 1.0F, 1.0F, 0.0F)
            .texOffs(0, 0).addBox(3.0F, -1.0F, 0.575F, 1.0F, 1.0F, 0.0F)
            .texOffs(32, 10).addBox(1.25F, -1.0F, -1.075F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.6F))
            .texOffs(32, 15).addBox(-4.25F, -1.0F, -1.075F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.6F)), PartPose.offsetAndRotation(0.0F, 9.0F, -2.275F, 0.0F, 3.1416F, 0.0F));
            body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.51F))
            .texOffs(0, 0).addBox(-4.0F, -9.5F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.31F)), PartPose.offsetAndRotation(0.0F, 9.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F))
            .addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 14).addBox(-8.0F, -7.125F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.3F))
            .texOffs(24, 5).addBox(-8.0F, 1.625F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.4F, 0.2F, 0.4F)), PartPose.offsetAndRotation(-5.0F, 5.125F, 0.0F, -3.1416F, 0.0F, 3.1416F));
            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F))
            .addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 0).addBox(4.0F, 1.625F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.4F, 0.2F, 0.4F))
            .texOffs(16, 14).addBox(4.0F, -7.125F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(5.0F, 5.125F, 0.0F, -3.1416F, 0.0F, 3.1416F));
        });
    }
}