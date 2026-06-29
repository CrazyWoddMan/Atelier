package crazywoddman.atelier.compat.accessories;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

class AccessoryRendererImpl implements AccessoryRenderer {
    private final IAccessoryRenderer renderer;

    AccessoryRendererImpl(Supplier<IAccessoryRenderer> renderer) {
        this.renderer = renderer.get();
    }

    @Override
    public <M extends LivingEntity> void render(
        ItemStack stack,
        SlotReference reference,
        PoseStack pose,
        EntityModel<M> model,
        MultiBufferSource buffer,
        int light,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float age,
        float HeadYaw,
        float headPitch
    ) {
        renderer.render(stack, reference.entity(), SimpleSlot.of(reference.slotName(), reference.slot()), pose, model, buffer, light);
    }
}
