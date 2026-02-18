package crazywoddman.atelier.mixins;

import crazywoddman.atelier.Atelier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(TagLoader.class)
public class TagLoaderMixin {
    private static final ResourceLocation TAG = ResourceLocation.fromNamespaceAndPath("forge", "plates/iron");
    private static final ResourceLocation PREDICATE = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "predicate");
    private static final ResourceLocation IRON_PLATE = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "iron_plate");
    
    @Inject(
        method = "load",
        at = @At("RETURN"),
        cancellable = true
    )
    private void filterLoadedTags(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
        Map<ResourceLocation, List<TagLoader.EntryWithSource>> tags = cir.getReturnValue();
        List<TagLoader.EntryWithSource> entries = tags.get(TAG);

        if (entries == null || entries.size() < 2)
            return;
        
        tags.put(TAG, entries.stream().filter(entry -> {
            if (entry.entry().getId().equals(IRON_PLATE)) {
                tags.remove(PREDICATE);
                return false;
            }

            return true;
        }).collect(Collectors.toList()));

        cir.setReturnValue(tags);
    }
}