package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class Ushanka extends DyableAccessory {
    public Ushanka() {
        super(new Properties().stacksTo(1), 4673362);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(64, 64, partdefinition ->
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 18).addBox(-5.0F, -9.0F, -4.75F, 10.0F, 3.0F, 1.0F, new CubeDeformation(-0.01F))
		    .texOffs(0, 0).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F))
        );
    }
}