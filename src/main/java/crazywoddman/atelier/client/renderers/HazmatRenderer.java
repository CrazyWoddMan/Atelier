package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class HazmatRenderer extends SimpleAccessoryRenderer {
    
    public HazmatRenderer() {
        super(new WearableTexture(AtelierItems.HAZMAT.getId()), AtelierItems.HAZMAT.getId());
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
                CompatHelper.getSlotContainer(entity, IWearableAccessory.FACE).map(face -> {
                    for (int i = 0; i < face.getContainerSize(); i++)
                        if (face.getItem(i).is(AtelierTags.Items.GASMASKS))
                            return true;
                    return false;
                }).orElse(false)
                ? "hood_up" : "hood_down")
            ),
            stack,
            0
        );
        render(pose, light, buffer, RenderType.armorCutoutNoCull(this.texture.get("overlay")));
    }
}