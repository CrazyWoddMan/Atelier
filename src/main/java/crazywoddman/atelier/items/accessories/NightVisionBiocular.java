package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.items.templates.NightVisionDevice;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
public class NightVisionBiocular extends NightVisionDevice {

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(-0.39F))
            .texOffs(40, 9).addBox(-1.0F, -7.9071F, -6.4163F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.ZERO);
            head.addOrReplaceChild("nvbo_down_3_r1", CubeListBuilder.create().texOffs(16, 28).addBox(-1.0F, -4.7F, -4.0F, 2.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -5.0F, -5.5F, 1.5708F, 0.0F, 0.0F));
            head.addOrReplaceChild("nvbo_down_4_r1", CubeListBuilder.create().texOffs(16, 23).addBox(-1.0F, -4.7F, -5.0F, 2.0F, 2.0F, 2.0F)
            .texOffs(16, 18).addBox(-1.0F, -4.7F, -4.0F, 2.0F, 2.0F, 2.0F)
            .texOffs(0, 18).addBox(-3.0F, -2.7F, -4.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -7.0F, -5.5F, 1.5708F, 0.0F, 0.0F));
            head.addOrReplaceChild("nvd_down_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, -0.6856F, -0.5429F, 4.0F, 5.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, -7.0F, -5.4952F, -0.1309F, 0.0F, 0.0F));
            head.addOrReplaceChild("nvbo_up_r1", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -4.7F, -4.0F, 2.0F, 2.0F, 2.0F)
            .texOffs(48, 5).addBox(-3.0F, -2.7F, -4.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -7.0F, -5.5F, -0.7854F, 0.0F, 0.0F));
            head.addOrReplaceChild("nvd_up_r1", CubeListBuilder.create().texOffs(50, 0).addBox(-2.0F, -0.6856F, -0.5429F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, -7.0F, -5.4952F, -2.4871F, 0.0F, 0.0F));
        });
    }
}