package crazywoddman.atelier.accessories;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import crazywoddman.atelier.api.interfaces.IDyable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;

public class SimpleWearableRenderer implements AccessoryRenderer {
    private final ResourceLocation texture;
    private final Optional<ResourceLocation> overlay;
    private final HumanoidModel<LivingEntity> model;

    public SimpleWearableRenderer(ModelLayerLocation layerLocation, ResourceLocation texture, @Nullable ResourceLocation overlay) {
        this.model = new HumanoidModel<LivingEntity>(Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation));
        this.texture = texture;
        this.overlay = Optional.ofNullable(overlay);
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
        LivingEntity entity = reference.entity();
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        AccessoryRenderer.followBodyRotations(entity, this.model);

        if (stack.getItem() instanceof IDyable dyable) {
            int color = dyable.getColor(stack);
            VertexConsumer vertexBuilder = ItemRenderer.getFoilBuffer(
                multiBufferSource,
                RenderType.armorCutoutNoCull(texture),
                false,
                stack.hasFoil()
            );
            this.model.renderToBuffer(
                poseStack,
                vertexBuilder,
                light,
                OverlayTexture.NO_OVERLAY,
                ((color >> 16) & 0xFF) / 255.0F, // red
                ((color >> 8) & 0xFF) / 255.0F,  // green
                (color & 0xFF) / 255.0F,         // blue
                1.0F
            );

            if (this.overlay.isEmpty())
                return;
        }

        VertexConsumer overlayVertexBuilder = ItemRenderer.getFoilBuffer(
            multiBufferSource,
            RenderType.armorCutoutNoCull(this.overlay.orElse(texture)),
            false,
            stack.hasFoil()
        );

        this.model.renderToBuffer(
            poseStack,
            overlayVertexBuilder,
            light,
            OverlayTexture.NO_OVERLAY,
            1.0F, 1.0F, 1.0F,
            1.0F
        );
    }
}