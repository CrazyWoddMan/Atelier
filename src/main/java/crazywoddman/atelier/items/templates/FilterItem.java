package crazywoddman.atelier.items.templates;

import java.util.List;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierSounds;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.effects.AtelierEffects;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class FilterItem extends DyableAccessory {
    public FilterItem(Properties properties) {
        super(properties.setNoRepair(), 13816530);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("isCreative");
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        String desc = super.getDescriptionId(stack);
        return stack.getOrCreateTag().getBoolean("isCreative") ? desc + ".creative" : desc;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("atelier.tooltip.equipped").withStyle(ChatFormatting.GRAY));
        CompoundTag tag = stack.getOrCreateTag();
        boolean isCreative = tag.getBoolean("isCreative");
        boolean hasEffects = tag.contains("effects");

        if (isCreative || hasEffects) {
            MutableComponent desc = Component.translatable(Atelier.MODID + ".tooltip.filter").withStyle(ChatFormatting.BLUE).append(" ");

            if (hasEffects) {
                ListTag effects = tag.getList("effects", ListTag.TAG_STRING);

                for (int i = 0; i < effects.size(); i++) {
                    ResourceLocation key = ResourceLocation.tryParse(effects.getString(i));

                    if (key != null && ForgeRegistries.MOB_EFFECTS.containsKey(key)) {
                        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(key);
                        desc.append(Component.translatable("effect." + key.getNamespace() + "." + key.getPath()).withStyle(effect.getCategory().getTooltipFormatting()));

                        if (effects.size() > 1 && i < effects.size() - 1)
                            desc.append(",");

                        desc.append(" ");
                    }
                }

                if (!isCreative) {
                    int timeLeft = stack.getMaxDamage() - stack.getDamageValue();
                    desc.append(String.format("(%d:%02d)", timeLeft / 60, timeLeft % 60));
                }
            } else {
                if (isCreative)
                    desc.append(Component.translatable(Atelier.MODID + ".tooltip.filter.creative"));
            }

            tooltip.add(desc);
        } else
            tooltip.add(Component.translatable(tag.getBoolean("isPrepared") ? "atelier.tooltip.filter.prepared" : "effect.none").withStyle(ChatFormatting.GRAY));
            
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        super.onEquip(stack, reference);
        LivingEntity entity = reference.entity();

        if (!(entity instanceof ServerPlayer player) || player.isSpectator())
            return;

        CompoundTag tag = stack.getOrCreateTag();
        boolean isCreative = tag.getBoolean("isCreative");

        if (isCreative || tag.contains("effects"))
            player.addEffect(new MobEffectInstance(
                AtelierEffects.FILTER_PROTECTED.get(),
                isCreative ? MobEffectInstance.INFINITE_DURATION : (stack.getMaxDamage() - stack.getDamageValue()) * 20,
                0,
                false, false, true
            ));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        super.onUnequip(stack, reference);

        if (reference.slotContainer().getAccessories().isEmpty())
            reference.entity().removeEffect(AtelierEffects.FILTER_PROTECTED.get());
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        super.tick(stack, reference);
        LivingEntity entity = reference.entity();

        if (entity.level().isClientSide && entity.canDrownInFluidType(ForgeMod.WATER_TYPE.get()) && entity.tickCount % 60 == 0)
            Minecraft.getInstance().getSoundManager().play(new FilterBreathingSound(entity, reference));
                
        if (!(entity instanceof ServerPlayer serverPlayer) || entity.tickCount % 20 != 0 || serverPlayer.isSpectator())
            return;
        
        CompoundTag tag = stack.getOrCreateTag();

        if (tag.contains("effects") && !tag.getBoolean("isCreative") && stack.hurt(1, serverPlayer.getRandom(), serverPlayer)) {
            AccessoriesAPI.breakStack(reference);
            Item item = stack.getItem();
            stack.shrink(1);
            serverPlayer.awardStat(Stats.ITEM_BROKEN.get(item));
        }
    }

    @Override
    public void onBreak(ItemStack stack, SlotReference reference) {
        super.onBreak(stack, reference);
        LivingEntity entity = reference.entity();
        entity.playSound(SoundEvents.ITEM_BREAK);
    }

    @Override
    public SoundEventData getEquipSound() {
        return new SoundEventData(SoundEvents.ARMOR_EQUIP_GOLD, 1, 1);
    }

    @OnlyIn(Dist.CLIENT)
    private static class FilterBreathingSound extends EntityBoundSoundInstance {
        private final LivingEntity entity;
        private final SlotReference reference;

        public FilterBreathingSound(LivingEntity entity, SlotReference reference) {
            super(
                AtelierSounds.GASMASK.get(),
                SoundSource.PLAYERS,
                1,
                0.85f + entity.getRandom().nextFloat() * 0.05f,
                entity,
                entity.level().getRandom().nextLong()
            );
            this.entity = entity;
            this.reference = reference;
        }

        @Override
        public void tick() {
            super.tick();
            
            if (this.entity == null || !this.entity.isAlive() || reference.getStack().isEmpty() || !reference.capability().getContainer(new SlotTypeReference("face")).shouldRender(0) || !reference.slotContainer().shouldRender(reference.slot()) || this.entity.isUnderWater())
                stop();
        }
    }
}