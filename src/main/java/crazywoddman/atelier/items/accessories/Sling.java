package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyeableContainerItem;
import crazywoddman.atelier.client.renderers.SlingRenderer;
import crazywoddman.atelier.data.AtelierTags;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public class Sling extends DyeableContainerItem implements IWearableAccessory {

    public Sling() {
        super(new Properties().stacksTo(1), DEFAULT_LEATHER_COLOR, GOLD);
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return SlingRenderer::new;
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition ->
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO)
            .addOrReplaceChild("sling_r1", CubeListBuilder.create().texOffs(16, 0).addBox(-3.0F, 5.0F, -4.0F, 5.0F, 5.0F, 3.0F, new CubeDeformation(-1.4F, -1.4F, -1))
            .texOffs(0, 2).addBox(-2.0F, -6.0F, -3.55F, 3.0F, 24.0F, 6.0F, new CubeDeformation(-0.7F, -5, -0.7F)), PartPose.offsetAndRotation(-2.75F, 0.0F, 0.55F, 0.0F, 0.0F, -0.6109F))
        );
    }

    @Override
    public boolean insertAtIndex(ItemStack stack, ItemStack input, SlotAccess output, int index) {
        return (input.isEmpty() || input.is(AtelierTags.Items.SLING))
            && super.insertAtIndex(stack, input, output, index);
    }

    @Override
    public EquipSound interactSound() {
        return EquipSound.IRON;
    }
}