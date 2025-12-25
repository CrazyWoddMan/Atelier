package crazywoddman.atelier.items;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.WearablesRegister;
import crazywoddman.atelier.api.templates.DyableArmor;
import crazywoddman.atelier.api.templates.SimpleItem;
import crazywoddman.atelier.blocks.AtelierBlocks;
import crazywoddman.atelier.models.ArmorVestAModel;
import crazywoddman.atelier.models.ArmorVestBModel;
import crazywoddman.atelier.models.ArmorVestCModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
        () -> new DyableArmor(
            AtelierArmorMaterials.PHANTOM_SILK,
            ArmorItem.Type.CHESTPLATE,
            new Item.Properties(),
            8618876,
            FMLEnvironment.dist.isClient() ? ArmorVestAModel::createLayer : null,
            FMLEnvironment.dist.isClient() ? ArmorVestAModel.LAYER_LOCATION : null,
            Atelier.MODID + ":textures/models/wearable/armor_vest_a.png"
        )
    );
    public static final RegistryObject<Item> ARMOR_VEST_B = WEARABLES.register(
        "armor_vest_b",
        () -> new DyableArmor(
            AtelierArmorMaterials.PHANTOM_SILK,
            ArmorItem.Type.CHESTPLATE,
            new Item.Properties(),
            8618876,
            FMLEnvironment.dist.isClient() ? ArmorVestBModel::createLayer : null,
            FMLEnvironment.dist.isClient() ? ArmorVestBModel.LAYER_LOCATION : null,
            Atelier.MODID + ":textures/models/wearable/armor_vest_b.png"
        )
    );
    public static final RegistryObject<Item> ARMOR_VEST_C = WEARABLES.register(
        "armor_vest_c",
        () -> new DyableArmor(
            AtelierArmorMaterials.PHANTOM_SILK,
            ArmorItem.Type.CHESTPLATE,
            new Item.Properties(),
            8618876,
            FMLEnvironment.dist.isClient() ? ArmorVestCModel::createLayer : null,
            FMLEnvironment.dist.isClient() ? ArmorVestCModel.LAYER_LOCATION : null,
            Atelier.MODID + ":textures/models/wearable/armor_vest_c.png"
        )
    );
    public static final RegistryObject<Item> BELT = WEARABLES.register(
        "belt",
        () -> new Belt(new Item.Properties().stacksTo(4), 8606770)
    );
    public static final RegistryObject<Item> POUCH = WEARABLES.register(
        "pouch",
        () -> new Pouch(new Item.Properties().stacksTo(4), 8606770)
    );
    public static final RegistryObject<Item> ARMBAND = WEARABLES.register(
        "armband",
        () -> new Armband(new Item.Properties().stacksTo(16), 16777215)
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

    public enum AtelierArmorMaterials implements ArmorMaterial {
        PHANTOM_SILK(
            AtelierItems.PHANTOM_SILK.getId().getPath(),
            15,
            new int[]{1, 3, 4, 1}, // Protection: boots, leggings, chestplate, helmmet
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            () -> Ingredient.of(AtelierItems.PHANTOM_SILK.get())
        );

        private static final int[] DURABILITY_MULTIPLIERS = new int[]{13, 15, 16, 11};
        
        private final String name;
        private final int durabilityMultiplier;
        private final int[] protection;
        private final int enchantmentValue;
        private final SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;
        private final Supplier<Ingredient> repairIngredient;

        AtelierArmorMaterials(
            String name, 
            int durabilityMultiplier, 
            int[] protection,
            int enchantmentValue,
            SoundEvent equipSound, 
            float toughness, 
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient
        ) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.protection = protection;
            this.enchantmentValue = enchantmentValue;
            this.equipSound = equipSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.repairIngredient = repairIngredient;
        }

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return DURABILITY_MULTIPLIERS[type.ordinal()] * this.durabilityMultiplier;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return this.protection[type.ordinal()];
        }

        @Override
        public int getEnchantmentValue() {
            return this.enchantmentValue;
        }

        @Override
        public SoundEvent getEquipSound() {
            return this.equipSound;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return this.repairIngredient.get();
        }

        @Override
        public String getName() {
            return Atelier.MODID + ":" + this.name;
        }

        @Override
        public float getToughness() {
            return this.toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return this.knockbackResistance;
        }
    }
}
