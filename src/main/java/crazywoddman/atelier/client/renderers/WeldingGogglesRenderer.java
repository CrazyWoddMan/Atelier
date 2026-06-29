package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WeldingGogglesRenderer extends SimpleAccessoryRenderer {

    public WeldingGogglesRenderer() {
        super(new WearableTexture(AtelierItems.WELDING_GOGGLES.get()), AtelierItems.WELDING_GOGGLES.getId());
    }

    @Override
    protected void renderModel(
        ItemStack stack,
        LivingEntity entity,
        SimpleSlot slot,
        PoseStack pose,
        MultiBufferSource buffer,
        int light
    ) {
        pose.pushPose();
        WearablesCapability.get(entity).ifPresent(cap -> {
            switch (cap.eyesLevel) {
                case 3 -> pose.translate(0, 0.07, 0);
                case 2 -> pose.translate(0, 0.14, 0);
            }
        });
        super.renderModel(stack, entity, slot, pose, buffer, light);
        pose.popPose();
    }
}