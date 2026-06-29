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
import net.minecraft.world.item.ItemStack;

public class GasMaskB extends AbstractGasMask implements IModular {
    public GasMaskB() {
        super(2, BIOPOLYMER, BLACK);
    }

    @Override
    public boolean modulePredicate(String module, ItemStack stack) {
        return module.equals(GAS_FILTER)
        ? stack.is(AtelierItems.BASE_FILTER.get())
        : IModular.super.modulePredicate(module, stack);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition ->
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-0.4F))
            .texOffs(0, 20).addBox(-5.0F, -6.0F, -5.1F, 10.0F, 4.0F, 2.0F, new CubeDeformation(-0.11F))
            .texOffs(0, 26).addBox(-5.0F, -5.1F, -5.1F, 10.0F, 4.0F, 2.0F, new CubeDeformation(-0.11F))
            .texOffs(0, 32).addBox(-5.0F, -4.25F, -5.1F, 10.0F, 4.0F, 2.0F, new CubeDeformation(-0.11F)), PartPose.ZERO)
            .addOrReplaceChild("gasmask_b_r1", CubeListBuilder.create().texOffs(28, 20).addBox(-1.0F, -2.4F, -2.0F, 2.0F, 3.0F, 4.0F)
            .texOffs(24, 27).addBox(-2.0F, -1.4F, -1.0F, 4.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, 0.48F, 0.0F, 0.0F))
        );
    }
}