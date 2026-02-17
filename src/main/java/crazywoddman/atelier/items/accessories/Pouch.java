package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.renderers.ConfigurableModuleRenderer;
import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.item.BundleItem;

public class Pouch extends BundleItem implements IWearableAccessory, IDyable {

    public Pouch() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return () -> new ConfigurableModuleRenderer(getTextureKey());
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(8, 8, partdefinition -> 
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, 9.0F, -3.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F))
		    .addOrReplaceChild("pouch_r1", CubeListBuilder.create().texOffs(0, 5).addBox(-0.5F, -0.8F, -0.8F, 2.0F, 2.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, 10.0F, -3.0F, -0.3054F, 0.0F, 0.0F))
        );
    }

    @Override
    public int getDefaultColor() {
        return 8606770;
    }
}