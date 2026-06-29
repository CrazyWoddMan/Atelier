package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class FieldJacket extends DyableAccessory {
    
    public FieldJacket() {
        super(WHITE);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition -> {
           partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(12, 25).addBox(-3.0F, -0.3F, -2.35F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.3F))
		    .texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 11.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.ZERO);
            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 15).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false)
            .texOffs(16, 15).addBox(-3.0F, 6.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 15).mirror().addBox(-1.0F, 6.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false)
            .texOffs(0, 15).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        });
    }
}