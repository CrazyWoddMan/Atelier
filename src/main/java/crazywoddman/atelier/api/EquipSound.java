package crazywoddman.atelier.api;

import org.jetbrains.annotations.Nullable;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EquipSound {
    public static final EquipSound
    LEATHER = new EquipSound(SoundEvents.ARMOR_EQUIP_LEATHER),
    BUNDLE = new EquipSound(SoundEvents.BUNDLE_INSERT, SoundEvents.BUNDLE_REMOVE_ONE),
    GENERIC = new EquipSound(SoundEvents.ARMOR_EQUIP_GENERIC),
    CHAIN = new EquipSound(SoundEvents.ARMOR_EQUIP_CHAIN),
    NETHERITE = new EquipSound(SoundEvents.ARMOR_EQUIP_NETHERITE),
    GOLD = new EquipSound(SoundEvents.ARMOR_EQUIP_GOLD),
    IRON = new EquipSound(SoundEvents.ARMOR_EQUIP_IRON);

    public final SoundEvent equip, unequip;

    public EquipSound(SoundEvent equip, SoundEvent unequip) {
        this.equip = equip;
        this.unequip = unequip;
    }

    public EquipSound(SoundEvent sound) {
        this(sound, sound);
    }

    public void playEquip(Entity entity, @Nullable Player localPlayer) {
        play(entity, localPlayer, this.equip, 1, 1);
    }

    public void playUnequip(Entity entity, @Nullable Player localPlayer) {
        play(entity, localPlayer, this.unequip, 1, 0.8f);
    }

    public static void play(Entity entity, @Nullable Player localPlayer, SoundEvent sound, float volume, float pitch) {
        entity.level().playSound(
            localPlayer,
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            sound,
            entity.getSoundSource(),
            volume,
            pitch
        );
    }
}