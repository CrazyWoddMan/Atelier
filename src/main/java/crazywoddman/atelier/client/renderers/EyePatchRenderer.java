package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.items.templates.SimpleMask;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EyePatchRenderer extends SimpleAccessoryRenderer {

    public <T extends SimpleMask> EyePatchRenderer() {
        super(new WearableTexture(AtelierItems.EYE_PATCH.get()), AtelierItems.EYE_PATCH.getId());
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
        WearablesCapability cap = WearablesCapability.get(entity).orElse(WearablesCapability.EMPTY);

        if (cap.eyePatch == WearablesCapability.EyePatch.LEFT) {
            scaleSafe(pose, -1, 1, 1);
            this.model.head.yRot = -this.model.head.yRot;
        }

        render(
            pose,
            light,
            buffer,
            RenderType.armorCutoutNoCull(this.texture.get(cap.eyesLevel == 5 ? 4 : cap.eyesLevel)),
            stack,
            0
        );
        render(
            pose,
            light,
            buffer,
            RenderType.armorCutoutNoCull(this.texture.get(cap.eyesLevel == 2 ? "overlay_2" : "overlay"))
        );
        
        if (cap.eyePatch == WearablesCapability.EyePatch.LEFT)
            this.model.head.yRot = -this.model.head.yRot;
    }
}