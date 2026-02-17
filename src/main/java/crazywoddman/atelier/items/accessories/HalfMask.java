package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.items.templates.GasMaskItem;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class HalfMask extends GasMaskItem {
    public HalfMask() {
        super(new Properties(), 2);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(64, 64, partdefinition -> 
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-0.4F))
            .texOffs(0, 20).addBox(-5.0F, -6.0F, -5.1F, 10.0F, 4.0F, 2.0F, new CubeDeformation(-0.11F)), PartPose.offset(0.0F, 0.0F, 0.0F))
            .addOrReplaceChild("halfmask_r1", CubeListBuilder.create().texOffs(24, 20).addBox(-1.0F, -2.4F, -2.0F, 2.0F, 3.0F, 4.0F)
            .texOffs(20, 27).addBox(-2.0F, -1.4F, -1.0F, 4.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, 0.48F, 0.0F, 0.0F))
        );
    }
}