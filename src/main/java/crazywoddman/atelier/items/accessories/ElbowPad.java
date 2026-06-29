package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.client.renderers.ElbowPadsRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class ElbowPad extends DyableAccessory {

    public ElbowPad() {
        super(new Properties().stacksTo(Atelier.ACCESSORIES_LOADED ? 16 : 1), BIOPOLYMER, DEFAULT_LEATHER_COLOR);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition -> {
            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F))
            .addOrReplaceChild("elbowpad_r1", CubeListBuilder.create().texOffs(16, 27).addBox(-1.9F, -3.0F, -2.9F, 4.0F, 4.0F, 1.0F)
            .texOffs(0, 24).addBox(-1.9F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(-1.0F, 4.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F))
            .addOrReplaceChild("elbowpad_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.1F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F))
            .texOffs(16, 19).addBox(-2.1F, -3.0F, -2.9F, 4.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(1.0F, 4.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        });
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return ElbowPadsRenderer::new;
    };
}