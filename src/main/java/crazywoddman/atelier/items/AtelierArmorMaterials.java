package crazywoddman.atelier.items;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public enum AtelierArmorMaterials implements ArmorMaterial {
    PHANTOM_SILK(
        AtelierItems.PHANTOM_SILK.getId().getPath(),
        6,
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