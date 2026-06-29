package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.data.WearablesCapability;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class NightVisionRenderer extends SimpleAccessoryRenderer {

    public NightVisionRenderer(Item item) {
        super(new WearableTexture(Atelier.MODID, "nvd"), ForgeRegistries.ITEMS.getKey(item));
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
        String texture =  cap.nvdActive ? "down_" + Math.min(4, cap.eyesLevel) : "up";

        render(
            pose,
            light,
            buffer,
            RenderType.armorCutoutNoCull(this.texture.get(texture)),
            stack,
            0
        );
        render(
            pose,
            cap.nvdActive ? LightTexture.FULL_BRIGHT : light,
            buffer,
            RenderType.armorCutoutNoCull(this.texture.get("overlay_" + texture))
        );
        
        if (entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            render(
                pose,
                light,
                buffer,
                RenderType.armorCutoutNoCull(this.texture.get("straps")),
                stack,
                1
            );
        }
    }
}