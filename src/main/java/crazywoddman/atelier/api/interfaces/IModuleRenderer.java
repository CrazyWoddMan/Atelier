package crazywoddman.atelier.api.interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import crazywoddman.atelier.api.SimpleSlot;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IModuleRenderer {
    static final Map<Item, IModuleRenderer> CACHE = new HashMap<>();
    
    static Optional<IModuleRenderer> get(Item item) {
        if (item instanceof IModule module) {
            IModuleRenderer renderer = CACHE.get(item);

            if (renderer == null) {
                renderer = module.getRenderer();
                CACHE.put(item, renderer);
            }

            return Optional.of(renderer);
        }
        return Optional.empty();
    }

    <M extends LivingEntity> void render(
        ItemStack stack,
        LivingEntity entity,
        SimpleSlot parent,
        SimpleSlot module,
        PoseStack pose,
        EntityModel<M> model,
        MultiBufferSource buffer,
        int light
    );

    static <M extends LivingEntity> boolean tryRender(
        ItemStack stack,
        LivingEntity entity,
        SimpleSlot parent,
        SimpleSlot module,
        PoseStack pose,
        EntityModel<M> model,
        MultiBufferSource buffer,
        int light
    ) {
        return get(stack.getItem()).map(renderer -> {
            renderer.render(
                stack,
                entity,
                parent,
                module,
                pose,
                model,
                buffer,
                light
            );
            return true;
        }).orElse(false);
    }
}