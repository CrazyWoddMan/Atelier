package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.client.renderers.WeldingGogglesRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class WeldingGoggles extends DyableAccessory {
    
    public WeldingGoggles() {
        super(IRON, 0x0C0C0E);
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return WeldingGogglesRenderer::new;
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition ->
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, -5.0F, -4.75F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
            .texOffs(0, 0).addBox(-3.0F, -5.0F, -4.75F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
            .texOffs(6, 0).addBox(-0.5F, -5.0F, -4.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.25F))
            .texOffs(6, 2).addBox(3.5F, -4.75F, -4.45F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.25F))
            .texOffs(6, 2).addBox(-4.5F, -4.75F, -4.45F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.25F))
            .texOffs(0, 1).addBox(-4.0F, -4.75F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.7F, 0.2F, 0.7F)), PartPose.offset(0.0F, 0.0F, 0.0F))
        );
    }

    @Override
    public EquipSound equipSound() {
        return EquipSound.NETHERITE;
    }
}