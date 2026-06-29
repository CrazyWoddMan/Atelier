package crazywoddman.atelier.mixins;

import crazywoddman.atelier.items.AtelierItems;
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

    @Inject(
        method = "load",
        at = @At("RETURN"),
        cancellable = true
    )
    private void filterLoadedTags(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
        ResourceLocation[] toRemoveTags = {ResourceLocation.fromNamespaceAndPath("forge", "plates/iron"), ResourceLocation.fromNamespaceAndPath("forge", "plates/netherite")};
        ResourceLocation[] toRemoveItems = {AtelierItems.IRON_PLATE.getId(), AtelierItems.NETHERITE_PLATE.getId()};
        Map<ResourceLocation, List<TagLoader.EntryWithSource>> tags = cir.getReturnValue();

        for (int i = 0; i < toRemoveTags.length; i++) {
            List<TagLoader.EntryWithSource> entries = tags.get(toRemoveTags[i]);

            if (entries != null && entries.size() > 1) {
                ResourceLocation item = toRemoveItems[i];
                tags.put(toRemoveTags[i], entries.stream().filter(entry -> !entry.entry().getId().equals(item)).collect(Collectors.toList()));
            }
        }

        cir.setReturnValue(tags);
    }
}