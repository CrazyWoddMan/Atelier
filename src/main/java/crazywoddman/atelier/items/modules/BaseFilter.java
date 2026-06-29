package crazywoddman.atelier.items.modules;

import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IModuleRenderer;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.client.renderers.BaseFilterRenderer;
import crazywoddman.atelier.items.templates.FilterItem;
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
        return AtelierRenderHelper.createLayer(32, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
            head.addOrReplaceChild("base_filter_c_r1", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0F, -1.0F, -1.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(3.0F, -0.7388F, -7.3415F, 0.9855F, -0.9275F, -0.879F));
            head.addOrReplaceChild("base_filter_c_r2", CubeListBuilder.create().texOffs(10, 5).addBox(-2.0F, -1.0F, -1.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-3.0F, -0.7388F, -7.3415F, 0.9855F, 0.9275F, 0.879F));
            head.addOrReplaceChild("base_filter_b_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -0.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(3.0F, -0.7388F, -7.3415F, 1.2786F, -1.0215F, -1.2318F));
            head.addOrReplaceChild("base_filter_b_r2", CubeListBuilder.create().texOffs(0, 5).addBox(-2.0F, -1.0F, -0.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-3.0F, -0.7388F, -7.3415F, 1.2786F, 1.0215F, 1.2318F));
            head.addOrReplaceChild("base_filter_a_r1", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 1.2612F, -7.3415F, 0.5236F, 0.0F, 0.0F));
        });
    }

    @Override
    public IModuleRenderer getRenderer() {
        return new BaseFilterRenderer();
    }
}