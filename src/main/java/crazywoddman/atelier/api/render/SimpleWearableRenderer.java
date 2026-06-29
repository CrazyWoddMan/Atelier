package crazywoddman.atelier.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.interfaces.IModuleRenderer;
import crazywoddman.atelier.client.ModulesRenderData;
import crazywoddman.atelier.client.WearableTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SimpleWearableRenderer {
    protected final WearableTexture texture;
    protected final HumanoidModel<LivingEntity> model;

    public SimpleWearableRenderer(WearableTexture texture, ResourceLocation layer) {
        this.model = AtelierRenderHelper.bake(layer);
        this.texture = texture;
    }

    protected void render(PoseStack pose, int light, MultiBufferSource buffer, RenderType renderType, float... colors) {
        this.model.renderToBuffer(
            pose,
            buffer.getBuffer(renderType),
            light,
            OverlayTexture.NO_OVERLAY,
            colors[0],
            colors[1],
            colors[2],
            colors[3]
        );
    }

    protected void render(PoseStack pose, int light, MultiBufferSource buffer, RenderType renderType) {
        render(pose, light, buffer, renderType, 1, 1, 1, 1);
    }

    protected void render(PoseStack pose, int light, MultiBufferSource buffer, RenderType renderType, ItemStack dyable, int colorIndex) {
        float[] color = IDyeable.getFloatColor(dyable, colorIndex);
        render(pose, light, buffer, renderType, color[0], color[1], color[2], 1);
    }

    protected void renderHumanoidModel(
        ItemStack stack,
        PoseStack pose,
        MultiBufferSource buffer,
        int light
    ) {
        if (stack.getItem() instanceof IDyeable dyable) {
            for (int i = 0; i < dyable.getDefaultColors().length; i++) {
                render(
                    pose,
                    light,
                    buffer,
                    RenderType.entityTranslucent(this.texture.get(i)),
                    stack,
                    i
                );
            }

            this.texture.getOverlay().ifPresent(overlay -> this.model.renderToBuffer(
                pose,
                buffer.getBuffer(RenderType.entityTranslucent(overlay)),
                light,
                OverlayTexture.NO_OVERLAY,
                1, 1, 1, 1
            ));
        } else render(
            pose,
            light,
            buffer,
            RenderType.entityTranslucent(this.texture.get())
        );
    }
    
    @SuppressWarnings("unchecked")
    public static void followBodyRotations(LivingEntity entity, HumanoidModel<LivingEntity> model) {
        EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        
        if (render instanceof LivingEntityRenderer renderer && renderer.getModel() instanceof HumanoidModel entityModel) 
            entityModel.copyPropertiesTo(model);
    }

    /** Scales PoseStack without breaking the lighting if some of the scaling axes have negative values (useful for mirroring model) */
    public static void scaleSafe(PoseStack pose, float x, float y, float z) {
        PoseStack.Pose last = pose.last();
        last.pose().scale(x, y, z);
        float ax = Math.abs(x);
        float ay = Math.abs(y);
        float az = Math.abs(z);
        float invScale = 1.0f / (float) Math.cbrt(ax * ay * az);
        int negCount = (x < 0 ? 1 : 0) + (y < 0 ? 1 : 0) + (z < 0 ? 1 : 0);
        float sign = (negCount % 2 == 1) ? -1.0f : 1.0f;
        last.normal().scale(sign * invScale * ax, invScale * ay, invScale * az);
    }

    public static void renderModules(
        ItemStack parent,
        LivingEntity entity,
        SimpleSlot slot,
        PoseStack pose,
        EntityModel<? extends LivingEntity> model,
        MultiBufferSource buffer,
        int light
    ) {
        IModular.forEachEquipped(parent, (module, stack) -> ModulesRenderData.get(parent.getItem(), module, slot).ifPresentOrElse(
            renderData -> {
                pose.pushPose();
                renderData.bodyPart.getModelPart((HumanoidModel<?>)model).translateAndRotate(pose);
                renderData.getTranslate().ifPresent(translate ->
                    pose.translate(translate[0], translate[1], translate[2])
                );
                renderData.getRotation().ifPresent(rotation -> {
                    if (rotation[0] != 0)
                        pose.mulPose(Axis.XP.rotationDegrees(rotation[0]));
                    if (rotation[1] != 0)
                        pose.mulPose(Axis.YP.rotationDegrees(rotation[1]));
                    if (rotation[2] != 0)
                        pose.mulPose(Axis.ZP.rotationDegrees(rotation[2]));
                });
                renderData.getScale().ifPresent(scale ->
                    scaleSafe(pose, scale[0], scale[1], scale[2])
                );

                if (!IModuleRenderer.tryRender(stack, entity, slot, module, pose, model, buffer, light))
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buffer, entity.level(), 0);

                pose.popPose();
            },
            () -> IModuleRenderer.tryRender(stack, entity, slot, module, pose, model, buffer, light)
        ));
    }
}