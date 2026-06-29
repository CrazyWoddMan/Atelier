package crazywoddman.atelier.api.render;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class AtelierRenderHelper {
    public static Supplier<LayerDefinition> createLayer(int textureSize, Consumer<PartDefinition> transform) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        for (String name : new String[]{"head", "hat", "body", "right_arm", "left_arm", "right_leg", "left_leg"})
            part.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.ZERO);

        transform.accept(part);
        return () -> LayerDefinition.create(mesh, textureSize, textureSize);
    }

    public static HumanoidModel<LivingEntity> bake(ResourceLocation layer) {
        return layer == null
        ? null
        : new HumanoidModel<LivingEntity>(Minecraft.getInstance().getEntityModels().bakeLayer(new ModelLayerLocation(layer, "main")));
    }
}