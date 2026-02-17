package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class UniformPants extends DyableAccessory {

    public UniformPants() {
        super(new Properties().stacksTo(1), 7568253);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(32, 32, partdefinition -> {
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.3F))
            .texOffs(0, 12).addBox(-2.0F, 7.2F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.4F, 0.2F, 0.4F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.3F))
            .texOffs(16, 12).addBox(-2.0F, 7.2F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.4F, 0.2F, 0.4F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        });
    }
}