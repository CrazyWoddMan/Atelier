package crazywoddman.atelier.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.SimpleWearableRenderer;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import io.wispforest.accessories.api.slot.SlotReference;

public class KneePadsRenderer extends SimpleWearableRenderer {

    public KneePadsRenderer() {
        super(AtelierItems.KNEEPADS.getId(), ref -> true);
    }

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
        if (!reference.entity().getItemBySlot(EquipmentSlot.LEGS).isEmpty()) {
            poseStack.scale(1.08f, 1.08f, 1.08f);
            poseStack.translate(0, -0.1f, 0);
        }

        super.render(stack, reference, poseStack, model, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }
}