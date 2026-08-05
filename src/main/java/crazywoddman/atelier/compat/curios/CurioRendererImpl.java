package crazywoddman.atelier.compat.curios;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

class CurioRendererImpl implements ICurioRenderer {
    private final IAccessoryRenderer renderer;

    CurioRendererImpl(Supplier<IAccessoryRenderer> renderer) {
        this.renderer = renderer.get();
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
        ItemStack stack,
        SlotContext context,
        PoseStack pose,
        RenderLayerParent<T, M> renderLayerParent,
        MultiBufferSource buffer,
        int light,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float age,
        float HeadYaw,
        float headPitch
    ) {
        pose.pushPose();
        renderer.render(stack, context.entity(), SimpleSlot.of(context.identifier(), context.index()), pose, renderLayerParent.getModel(), buffer, light);
        pose.popPose();
    }
}