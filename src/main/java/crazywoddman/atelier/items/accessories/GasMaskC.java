package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.items.templates.AbstractGasMask;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.item.ItemStack;

public class GasMaskC extends AbstractGasMask implements IModular {
    
    public GasMaskC() {
        super(2, BIOPOLYMER, DEFAULT_LEATHER_COLOR);
    }

    @Override
    public boolean modulePredicate(String module, ItemStack stack) {
        return module.equals(GAS_FILTER)
        ? stack.is(AtelierItems.BASE_FILTER.get())
        : IModular.super.modulePredicate(module, stack);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-0.49F))
            .texOffs(0, 20).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-0.4F)), PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("gasmask_c_2_r1", CubeListBuilder.create().texOffs(0, 52).addBox(-2.0F, -2.4F, -1.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, -5.0F, 0.0873F, 0.0F, 0.0F));
            head.addOrReplaceChild("gasmask_c_3_2_r1", CubeListBuilder.create().texOffs(14, 46).addBox(-2.0F, -1.0F, -0.5F, 4.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(0.0F, -0.9F, -6.5F, 0.1745F, 0.0F, 0.0F));
            head.addOrReplaceChild("gasmask_c_3_r1", CubeListBuilder.create().texOffs(0, 46).addBox(-2.0F, -2.4F, -1.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, -5.0F, 0.48F, 0.0F, 0.0F));
            head.addOrReplaceChild("gasmask_c_4_r1", CubeListBuilder.create().texOffs(14, 40).addBox(-2.0F, -1.0F, -0.5F, 4.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(0.0F, -1.4F, -6.5F, 0.1745F, 0.0F, 0.0F));
            head.addOrReplaceChild("gasmask_c_4_r2", CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, -2.4F, -1.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, 0.48F, 0.0F, 0.0F));
            head.addOrReplaceChild("gasmask_c_r1", CubeListBuilder.create().texOffs(24, 40).mirror().addBox(-2.0F, -1.4F, -0.9F, 1.0F, 1.0F, 1.0F).mirror(false), PartPose.offsetAndRotation(-1.0F, 0.0F, -5.0F, 0.4996F, -0.27F, -0.1446F));
            head.addOrReplaceChild("gasmask_c_r2", CubeListBuilder.create().texOffs(24, 40).addBox(1.0F, -1.4F, -0.9F, 1.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(1.0F, 0.0F, -5.0F, 0.4996F, 0.27F, 0.1446F));
        });
    }
}