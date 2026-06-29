package crazywoddman.atelier.items.templates;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.client.renderers.SimpleMaskRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

public class SimpleMask extends DyableAccessory {
    
    public SimpleMask() {
        super(WHITE);
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return () -> new SimpleMaskRenderer(this);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition -> 
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.offset(0.0F, 0.0F, 0.0F))
        );
    }

    @Override
    public ResourceLocation getLayerKey() {
        return ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "mask");
    }
}