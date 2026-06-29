package crazywoddman.atelier.items.modules;

import java.util.function.Supplier;

import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.items.templates.FilterItem;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class AdvancedFilter extends FilterItem {

    public AdvancedFilter() {
        super(new Properties().durability(480));
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition -> 
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F))
		    .addOrReplaceChild("advanced_filter_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.6612F, -3.5585F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 1.2612F, -7.3415F, 0.6981F, 0.0F, 0.0F))
        );
    }
}