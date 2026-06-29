package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BandageRenderer extends SimpleAccessoryRenderer {
    
    public BandageRenderer() {
        super(new WearableTexture(Atelier.MODID, "bandage"), AtelierItems.BANDAGE.getId());
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
            RenderType.armorCutoutNoCull(this.texture.get(switch (slot.name) {
                case IWearableAccessory.ARM -> slot.index == 0 ? "left_arm" : "right_arm";
                case IWearableAccessory.FACE -> slot.name + '_' + WearablesCapability.get(entity).map(cap -> cap.eyesLevel >= 4 ? 4 : 3).orElse((int)WearablesCapability.DEFAULT_EYES_LEVEL);
                default -> slot.name;
            })),
            stack,
            0
        );
    }
}