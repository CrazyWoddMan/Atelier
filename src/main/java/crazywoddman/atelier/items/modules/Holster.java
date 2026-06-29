package crazywoddman.atelier.items.modules;

import java.util.function.Supplier;

import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.interfaces.IModule;
import crazywoddman.atelier.api.interfaces.IModuleRenderer;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyeableContainerItem;
import crazywoddman.atelier.client.renderers.HolsterRenderer;
import crazywoddman.atelier.data.AtelierTags;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public class Holster extends DyeableContainerItem implements IModule {

    public Holster() {
        super(new Properties().stacksTo(1), DEFAULT_LEATHER_COLOR);
    }

    @Override
    public IModuleRenderer getRenderer() {
        return new HolsterRenderer();
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(8, partdefinition ->
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(4, 0).addBox(-2.5F, 9.0F, -3.531F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.01F))
            .texOffs(0, 0).addBox(-3.0F, 9.0F, -3.531F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.01F))
            .texOffs(4, 5).addBox(-4.0F, 9.5F, -3.531F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.ZERO)
        );
    }

    @Override
    public boolean insertAtIndex(ItemStack stack, ItemStack input, SlotAccess output, int index) {
        return (input.isEmpty() || input.is(AtelierTags.Items.HOLSTER))
            && super.insertAtIndex(stack, input, output, index);
    }

    @Override
    public EquipSound interactSound() {
        return EquipSound.CHAIN;
    }
}