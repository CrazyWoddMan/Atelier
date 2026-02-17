package crazywoddman.atelier.items;

import java.util.Comparator;
import java.util.Map;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierTags;
import crazywoddman.atelier.api.WearablesRegister;
import crazywoddman.atelier.api.templates.SimpleItem;
import crazywoddman.atelier.api.templates.SimpleMask;
import crazywoddman.atelier.blocks.AtelierBlocks;
import crazywoddman.atelier.config.Config;
import crazywoddman.atelier.items.accessories.*;
import crazywoddman.atelier.items.armor.*;
import crazywoddman.atelier.items.simple.*;
import crazywoddman.atelier.recipes.AtelierRecipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AtelierItems {
    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
        SILENT_REGISTRY.register(bus);
        WEARABLES.register(bus);
        SILENT_WEARABLES.register(bus);
        CREATIVE_TABS.register(bus);
    }

    public final static DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Atelier.MODID);
    public final static DeferredRegister<Item> SILENT_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Atelier.MODID);
    public static final WearablesRegister WEARABLES = new WearablesRegister(REGISTRY);
    public static final WearablesRegister SILENT_WEARABLES = new WearablesRegister(SILENT_REGISTRY);

    public static final RegistryObject<Item> SEWING_TABLE = registerBlock(AtelierBlocks.SEWING_TABLE);
    public static final RegistryObject<Item> WILD_COTTON = registerBlock(AtelierBlocks.WILD_COTTON);
    public static final RegistryObject<Item> COTTON = REGISTRY.register("cotton", SimpleItem::new);
    public static final RegistryObject<Item> COTTON_SEEDS = REGISTRY.register("cotton_seeds", () -> new ItemNameBlockItem(AtelierBlocks.COTTON.get(), new Item.Properties()));
    public static final RegistryObject<Item> COTTON_SPOOL = REGISTRY.register("cotton_spool", SimpleItem::new);
    public static final RegistryObject<Item> FABRIC = WEARABLES.register("fabric", Fabric::new);
    public static final RegistryObject<Item> NETHERITE_PLATE = SILENT_REGISTRY.register("netherite_plate", SimpleItem::new);
    public static final RegistryObject<Item> IRON_PLATE = SILENT_WEARABLES.register("iron_plate", SimpleItem::new);
    public static final RegistryObject<Item> WOODEN_PLATE = SILENT_WEARABLES.register("wooden_plate", SimpleItem::new);
    public static final RegistryObject<Item> CERAMIC_PLATE = SILENT_WEARABLES.register("ceramic_plate", SimpleItem::new);
    public static final RegistryObject<Item> PHANTOM_SILK = REGISTRY.register("phantom_silk", SimpleItem::new);
    public static final RegistryObject<Item> BASE_FILTER = SILENT_WEARABLES.register("base_filter", BaseFilter::new);
    public static final RegistryObject<Item> ADVANCED_FILTER = SILENT_WEARABLES.register("advanced_filter", AdvancedFilter::new);
    public static final RegistryObject<Item> TACTICAL_HELMET_A = WEARABLES.register("tactical_helmet_a", TacticalHelmetA::new);
    public static final RegistryObject<Item> TACTICAL_HELMET_B = WEARABLES.register("tactical_helmet_b", TacticalHelmetB::new);
    public static final RegistryObject<Item> ARMOR_VEST_A = WEARABLES.register("armor_vest_a", ArmorVestA::new);
    public static final RegistryObject<Item> ARMOR_VEST_B = WEARABLES.register("armor_vest_b", ArmorVestB::new);
    public static final RegistryObject<Item> ARMOR_VEST_C = WEARABLES.register("armor_vest_c", ArmorVestC::new);
    public static final RegistryObject<Item> KNEEPADS = WEARABLES.register("kneepads", KneePads::new);
    public static final RegistryObject<Item> SERVICE_CAP = WEARABLES.register("service_cap", ServiceCap::new);
    public static final RegistryObject<Item> UNIFORM_HAT = WEARABLES.register("uniform_hat", UniformHat::new);
    public static final RegistryObject<Item> UNIFORM = WEARABLES.register("uniform", Uniform::new);
    public static final RegistryObject<Item> UNIFORM_PANTS = WEARABLES.register("uniform_pants", UniformPants::new);
    public static final RegistryObject<Item> UNIFORM_BOOTS = WEARABLES.register("uniform_boots", UniformBoots::new);
    public static final RegistryObject<Item> GLOVE = WEARABLES.register("leather_glove", Glove::new);
    public static final RegistryObject<Item> BELT = WEARABLES.register("belt", Belt::new);
    public static final RegistryObject<Item> POUCH = WEARABLES.register("pouch", Pouch::new);
    public static final RegistryObject<Item> DETONATOR = SILENT_REGISTRY.register("detonator", Detonator::new);
    public static final RegistryObject<Item> BANDAGE = SILENT_WEARABLES.register("bandage", Bandage::new);
    public static final RegistryObject<Item> GASMASK = WEARABLES.register("gasmask", GasMask::new);
    public static final RegistryObject<Item> HALFMASK = WEARABLES.register("halfmask", HalfMask::new);
    public static final RegistryObject<Item> USHANKA = WEARABLES.register("ushanka", Ushanka::new);
    public static final RegistryObject<Item> CIGARETTE_PACK = REGISTRY.register("cigarette_pack", CigarettePack::new);
    public static final RegistryObject<Item> CIGARETTE = WEARABLES.register("cigarette", Cigarette::new);
    public static final RegistryObject<Item> BALACLAVA = WEARABLES.register("balaclava", () -> new SimpleMask(1908001));
    public static final RegistryObject<Item> SKIMASK = WEARABLES.register("skimask", () -> new SimpleMask(1908001));
    public static final RegistryObject<Item> BANDANA = SILENT_WEARABLES.register("face_bandana", Bandana::new);

    private static RegistryObject<Item> registerBlock(RegistryObject<Block> block) {
        return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Atelier.MODID);
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register(Atelier.MODID, () -> CreativeModeTab
        .builder()
        .title(Component.translatable("itemGroup." + Atelier.MODID))
        .icon(() -> new ItemStack(AtelierItems.SEWING_TABLE.get()))
        .displayItems((parameters, output) -> {
            for (RegistryObject<Item> item : AtelierItems.REGISTRY.getEntries())
                output.accept(item.get());

            if (Config.SERVER.bombVest.get() != Detonator.ExplosionType.DISABLE)
                output.accept(AtelierItems.DETONATOR.get());

            ItemStack[] filters = {new ItemStack(AtelierItems.BASE_FILTER.get()), new ItemStack(AtelierItems.ADVANCED_FILTER.get())};

            AtelierRecipes.PLATE_ITEMS
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(entry -> entry.getValue().protection))
            .map(Map.Entry::getKey)
            .forEach(output::accept);
            
            for (ItemStack genericFilter : filters) {
                ItemStack creativeFilter = genericFilter.copy();
                creativeFilter.getOrCreateTag().putBoolean("isCreative", true);
                output.accept(creativeFilter);
                output.accept(genericFilter);

                for (Potion potion : AtelierTags.Potions.get(AtelierTags.Potions.GAS_FILTER)) {
                    ItemStack filter = genericFilter.copy();
                    ListTag effects = new ListTag();

                    for (MobEffectInstance effect : potion.getEffects())
                        effects.add(StringTag.valueOf(ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect()).toString()));

                    filter.getOrCreateTag().put("effects", effects);
                    output.accept(filter);
                }
            }
        })
        .build()
    );
}
