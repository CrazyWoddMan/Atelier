package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ElbowPadsRenderer extends SimpleAccessoryRenderer {

    public ElbowPadsRenderer() {
        super(new WearableTexture(Atelier.MODID, "pad"), AtelierItems.ELBOW_PAD.getId());
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
        boolean isLeft = slot.index == 0;
        render(
            pose,
            light,
            buffer,
            RenderType.armorCutoutNoCull(this.texture.get(isLeft ? "0_left" : "0")),
            stack,
            0
        );
        render(
            pose,
            light,
            buffer,
            RenderType.armorCutoutNoCull(this.texture.get(isLeft ? "1_left" : "1")),
            stack,
            1
        );
    }
}