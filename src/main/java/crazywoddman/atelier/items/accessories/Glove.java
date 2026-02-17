package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.renderers.GlovesRenderer;
import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Glove extends DyableAccessory {
    public Glove() {
        super(new Properties().stacksTo(16), 8606770);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(16, 16, partdefinition -> {
            PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
            right_arm.addOrReplaceChild("right_glove_r1", CubeListBuilder.create().texOffs(0, 0).addBox(4.0F, 0.875F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.31F)), PartPose.offsetAndRotation(5.0F, 7.125F, 0.0F, -3.1416F, 0.0F, 3.1416F));
            right_arm.addOrReplaceChild("right_glove_r2", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(4.0F, 2.1F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.6F)).mirror(false), PartPose.offsetAndRotation(5.0F, 4.5F, 0.0F, -3.1416F, 0.0F, 3.1416F));
            PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
            left_arm.addOrReplaceChild("left_glove_r1", CubeListBuilder.create().texOffs(0, 6).mirror().addBox(4.0F, 0.875F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.31F)).mirror(false), PartPose.offsetAndRotation(7.0F, 7.125F, 0.0F, -3.1416F, 0.0F, 3.1416F));
            left_arm.addOrReplaceChild("left_glove_r2", CubeListBuilder.create().texOffs(0, 6).addBox(4.0F, 2.1F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.6F)), PartPose.offsetAndRotation(7.0F, 4.5F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        });
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return GlovesRenderer::new;
    };
}