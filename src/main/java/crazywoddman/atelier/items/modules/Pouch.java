package crazywoddman.atelier.items.modules;

import java.util.function.Supplier;

import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.interfaces.IModule;
import crazywoddman.atelier.api.interfaces.IModuleRenderer;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.render.SimpleModuleRenderer;
import crazywoddman.atelier.api.templates.DyeableContainerItem;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.data.AtelierTags;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class Pouch extends DyeableContainerItem implements IModule {

    public Pouch() {
        super(new Properties().stacksTo(1), DEFAULT_LEATHER_COLOR, GOLD);
    }

    @Override
    public IModuleRenderer getRenderer() {
        return new SimpleModuleRenderer(new WearableTexture(this),  ForgeRegistries.ITEMS.getKey(this), false);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(8, partdefinition -> 
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, 9.0F, -3.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F))
		    .addOrReplaceChild("pouch_r1", CubeListBuilder.create().texOffs(0, 5).addBox(-0.5F, -0.8F, -0.8F, 2.0F, 2.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, 10.0F, -3.0F, -0.3054F, 0.0F, 0.0F))
        );
    }

    @Override
    public int getContainerCapacity() {
        return AtelierConfig.Server.POUCH_CAPACITY.get();
    }

    @Override
    public boolean insertAtIndex(ItemStack stack, ItemStack input, SlotAccess output, int index) {
        return !input.is(AtelierTags.Items.POUCH_BLACKLIST) && super.insertAtIndex(stack, input, output, index);
    }
}