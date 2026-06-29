package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.item.ItemStack;

public class Beret extends DyableAccessory {
    public Beret() {
        super(WHITE, WHITE);
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        IDyeable.setColor(stack, color, 0);
        IDyeable.setColor(stack, color, 1);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition ->
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.7F))
            .texOffs(0, 22).addBox(-4.0F, -6.75F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.52F)), PartPose.ZERO)
            .addOrReplaceChild("beret_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.75F, -7.0F, 0.0F, 3.1416F, 0.0F, 3.0543F))
        );
    }
}