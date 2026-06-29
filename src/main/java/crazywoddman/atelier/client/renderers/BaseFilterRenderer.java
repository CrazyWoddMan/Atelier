package crazywoddman.atelier.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IModuleRenderer;
import crazywoddman.atelier.api.render.SimpleWearableRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BaseFilterRenderer extends SimpleWearableRenderer implements IModuleRenderer {

    public BaseFilterRenderer() {
        super( new WearableTexture(AtelierItems.BASE_FILTER.getId()), AtelierItems.BASE_FILTER.getId());
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
        if (!CompatHelper.shouldRender(entity, parent))
            return;
        
        followBodyRotations(entity, this.model);
        ItemStack gasmask = CompatHelper.getSlotContainer(entity, parent.name).get().getItem(parent.index);
        render(
            pose,
            light,
            buffer,
            RenderType.armorCutoutNoCull(this.texture.get(
                gasmask.is(AtelierItems.GASMASK_A.get())
                ? "a"
                : ((gasmask.is(AtelierItems.GASMASK_B.get()) ? 'b' : 'c') + (module.index == 0 ? "_right" : "_left"))
            )),
            stack,
            0
        );
    }
}