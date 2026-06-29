package crazywoddman.atelier.items.templates;

import java.util.function.Supplier;

import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.client.renderers.GasMaskRenderer;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.effects.AtelierEffects;
import crazywoddman.atelier.network.packets.BreathingSoundPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractGasMask extends DyableAccessory {
    protected final int filters;

    public AbstractGasMask(int filters, int... defaultColors) {
        super(defaultColors);
        this.filters = filters;
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return () -> new GasMaskRenderer(this);
    }

    @Override
    public void onUnequip(ItemStack stack, LivingEntity entity, SimpleSlot slot) {
        super.onUnequip(stack, entity, slot);

        if (entity instanceof Player)
            entity.removeEffect(AtelierEffects.FILTER_PROTECTED.get());
    }

    @Override
    public void wearTick(ItemStack stack, LivingEntity entity, SimpleSlot slot) {
        super.wearTick(stack, entity, slot);
                
        if (!(entity instanceof ServerPlayer player) || entity.tickCount % 20 != 0 || player.isSpectator())
            return;

        if (entity.tickCount % 60 == 0 && CompatHelper.shouldRender(entity, slot) && IModular.getModule(stack, IModular.GAS_FILTER).isPresent())
            BreathingSoundPacket.send(player, slot.index);
        
        IModular.forEachEquipped(stack, IModular.GAS_FILTER, (index, filter) -> {
            CompoundTag tag = filter.getOrCreateTag();

            if (
                tag.contains(FilterItem.EFFECTS_TAG) &&
                !tag.getBoolean(FilterItem.CREATIVE_TAG) &&
                filter.hurt(1, player.getRandom(), player)
            ) {
                CompatHelper.breakEvent(entity, slot);
                IModular.insert(stack, ItemStack.EMPTY, SimpleSlot.of(IModular.GAS_FILTER, index));
            }
        });
    }

    public static boolean onChange(ItemStack stack, Player player) {
        if (stack.is(AtelierTags.Items.GASMASKS)) {
            player.removeEffect(AtelierEffects.FILTER_PROTECTED.get());
            IModular.forEachEquipped(stack, IModular.GAS_FILTER, (index, filter) -> {
                CompoundTag tag = filter.getOrCreateTag();
                boolean isCreative = tag.getBoolean(FilterItem.CREATIVE_TAG);

                if (isCreative || tag.contains(FilterItem.EFFECTS_TAG)) {
                    player.addEffect(new MobEffectInstance(
                        AtelierEffects.FILTER_PROTECTED.get(),
                        isCreative ? MobEffectInstance.INFINITE_DURATION : (filter.getMaxDamage() - filter.getDamageValue()) * 20,
                        0,
                        false, false, true
                    ));
                }
            });
            return true;
        } else {
            player.removeEffect(AtelierEffects.FILTER_PROTECTED.get());
            return false;
        }
    }
}