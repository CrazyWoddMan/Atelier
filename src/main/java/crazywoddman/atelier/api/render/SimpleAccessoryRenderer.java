package crazywoddman.atelier.api.render;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.client.WearableTexture;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SimpleAccessoryRenderer extends SimpleWearableRenderer implements IAccessoryRenderer {

    public SimpleAccessoryRenderer(WearableTexture texture, ResourceLocation layer) {
        super(texture, layer);
    }

    protected boolean shouldRender(LivingEntity entity, SimpleSlot slot, ItemStack stack) {
        String name = slot.name;

        if (name.equals(IWearableAccessory.HAT)) {
            return !((IWearableAccessory)stack.getItem()).hideUnderHelmet() || entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
        } else if (name.equals(IWearableAccessory.BODY) || name.equals(IWearableAccessory.LEGS)) {
            return CompatHelper.getSlotContainer(entity, IWearableAccessory.SUIT).map(Container::isEmpty).orElse(true)
               || !CompatHelper.shouldRender(entity, SimpleSlot.of(IWearableAccessory.SUIT, 0));
        }

        return true;
    }

    @Override
    public <M extends LivingEntity> void render(
        ItemStack stack,
        LivingEntity entity,
        SimpleSlot slot,
        PoseStack pose,
        EntityModel<M> model,
        MultiBufferSource buffer,
        int light
    ) {
        if (!shouldRender(entity, slot, stack))
            return;

        followBodyRotations(entity, this.model);
        renderModel(stack, entity, slot, pose, buffer, light);
        renderModules(stack, entity, slot, pose, model, buffer, light);
    }

    protected void renderModel(
        ItemStack stack,
        LivingEntity entity,
        SimpleSlot slot,
        PoseStack pose,
        MultiBufferSource buffer,
        int light
    ) {
        renderHumanoidModel(stack, pose, buffer, light);
    }
}