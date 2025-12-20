package crazywoddman.atelier.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import crazywoddman.atelier.Atelier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class BeltModel<T extends LivingEntity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
		ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "belt"),
		"main"
	);
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/models/wearable/belt.png");
	public static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/models/wearable/belt_overlay.png");
	public final ModelPart body;

	public BeltModel(ModelPart root) {
		this.body = root.getChild("body");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 10.0F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.01F))
		.texOffs(0, 0).addBox(-4.75F, 10.0F, -0.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F, 0.1F, 0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F))
		.addOrReplaceChild("belt_r1", CubeListBuilder.create().texOffs(23, 0).addBox(-0.3F, -2.0F, -1.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-4.5F, 12.0F, 0.5F, 0.0F, -0.3054F, 0.0F));

		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_boot", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_boot", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}