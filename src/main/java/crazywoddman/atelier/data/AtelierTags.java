package crazywoddman.atelier.data;

import crazywoddman.atelier.Atelier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

public class AtelierTags {
    public static class Potions {
        public static final TagKey<Potion> GAS_FILTER = create("gas_filter");

        public static ITag<Potion> get(TagKey<Potion> tagkey) {
            return ForgeRegistries.POTIONS.tags().getTag(tagkey);
        }

        private static TagKey<Potion> create(String path) {
            return create(Atelier.MODID, path);
        }

        public static TagKey<Potion> create(String namespace, String path) {
            return ForgeRegistries.POTIONS.tags().createTagKey(ResourceLocation.fromNamespaceAndPath(namespace, path));
        }
    }
    public static class Items {
        public static final TagKey<Item> PLATE_CARRIERS = create("plate_carriers");
        public static final TagKey<Item> CAN_DETONATE = create("can_detonate");
        public static final TagKey<Item> CAN_DETONATE_FIRE = create("can_detonate_fire");
        public static final TagKey<Item> GAS_FILTERS = create("slots/gas_filter");
        public static final TagKey<Item> GASMASKS = create("gasmask");
        public static final TagKey<Item> TINTED_VISION = create("tinted_vision");
        public static final TagKey<Item> WARIUM_GASMASKS = create("crusty_chunks", "gasmask");
        public static final TagKey<Item> HOLSTER = create("holster");
        public static final TagKey<Item> SLING = create("sling");
        public static final TagKey<Item> NVD = create("nvd");
        public static final TagKey<Item> POUCH_BLACKLIST = create("pouch_blacklist");

        public static ITag<Item> get(TagKey<Item> tagkey) {
            return ForgeRegistries.ITEMS.tags().getTag(tagkey);
        }

        private static TagKey<Item> create(String path) {
            return create(Atelier.MODID, path);
        }

        public static TagKey<Item> create(String namespace, String path) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, path));
        }
    }
}
