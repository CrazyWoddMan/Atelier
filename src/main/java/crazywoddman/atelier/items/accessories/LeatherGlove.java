package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.client.renderers.GlovesRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class LeatherGlove extends DyableAccessory {
    
    public LeatherGlove() {
        super(new Properties().stacksTo(Atelier.ACCESSORIES_LOADED ? 16 : 1), DEFAULT_LEATHER_COLOR);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(16, partdefinition -> {
            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 7.2F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.6F, 0.3F, 0.6F))
            .texOffs(0, 0).mirror().addBox(-3.0F, 8.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.31F)).mirror(false), PartPose.offset(-5.0F, 2.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 6).mirror().addBox(-1.0F, 7.2F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.6F, 0.3F, 0.6F)).mirror(false)
            .texOffs(0, 6).addBox(-1.0F, 8.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.31F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        });
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return GlovesRenderer::new;
    };
}