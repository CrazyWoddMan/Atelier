package crazywoddman.atelier.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class FilterEffect extends MobEffect {
    public FilterEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0xFFFFFF
        );
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {}

    @Override
    public void applyInstantenousEffect(Entity entity, Entity entity2, LivingEntity entity3, int i, double d) {}

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}