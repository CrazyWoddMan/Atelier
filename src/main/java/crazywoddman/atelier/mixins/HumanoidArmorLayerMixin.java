package crazywoddman.atelier.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.interfaces.IDyeable;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

@Mixin({HumanoidArmorLayer.class})
public class HumanoidArmorLayerMixin {
    @Shadow
    private void renderModel(PoseStack pose, MultiBufferSource buffer, int i, ArmorItem item, Model model, boolean flag, float r, float g, float b, ResourceLocation texture) {}
    
    @Redirect(
        method = "renderArmorPiece",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(" +
                "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                "I" +
                "Lnet/minecraft/world/item/ArmorItem;" +
                "Lnet/minecraft/client/model/Model;" +
                "Z" +
                "F" +
                "F" +
                "F" +
                "Lnet/minecraft/resources/ResourceLocation;" +
            ")V",
            ordinal = 0
        )
    )
    private void injectColors(
        HumanoidArmorLayer<?, ?, ?> layer,
        PoseStack pose,
        MultiBufferSource buffer,
        int i,
        ArmorItem item,
        Model model,
        boolean flag,
        float r, float g, float b,
        ResourceLocation texture,
        PoseStack pose1,
        MultiBufferSource buffer1,
        LivingEntity entity,
        EquipmentSlot slot
    ) {
        if (item instanceof IDyeable dyeable) {
            ItemStack stack = entity.getItemBySlot(slot);
            for (int color = 0; color < dyeable.getDefaultColors().length; color++) {
                float[] rgb = IDyeable.getFloatColor(stack, color);
                renderModel(pose, buffer, i, item, model, flag, rgb[0], rgb[1], rgb[2], layer.getArmorResource(entity, stack, slot, Integer.toString(color)));
            }
        } else renderModel(pose, buffer, i, item, model, flag, r, g, b, texture);
    }
}
