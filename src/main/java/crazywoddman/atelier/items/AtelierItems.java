package crazywoddman.atelier.items;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.WearablesRegister;
import crazywoddman.atelier.api.templates.SimpleItem;
import crazywoddman.atelier.blocks.AtelierBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.ITag;

public class AtelierItems {
    public final static DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Atelier.MODID);
    public static final WearablesRegister WEARABLES = new WearablesRegister(REGISTRY);

    public static final RegistryObject<Item> SEWING_TABLE = registerBlock(AtelierBlocks.SEWING_TABLE);
    public static final RegistryObject<Item> NETHERITE_PLATE = REGISTRY.register(
        "netherite_plate",
        SimpleItem::new
    );
    public static final RegistryObject<Item> CERAMIC_PLATE = REGISTRY.register(
        "ceramic_plate",
        SimpleItem::new
    );
    public static final RegistryObject<Item> IRON_PLATE = REGISTRY.register(
        "iron_plate",
        SimpleItem::new
    );
    public static final RegistryObject<Item> WOODEN_PLATE = REGISTRY.register(
        "wooden_plate",
        SimpleItem::new
    );
    public static final RegistryObject<Item> PHANTOM_SILK = REGISTRY.register(
        "phantom_silk",
        SimpleItem::new
    );
    public static final RegistryObject<Item> ARMOR_VEST_A = WEARABLES.register(
        "armor_vest_a",
        ArmorVestA::new
    );
    public static final RegistryObject<Item> ARMOR_VEST_B = WEARABLES.register(
        "armor_vest_b",
        ArmorVestB::new
    );
    public static final RegistryObject<Item> ARMOR_VEST_C = WEARABLES.register(
        "armor_vest_c",
        ArmorVestC::new
    );
    public static final RegistryObject<Item> BELT = WEARABLES.register(
        "belt",
        Belt::new
    );
    public static final RegistryObject<Item> POUCH = WEARABLES.register(
        "pouch",
        Pouch::new
    );
    public static final RegistryObject<Item> ARMBAND = WEARABLES.register(
        "armband",
        Armband::new
    );

    private static RegistryObject<Item> registerBlock(RegistryObject<Block> block) {
        return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Atelier.MODID);
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register(
        Atelier.MODID,
        () -> CreativeModeTab
            .builder()
            .title(Component.translatable("itemGroup." + Atelier.MODID))
            .icon(() -> new ItemStack(AtelierItems.ARMOR_VEST_A.get()))
            .displayItems((parameters, output) -> {
                output.accept(AtelierItems.SEWING_TABLE.get());
            })
            .build()
    );

    public static class Tags {
        public static final TagKey<Item> PLATE_CARRIERS = tag("plate_carriers");

        public static ITag<Item> get(TagKey<Item> tagkey) {
            return ForgeRegistries.ITEMS.tags().getTag(tagkey);
        }

        private static TagKey<Item> tag(String path) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Atelier.MODID, path));
        }
    }
}
