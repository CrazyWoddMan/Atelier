package crazywoddman.atelier.mixins;

import org.spongepowered.asm.mixin.Mixin;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.effects.FakeNightVisionAccessor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements FakeNightVisionAccessor {
    private final MobEffectInstance fakeNightVision = new MobEffectInstance(MobEffects.NIGHT_VISION) {
        private float lastFullness = -1;
        private int lastTick = 0;
        
        @Override
        public int getDuration() {
            if (AtelierConfig.Server.NVD_CONSUME.get() != 0) {
                LocalPlayer player = (LocalPlayer)(Object)LocalPlayerMixin.this;
                
                if (!player.isCreative() && !player.isSpectator()) {
                    return CompatHelper
                        .getStackInSlot(player, SimpleSlot.of(IWearableAccessory.HAT, 0))
                        .map(IContainerItem::getFullness)
                        .map(fullness -> {
                            if (fullness != this.lastFullness) {
                                this.lastFullness = fullness;
                                this.lastTick = player.tickCount;
                            }
                            return Math.max(1, (int)(fullness * 64) * AtelierConfig.Server.NVD_CONSUME.get() - player.tickCount + this.lastTick);
                        })
                        .orElse(1);
                }
            }

            return INFINITE_DURATION;
        }

        @Override
        public boolean isInfiniteDuration() {
            return getDuration() == INFINITE_DURATION;
        }
    };

    @Override
    public MobEffectInstance getFakeNightVision() {
        return this.fakeNightVision;
    }
}
