package crazywoddman.atelier.items;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.api.templates.SimpleDyeable;
import crazywoddman.atelier.api.templates.SimpleItem;
import crazywoddman.atelier.blocks.AtelierBlocks;
import crazywoddman.atelier.data.ArmorPlates;
import crazywoddman.atelier.items.accessories.*;
import crazywoddman.atelier.items.armor.*;
import crazywoddman.atelier.items.modules.AdvancedFilter;
import crazywoddman.atelier.items.modules.BaseFilter;
import crazywoddman.atelier.items.modules.CigarettePack;
import crazywoddman.atelier.items.modules.Holster;
import crazywoddman.atelier.items.modules.Pouch;
import crazywoddman.atelier.items.simple.*;
import crazywoddman.atelier.items.templates.FilterItem;
import crazywoddman.atelier.items.templates.SimpleMask;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        CREATIVE_TABS.register(bus);
    }

    public static final Set<Item> QUICK_ACCESS = new HashSet<>();

    private final static DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Atelier.MODID);
    private final static DeferredRegister<Item> SILENT_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Atelier.MODID);

    public static final RegistryObject<Item> SEWING_TABLE = registerBlock(AtelierBlocks.SEWING_TABLE);

    public static final RegistryObject<Item> WILD_COTTON = registerBlock(AtelierBlocks.WILD_COTTON);
    public static final RegistryObject<Item> COTTON = REGISTRY.register("cotton", SimpleItem::new);
    public static final RegistryObject<Item> COTTON_SEEDS = REGISTRY.register("cotton_seeds", () -> new ItemNameBlockItem(AtelierBlocks.COTTON.get(), new Item.Properties()));
    public static final RegistryObject<Item> COTTON_SPOOL = REGISTRY.register("cotton_spool", SimpleItem::new);
    public static final RegistryObject<Item> FABRIC = REGISTRY.register("fabric", () -> new SimpleDyeable(IDyeable.WHITE));
    public static final RegistryObject<Item> PHANTOM_CLOTH = REGISTRY.register("phantom_cloth", () -> new SimpleDyeable(IDyeable.PHANTOM_CLOTH));
    public static final RegistryObject<Item> BIOPOLYMER = REGISTRY.register("biopolymer", SimpleItem::new);
    public static final RegistryObject<Item> BIOPOLYSTER = REGISTRY.register("biopolyster", () -> new SimpleDyeable(IDyeable.BIOPOLYMER));
    public static final RegistryObject<Item> BIOPLASTIC = REGISTRY.register("bioplastic", () -> new SimpleDyeable(IDyeable.BIOPOLYMER));

    public static final RegistryObject<Item> NETHERITE_PLATE = SILENT_REGISTRY.register("netherite_plate", SimpleItem::new);
    public static final RegistryObject<Item> WOODEN_PLATE = SILENT_REGISTRY.register("wooden_plate", SimpleItem::new);
    public static final RegistryObject<Item> CERAMIC_PLATE = SILENT_REGISTRY.register("ceramic_plate", SimpleItem::new);

    public static final RegistryObject<Item> TACTICAL_HELMET_A = REGISTRY.register("tactical_helmet_a", TacticalHelmetA::new);
    public static final RegistryObject<Item> TACTICAL_HELMET_B = REGISTRY.register("tactical_helmet_b", TacticalHelmetB::new);
    public static final RegistryObject<Item> TACTICAL_HELMET_C = REGISTRY.register("tactical_helmet_c", TacticalHelmetC::new);

    public static final RegistryObject<Item> ARMOR_VEST_A = REGISTRY.register("armor_vest_a", ArmorVestA::new);
    public static final RegistryObject<Item> ARMOR_VEST_B = REGISTRY.register("armor_vest_b", ArmorVestB::new);
    public static final RegistryObject<Item> ARMOR_VEST_C = REGISTRY.register("armor_vest_c", ArmorVestC::new);
    public static final RegistryObject<Item> WEBBING = REGISTRY.register("webbing", Webbing::new);

    public static final RegistryObject<Item> KNEEPADS = REGISTRY.register("kneepads", KneePads::new);
    public static final RegistryObject<Item> ELBOW_PAD = REGISTRY.register("elbowpad", ElbowPad::new);

    public static final RegistryObject<Item> TACTICAL_BOOTS = REGISTRY.register("tactical_boots", TacticalBoots::new);

    public static final RegistryObject<Item> USHANKA = REGISTRY.register("ushanka", Ushanka::new);
    public static final RegistryObject<Item> PAPAKHA = REGISTRY.register("papakha", Papakha::new);
    public static final RegistryObject<Item> BERET = REGISTRY.register("beret", Beret::new);
    public static final RegistryObject<Item> PEAKED_CAP = REGISTRY.register("peaked_cap", PeakedCap::new);
    public static final RegistryObject<Item> UNION_KEPI = REGISTRY.register("union_kepi", UnionKepi::new);
    public static final RegistryObject<Item> UTILITY_CAP = REGISTRY.register("utility_cap", UtilityCap::new);
    public static final RegistryObject<Item> COWBOY_HAT = REGISTRY.register("cowboy_hat", CowboyHat::new);
    public static final RegistryObject<Item> PORK_PIE = REGISTRY.register("pork_pie", PorkPie::new);
    public static final RegistryObject<Item> NIGHT_VISION_BINUCULARS = REGISTRY.register("nvb", NightVisionBinoculars::new);
    public static final RegistryObject<Item> NIGHT_VISION_MONOCULAR = REGISTRY.register("nvbo", NightVisionBiocular::new);

    public static final RegistryObject<Item> GASMASK_A = REGISTRY.register("gasmask_a", GasMaskA::new);
    public static final RegistryObject<Item> GASMASK_B = REGISTRY.register("gasmask_b", GasMaskB::new);
    public static final RegistryObject<Item> GASMASK_C = REGISTRY.register("gasmask_c", GasMaskC::new);
    public static final RegistryObject<Item> BALACLAVA = REGISTRY.register("balaclava", SimpleMask::new);
    public static final RegistryObject<Item> SKIMASK = REGISTRY.register("skimask", SimpleMask::new);
    public static final RegistryObject<Item> WELDING_GOGGLES = REGISTRY.register("welding_goggles", WeldingGoggles::new);
    public static final RegistryObject<Item> EYE_PATCH = REGISTRY.register("eye_patch", EyePatch::new);

    public static final RegistryObject<Item> MILITARY_TUNIC = REGISTRY.register("military_tunic", MilitaryTunic::new);
    public static final RegistryObject<Item> FIELD_JACKET = REGISTRY.register("field_jacket", FieldJacket::new);
    public static final RegistryObject<Item> TANK_TOP = REGISTRY.register("tank_top", TankTop::new);

    public static final RegistryObject<Item> BANDAGE = REGISTRY.register("bandage", Bandage::new);
    public static final RegistryObject<Item> LEATHER_GLOVE = REGISTRY.register("leather_glove", LeatherGlove::new);

    public static final RegistryObject<Item> SLING = REGISTRY.register("sling", Sling::new);
    public static final RegistryObject<Item> BELT = REGISTRY.register("belt", Belt::new);
    public static final RegistryObject<Item> POUCH = REGISTRY.register("pouch", Pouch::new);
    public static final RegistryObject<Item> HOLSTER = REGISTRY.register("holster", Holster::new);

    public static final RegistryObject<Item> PANTS = REGISTRY.register("pants", UniformPants::new);

    public static final RegistryObject<Item> HAZMAT = REGISTRY.register("hazmat", Hazmat::new);

    public static final RegistryObject<Item> CIGARETTE = REGISTRY.register("cigarette", Cigarette::new);
    public static final RegistryObject<Item> CIGARETTE_PACK = REGISTRY.register("cigarette_pack", CigarettePack::new);

    public static final RegistryObject<Item> BASE_FILTER = SILENT_REGISTRY.register("base_filter", BaseFilter::new);
    public static final RegistryObject<Item> ADVANCED_FILTER = SILENT_REGISTRY.register("advanced_filter", AdvancedFilter::new);
    public static final RegistryObject<Item> DETONATOR = SILENT_REGISTRY.register("detonator", Detonator::new);

    static {
        if (!Atelier.CREATE_LOADED)
            SILENT_REGISTRY.register("iron_plate", SimpleItem::new);
    }

    private static RegistryObject<Item> registerBlock(RegistryObject<Block> block) {
        return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Atelier.MODID);
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register(Atelier.MODID, () -> CreativeModeTab
        .builder()
        .title(Component.translatable("itemGroup." + Atelier.MODID))
        .icon(() -> new ItemStack(AtelierItems.SEWING_TABLE.get()))
        .displayItems((parameters, output) -> {
            for (RegistryObject<Item> object : AtelierItems.REGISTRY.getEntries()) {
                Item item = object.get();
                ItemStack stack = new ItemStack(item);

                if (stack.is(AtelierTags.Items.NVD)) {
                    ListTag list = new ListTag();
                    list.add(new ItemStack(Items.REDSTONE, 64).save(new CompoundTag()));
                    stack.getOrCreateTag().put("Items", list);
                }

                output.accept(stack);
            }

            if (AtelierConfig.Server.BOMBVEST_MODE.get() != Detonator.ExplosionType.DISABLE)
                output.accept(AtelierItems.DETONATOR.get());

            ItemStack[] filters = {new ItemStack(AtelierItems.BASE_FILTER.get()), new ItemStack(AtelierItems.ADVANCED_FILTER.get())};

            ArmorPlates.CACHE
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(entry -> entry.getValue().protection))
            .map(Map.Entry::getKey)
            .forEach(output::accept);
            
            for (ItemStack genericFilter : filters) {
                ItemStack creativeFilter = genericFilter.copy();
                creativeFilter.getOrCreateTag().putBoolean(FilterItem.CREATIVE_TAG, true);
                output.accept(creativeFilter);
                output.accept(genericFilter);

                for (Potion potion : AtelierTags.Potions.get(AtelierTags.Potions.GAS_FILTER)) {
                    ItemStack filter = genericFilter.copy();
                    ListTag effects = new ListTag();

                    for (MobEffectInstance effect : potion.getEffects())
                        effects.add(StringTag.valueOf(ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect()).toString()));

                    filter.getOrCreateTag().put(FilterItem.EFFECTS_TAG, effects);
                    output.accept(filter);
                }
            }
        })
        .build()
    );
}
