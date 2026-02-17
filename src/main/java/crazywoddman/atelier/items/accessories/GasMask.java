package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.items.templates.GasMaskItem;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class GasMask extends GasMaskItem {
    
    public GasMask() {
        super(new Properties(), 1);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(64, 64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-0.4F)), PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("gasmask_r1", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, -2.4F, -1.0F, 2.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 1.0F, -5.0F, 0.48F, 0.0F, 0.0F));
            head.addOrReplaceChild("gasmask_r2", CubeListBuilder.create().texOffs(12, 20).addBox(-2.0F, -3.0F, 0.9F, 4.0F, 4.0F, 2.0F), PartPose.offsetAndRotation(3.0F, -3.0F, -6.0F, 0.0F, -0.2182F, 0.0F));
            head.addOrReplaceChild("gasmask_r3", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, -3.0F, 0.9F, 4.0F, 4.0F, 2.0F), PartPose.offsetAndRotation(-3.0F, -3.0F, -6.0F, 0.0F, 0.2182F, 0.0F));
        });
    }
}