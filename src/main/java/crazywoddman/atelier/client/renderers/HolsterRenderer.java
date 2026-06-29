package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.api.render.SimpleModuleRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HolsterRenderer extends SimpleModuleRenderer {

    public HolsterRenderer() {
        super(new WearableTexture(AtelierItems.HOLSTER.get()), AtelierItems.HOLSTER.getId(), false);
    }

    @Override
    public <M extends LivingEntity> void render(
        ItemStack stack,
        LivingEntity entity,
        SimpleSlot parent,
        SimpleSlot module,
        PoseStack pose,
        EntityModel<M> model,
        MultiBufferSource buffer,
        int light
    ) {
        render(
            pose,
            light,
            buffer,
            RenderType.armorCutoutNoCull(texture.get(0)),
            stack,
            0
        );
        
        if (!(stack.getItem() instanceof IQuickAccess accessible))
            return;

        ItemStack pistol = accessible.quickAccessPreview(stack);

        if (pistol.isEmpty())
            return;

        pose.translate(-0.1f, 1.05f, -0.13f);
        pose.mulPose(Axis.YP.rotationDegrees(90));
        pose.mulPose(Axis.XP.rotationDegrees(90));
        pose.scale(0.29f, 0.29f, 0.29f);
        Minecraft.getInstance().getItemRenderer().renderStatic(
            pistol,
            ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
            light,
            OverlayTexture.NO_OVERLAY,
            pose,
            buffer,
            entity.level(),
            0
        );
    }
}