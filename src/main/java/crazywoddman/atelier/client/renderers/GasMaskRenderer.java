package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.items.templates.AbstractGasMask;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class GasMaskRenderer extends SimpleAccessoryRenderer {

    public <T extends AbstractGasMask> GasMaskRenderer(T item) {
        super(new WearableTexture(item), ForgeRegistries.ITEMS.getKey(item));
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
        String eyesLevel = "_" + WearablesCapability.get(entity).map(cap -> cap.eyesLevel).orElse(WearablesCapability.DEFAULT_EYES_LEVEL);

        for (int i = 0; i < IDyeable.getDefaultColors(stack).length; i++) {
            render(
                pose,
                light,
                buffer,
                RenderType.entityTranslucent(this.texture.get(i + eyesLevel)),
                stack,
                i
            );
        }

        render(
            pose,
            light,
            buffer,
            RenderType.entityTranslucent(this.texture.get("overlay" + eyesLevel))
        );
    }
}