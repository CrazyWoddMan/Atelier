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
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;

public class BaseFilterRenderer implements AccessoryRenderer {
    private static final ResourceLocation BASE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/wearable/base_filter.png");
    private static final ResourceLocation LEFT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/wearable/base_filter_left.png");
    private static final ResourceLocation RIGHT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "textures/wearable/base_filter_right.png");
    private final HumanoidModel<LivingEntity> model;

    public BaseFilterRenderer() {
        this.model = HumanoidModelHelper.bake(AtelierItems.BASE_FILTER.getId());
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
        AccessoriesContainer gasmask = reference.capability().getContainer(new SlotTypeReference("face"));
        
        if (!gasmask.shouldRender(0))
            return;

        AccessoryRenderer.followBodyRotations(reference.entity(), this.model);
        Vector3f color = ((IDyable)stack.getItem()).getRGBcolor(stack);
        this.model.renderToBuffer(
            poseStack,
            multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(gasmask.getAccessories().getItem(0).is(AtelierItems.HALFMASK.get()) ? (reference.slot() == 0 ? LEFT_TEXTURE : RIGHT_TEXTURE) : BASE_TEXTURE)),
            light,
            OverlayTexture.NO_OVERLAY,
            color.x, color.y, color.z,
            1.0F
        );
    }
}