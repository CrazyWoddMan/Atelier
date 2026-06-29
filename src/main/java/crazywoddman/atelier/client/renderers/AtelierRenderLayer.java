package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.render.SimpleWearableRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AtelierRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final EquipmentSlot[] ARMOR = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public AtelierRenderLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(
        PoseStack pose,
        MultiBufferSource buffer,
        int light,
        T entity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float age,
        float headYaw,
        float headPitch
    ) {
        for (EquipmentSlot slot : ARMOR) {
            ItemStack parent = entity.getItemBySlot(slot);

            if (parent.isEmpty() || !IModular.isModular(parent.getItem()))
                continue;

            pose.pushPose();
            SimpleWearableRenderer.renderModules(
                parent,
                entity,
                SimpleSlot.of(slot),
                pose,
                getParentModel(),
                buffer,
                light
            );
            pose.popPose();
        }
    }
}
