package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.items.templates.FilterItem;
import crazywoddman.atelier.renderers.BaseFilterRenderer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class BaseFilter extends FilterItem {

    public BaseFilter() {
        super(new Properties().durability(300));
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(16, 16, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("base_filter_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -0.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(3.0F, -0.7388F, -7.3415F, 1.2786F, -1.0215F, -1.2318F));
            head.addOrReplaceChild("base_filter_r2", CubeListBuilder.create().texOffs(0, 5).addBox(-2.0F, -1.0F, -0.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-3.0F, -0.7388F, -7.3415F, 1.2786F, 1.0215F, 1.2318F));
            head.addOrReplaceChild("base_filter_r3", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 1.2612F, -7.3415F, 0.5236F, 0.0F, 0.0F));
        });
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return BaseFilterRenderer::new;
    }
}