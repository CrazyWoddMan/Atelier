package crazywoddman.atelier.renderers;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;

public class GlovesRenderer implements AccessoryRenderer {
    private static final ResourceLocation LEFT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/wearable/left_glove.png");
    private static final ResourceLocation RIGHT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/wearable/right_glove.png");
    private final HumanoidModel<LivingEntity> model;

    public GlovesRenderer() {
        this.model = HumanoidModelHelper.bake(AtelierItems.GLOVE.getId());
    }

    @SuppressWarnings("removal")
    @Override
    public <M extends LivingEntity> void render(
        ItemStack stack,
        SlotReference reference,
        PoseStack poseStack,
        EntityModel<M> model,
        MultiBufferSource multiBufferSource,
        int light,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        AccessoryRenderer.followBodyRotations(reference.entity(), this.model);

        if (stack.getItem() instanceof IDyable dyable) {
            Vector3f color = dyable.getRGBcolor(stack);
            this.model.renderToBuffer(
                poseStack,
                multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(reference.slot() == 0 ? LEFT_TEXTURE : RIGHT_TEXTURE)),
                light,
                OverlayTexture.NO_OVERLAY,
                color.x,
                color.y,
                color.z,
                1.0F
            );
        }
    }
}