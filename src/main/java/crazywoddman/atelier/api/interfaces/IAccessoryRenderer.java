package crazywoddman.atelier.api.interfaces;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.SimpleSlot;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IAccessoryRenderer {
    <M extends LivingEntity> void render(
        ItemStack stack,
        LivingEntity entity,
        SimpleSlot slot,
        PoseStack pose,
        EntityModel<M> model,
        MultiBufferSource buffer,
        int light
    );
}