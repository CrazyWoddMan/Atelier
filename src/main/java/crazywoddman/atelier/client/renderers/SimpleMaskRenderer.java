package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.items.templates.SimpleMask;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SimpleMaskRenderer extends SimpleAccessoryRenderer {

    public <T extends SimpleMask> SimpleMaskRenderer(T item) {
        super(new WearableTexture(item), ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "mask"));
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
            RenderType.armorCutoutNoCull(this.texture.get(
                WearablesCapability.get(entity).map(cap -> cap.eyesLevel).orElse(WearablesCapability.DEFAULT_EYES_LEVEL)
            )),
            stack,
            0
        );
    }
}