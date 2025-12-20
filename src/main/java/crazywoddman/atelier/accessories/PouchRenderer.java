package crazywoddman.atelier.accessories;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.models.PouchModel;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PouchRenderer extends SimpleWearableRenderer {

    public PouchRenderer() {
        super(PouchModel.LAYER_LOCATION, PouchModel.TEXTURE, PouchModel.OVERLAY);
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
        poseStack.pushPose();

        if (reference.slot() == 1)
            poseStack.translate(0.313, 0.0, 0.0);

        super.render(
            stack,
            reference,
            poseStack,
            model,
            multiBufferSource, light,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            netHeadYaw,
            headPitch
        );

        poseStack.popPose();
    }
}