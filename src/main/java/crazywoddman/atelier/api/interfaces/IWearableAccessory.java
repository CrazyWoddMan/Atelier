package crazywoddman.atelier.api.interfaces;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import crazywoddman.atelier.api.SimpleWearableRenderer;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IWearableAccessory extends IWearable, Accessory {
    
    default Supplier<AccessoryRenderer> getRenderer() {
        return () -> new SimpleWearableRenderer(getTextureKey(), getModelKey(), ref -> true);
    }

    @Override
    default int maxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    default void onEquip(ItemStack stack, SlotReference reference) {
        playSound(reference, getEquipSound());
    }

    @Override
    default void onUnequip(ItemStack stack, SlotReference reference) {
        playSound(reference, getUnequipSound());
    }

    public static void playSound(SlotReference reference, SoundEventData sound) {
        if (sound != null) {
            LivingEntity entity = reference.entity();
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound.event(), entity.getSoundSource(), sound.volume(), sound.pitch());
        }
    }

    @Nullable
    default SoundEventData getEquipSound() {
        return new SoundEventData(SoundEvents.ARMOR_EQUIP_LEATHER, 1, 1);
    }

    @Nullable
    default SoundEventData getUnequipSound() {
        SoundEventData sound = getEquipSound();
        return sound == null ? null : new SoundEventData(sound.event(), sound.volume(), sound.pitch() * 0.8f);
    }

    @Override
    default void onEquipFromUse(ItemStack stack, SlotReference reference) {}

    /**
    * For hats only!
    **/
    default boolean hideUnderHelmet() {
        return true;
    }
}