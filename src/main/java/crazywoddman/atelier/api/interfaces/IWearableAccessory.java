package crazywoddman.atelier.api.interfaces;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IWearableAccessory extends IWearable {
    static final String
    HAT = Atelier.ACCESSORIES_LOADED ? "hat" : "head",
    FACE = "face",
    BODY = "body",
    SUIT = "suit",
    ARM = "arm",
    HAND = Atelier.ACCESSORIES_LOADED ? "hand" : "hands",
    BELT = "belt",
    LEGS = "legs";
    
    default Supplier<IAccessoryRenderer> getRenderer() {
        return () -> new SimpleAccessoryRenderer(getTexture(), getLayerKey());
    }

    default void onEquip(ItemStack stack, LivingEntity entity, SimpleSlot slot) {
        equipSound().playEquip(entity, null);
    }

    default void onUnequip(ItemStack stack, LivingEntity entity, SimpleSlot slot) {
        equipSound().playUnequip(entity, null);
    }

    default void wearTick(ItemStack stack, LivingEntity entity, SimpleSlot slot) {}

    default boolean canEquipFromUse(ItemStack stack) {
        return true;
    }

    /**
    * For hats only!
    **/
    default boolean hideUnderHelmet() {
        return true;
    }
}