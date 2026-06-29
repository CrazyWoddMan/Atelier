package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class Papakha extends DyableAccessory {
    
    public Papakha() {
        super(WHITE);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition ->
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO)
            .addOrReplaceChild("papakha_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -13.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.7F, 0, 0.7F))
            .texOffs(0, 15).addBox(-4.0F, -13.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.8F, 0.1F, 0.8F)), PartPose.offsetAndRotation(0.0F, 0.5F, -0.3F, -0.0436F, 0.0F, 0.0F))
        );
    }
}