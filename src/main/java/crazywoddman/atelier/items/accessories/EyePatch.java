package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.client.renderers.EyePatchRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class EyePatch extends DyableAccessory {
    
    public EyePatch() {
        super(DEFAULT_LEATHER_COLOR);
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return EyePatchRenderer::new;
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.1F))
            .texOffs(0, 3).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.1F))
            .texOffs(26, 0).addBox(-3.0F, -3.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.ZERO);
            head.addOrReplaceChild("eye_patch_2_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -3.75F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.8F)), PartPose.offsetAndRotation(-1.15F, 1.75F, 0.0F, 0.0F, 0.0F, -0.3927F));
            head.addOrReplaceChild("eye_patch_3_4_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -3.75F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.8F)), PartPose.offsetAndRotation(-0.25F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
        });
    }
}