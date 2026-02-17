package crazywoddman.atelier.api;

import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.api.interfaces.IWearable;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;

public class SimpleWearableRenderer implements AccessoryRenderer {
    protected final ResourceLocation texture;
    protected final @Nullable ResourceLocation overlay;
    protected final Function<ResourceLocation, RenderType> mainRenderType;
    protected final @Nullable Function<ResourceLocation, RenderType> overlayRenderType;
    protected final HumanoidModel<LivingEntity> model;
    protected final Predicate<SlotReference> shouldRender;
    protected boolean followBody = true;

    public SimpleWearableRenderer() {
        this.model = null;
        this.texture = null;
        this.overlay = null;
        this.mainRenderType = null;
        this.overlayRenderType = null;
        this.shouldRender = ref -> true;
    }

    public SimpleWearableRenderer(ResourceLocation textureKey, ResourceLocation layerKey, Predicate<SlotReference> shouldRender, Function<ResourceLocation, RenderType> mainRenderType, @Nullable Function<ResourceLocation, RenderType> overlayRenderType) {
        this.model = HumanoidModelHelper.bake(layerKey);
        this.texture = IWearable.getModelTexture(textureKey);
        this.overlay = IWearable.getOverlayTexture(textureKey).orElse(null);
        this.mainRenderType = mainRenderType;
        this.overlayRenderType = overlayRenderType;
        this.shouldRender = shouldRender;
    }

    public SimpleWearableRenderer(ResourceLocation key, Predicate<SlotReference> shouldRender, Function<ResourceLocation, RenderType> mainRenderType, @Nullable Function<ResourceLocation, RenderType> overlayRenderType) {
        this(key, key, shouldRender, mainRenderType, overlayRenderType);
    }

    public SimpleWearableRenderer(ResourceLocation textureKey, ResourceLocation layerKey, Predicate<SlotReference> shouldRender) {
        this(textureKey, layerKey, shouldRender, RenderType::armorCutoutNoCull, RenderType::armorCutoutNoCull);
    }

    public SimpleWearableRenderer(ResourceLocation key, Predicate<SlotReference> shouldRender) {
        this(key, key, shouldRender);
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
        if (!this.shouldRender.test(reference))
            return;

        LivingEntity entity = reference.entity();

        if (reference.slotName().equals("hat") && ((IWearableAccessory)stack.getItem()).hideUnderHelmet() && !entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
            return;

        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (followBody)
            AccessoryRenderer.followBodyRotations(entity, this.model);
        else
            align(stack, reference, model, poseStack);

        if (stack.getItem() instanceof IDyable dyable) {
            Vector3f color = dyable.getRGBcolor(stack);
            this.model.renderToBuffer(
                poseStack,
                multiBufferSource.getBuffer(this.mainRenderType.apply(this.texture)),
                light,
                OverlayTexture.NO_OVERLAY,
                color.x,
                color.y,
                color.z,
                1.0F
            );

            if (this.overlay == null)
                return;
        }

        this.model.renderToBuffer(
            poseStack,
            multiBufferSource.getBuffer(this.overlay == null ? this.mainRenderType.apply(this.texture) : this.overlayRenderType.apply(this.overlay)),
            light,
            OverlayTexture.NO_OVERLAY,
            1.0F, 1.0F, 1.0F,
            1.0F
        );
    }

    public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, PoseStack poseStack) {}
}