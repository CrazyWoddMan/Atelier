package crazywoddman.atelier.api.render;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IModuleRenderer;
import crazywoddman.atelier.client.WearableTexture;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
public class SimpleModuleRenderer extends SimpleWearableRenderer implements IModuleRenderer {
    protected final boolean followBody;

    public SimpleModuleRenderer(WearableTexture texture, ResourceLocation layer, boolean followBody) {
        super(texture, layer);
        this.followBody = followBody;
    }

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
        if (this.followBody)
            followBodyRotations(entity, this.model);

        renderHumanoidModel(stack, pose, buffer, light);
    }
}