package crazywoddman.atelier;

import crazywoddman.atelier.config.Config;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AtelierClientUtils {
    public static void playeDetonationSound(Player player) {
        Level level = player.level();
        boolean halal = Config.CLIENT.halalMode.get();
        Minecraft.getInstance().getSoundManager().play(
            player.isLocalPlayer()
            ? new SimpleSoundInstance(
                (halal ? AtelierSounds.HALAL : AtelierSounds.DETONATION).getId(),
                SoundSource.PLAYERS,
                1, 1,
                level.getRandom(),
                false,
                0,
                Attenuation.NONE,
                0, 0, 0,
                true
            ) : new EntityBoundSoundInstance(
                (halal ? AtelierSounds.HALAL : AtelierSounds.DETONATION).get(),
                SoundSource.PLAYERS,
                1,
                1,
                player,
                0
            )
        );
    }
    
    public static void playFilterSound(SlotReference reference) {
        Minecraft.getInstance().getSoundManager().play(new FilterBreathingSound(reference));
    }

    public static class FilterBreathingSound extends EntityBoundSoundInstance {
        private final SlotReference reference;

        public FilterBreathingSound(SlotReference reference) {
            super(
                AtelierSounds.GASMASK.get(),
                reference.entity().getSoundSource(),
                1,
                0.85f + reference.entity().getRandom().nextFloat() * 0.05f,
                reference.entity(),
                0
            );
            this.reference = reference;
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity entity = reference.entity();
            
            if (entity == null || !entity.isAlive() || this.reference.getStack().isEmpty() || !this.reference.capability().getContainer(new SlotTypeReference("face")).shouldRender(0) || !this.reference.slotContainer().shouldRender(this.reference.slot()) || entity.isUnderWater())
                stop();
        }
    }
}