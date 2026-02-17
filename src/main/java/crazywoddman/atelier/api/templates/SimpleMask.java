package crazywoddman.atelier.api.templates;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.HumanoidModelHelper;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

public class SimpleMask extends DyableAccessory {
    public SimpleMask(int defaultColor) {
        super(new Properties().stacksTo(1), defaultColor);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(32, 32, partdefinition -> 
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.offset(0.0F, 0.0F, 0.0F))
        );
    }

    @Override
    public ResourceLocation getModelKey() {
        return ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "mask");
    }
}