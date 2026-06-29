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

public class GlovesRenderer extends SimpleAccessoryRenderer {

    public GlovesRenderer() {
        super(new WearableTexture(Atelier.MODID, "leather_glove"), AtelierItems.LEATHER_GLOVE.getId());
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
        render(
            pose,
            light,
            buffer,
            RenderType.armorCutoutNoCull(this.texture.get(slot.index == 0 ? "left" : "right")),
            stack,
            0
        );
    }
}