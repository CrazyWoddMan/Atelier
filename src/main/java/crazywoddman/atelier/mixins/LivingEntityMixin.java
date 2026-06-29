package crazywoddman.atelier.mixins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.client.LocalPlayerVars;
import crazywoddman.atelier.compat.oculus.OculusHelper;
import crazywoddman.atelier.effects.FakeNightVisionAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    private static final MobEffectInstance FAKE_NIGHT_VISION = new MobEffectInstance(MobEffects.NIGHT_VISION, MobEffectInstance.INFINITE_DURATION);

    @Shadow @Final
    private Map<MobEffect, MobEffectInstance> activeEffects;

    @Inject(
        method = "hasEffect",
        at = @At("HEAD"),
        cancellable = true
    )
    private void redirectNightVisionCheck1(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (checkConditions(effect))
            cir.setReturnValue(true);
    }

    @Inject(
        method = "getEffect",
        at = @At("HEAD"),
        cancellable = true
    )
    private void redirectNightVisionCheck2(MobEffect effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (checkConditions(effect))
            cir.setReturnValue(FAKE_NIGHT_VISION);
    }

    @Inject(
        method = "getActiveEffects",
        at = @At("HEAD"),
        cancellable = true
    )
    private void redirectNightVisionCheck3(CallbackInfoReturnable<Collection<MobEffectInstance>> cir) {
        if (checkConditions()) {
            Collection<MobEffectInstance> result = new ArrayList<>();

            for (MobEffectInstance effect : this.activeEffects.values())
                if (effect.getEffect() != MobEffects.NIGHT_VISION)
                    result.add(effect);

            result.add(((FakeNightVisionAccessor)this).getFakeNightVision());
            cir.setReturnValue(result);
        }
    }

    private boolean checkConditions(MobEffect effect) {
        return effect == MobEffects.NIGHT_VISION
            && checkConditions()
            && OculusHelper.shadersActive()
            && (AtelierConfig.Client.THIRD_PERSON_NVD.get() || Minecraft.getInstance().options.getCameraType().isFirstPerson());
    }

    private boolean checkConditions() {
        return (LivingEntity)(Object)this instanceof LocalPlayer
            && LocalPlayerVars.wearables.nvdActive;
    }
}
