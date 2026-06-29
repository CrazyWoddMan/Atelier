package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CigaretteRenderer extends SimpleAccessoryRenderer {
    private static final ResourceLocation TEXTURE = new WearableTexture(AtelierItems.CIGARETTE.get()).get();

    public CigaretteRenderer() {
        super(null, AtelierItems.CIGARETTE.getId());
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
                case 3 -> pose.translate(0, 0.05, 0);
                case 2 -> pose.translate(0, 0.07, 0);
            }
        });
        render(
            pose,
            light,
            buffer,
            RenderType.entityCutout(TEXTURE)
        );
        pose.popPose();
    }
}