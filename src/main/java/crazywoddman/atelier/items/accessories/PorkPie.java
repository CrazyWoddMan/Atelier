package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class PorkPie extends DyableAccessory {
    
    public PorkPie() {
        super(WHITE, DARK_GRAY);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
            head.addOrReplaceChild("pork_pie_r1", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.7F)), PartPose.offsetAndRotation(0.0F, 0.5F, -0.35F, -0.0436F, 0.0F, 0.0F));
            head.addOrReplaceChild("pork_pie_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.9F, -6.0F, 12.0F, 1.0F, 12.0F), PartPose.offsetAndRotation(0.0F, 1.0F, -0.25F, -0.0436F, 0.0F, 0.0F));
        });
    }
}