package crazywoddman.atelier.events;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierCommands;
import crazywoddman.atelier.AtelierSounds;
import crazywoddman.atelier.AtelierTags;
import crazywoddman.atelier.config.Config;
import crazywoddman.atelier.effects.AtelierEffects;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.network.NetworkHandler;
import crazywoddman.atelier.network.SyncConfigPacket;
import crazywoddman.atelier.recipes.AtelierRecipes;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangeGameModeEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = Atelier.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class AtelierForgeEvents {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        AtelierRecipes.reload(event.getServer().getRecipeManager());
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        AtelierCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();

        AccessoriesCapability.getOptionally(entity).ifPresent(capability -> {
            ExpandedSimpleContainer filters = capability.getContainer(new SlotTypeReference("gas_filter")).getAccessories();

            for (int slot = 0; slot < filters.getContainerSize(); slot++) {
                ItemStack item = filters.getItem(slot);

                if (item.isEmpty())
                    continue;

                CompoundTag tag = item.getOrCreateTag();
                MobEffect effect = event.getEffectInstance().getEffect();

                if (tag.getList("effects", ListTag.TAG_STRING).contains(StringTag.valueOf(ForgeRegistries.MOB_EFFECTS.getKey(effect).toString())) || (tag.getBoolean("isCreative") && effect.getCategory() == MobEffectCategory.HARMFUL)) {
                    event.setResult(Result.DENY);
                    return;
                }
            }
        });
    }

    @SubscribeEvent
    public static void onGameModeChange(PlayerChangeGameModeEvent event) {
        Player player = event.getEntity();

        if (player.hasEffect(AtelierEffects.FILTER_PROTECTED.get())) {
            player.removeEffect(AtelierEffects.FILTER_PROTECTED.get());
            Atelier.Queue.addToQueue(player, () -> {
                ExpandedSimpleContainer container = AccessoriesCapability.get(player).getContainer(new SlotTypeReference("gas_filter")).getAccessories();
                for (int i = 0; i < container.getContainerSize(); i++) {
                    ItemStack item = container.getItem(i);

                    if (!item.isEmpty()) {
                        Accessory accessory = (Accessory)item.getItem();
                        SlotReference reference = SlotReference.of(player, "gas_filter", i);
                        accessory.onEquip(item, reference);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEarnEvent event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer && event.getAdvancement().getId().equals(ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "cotton_field")))
            serverPlayer.connection.send(new ClientboundSoundPacket(
                AtelierSounds.GOTTA_MOVE.getHolder().get(),
                SoundSource.MUSIC,
                serverPlayer.getX(),
                serverPlayer.getY(),
                serverPlayer.getZ(),
                1,
                1,
                0
            ));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().is(DamageTypes.FALL)) {
            AccessoriesCapability.getOptionally(event.getEntity()).ifPresent(inventory -> {
                if (inventory.isEquipped(AtelierItems.KNEEPADS.get()))
                    event.setAmount(event.getAmount() * (1 - Config.SERVER.kneePadsProtection.get().floatValue()));
            });
        }
        if (Atelier.warium && event.getSource().getMsgId().equals("armor_bypass_damage")) {
            LivingEntity entity = event.getEntity();
            ItemStack vest = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
                
            if (!AtelierTags.Items.get(AtelierTags.Items.PLATE_CARRIERS).contains(vest.getItem()))
                return;

            CompoundTag tag = vest.getTag();

            if (tag == null || !tag.contains("modules"))
                return;

            ListTag list = tag.getCompound("modules").getList("armor_plate", ListTag.TAG_COMPOUND);

            if (list.isEmpty())
                return;

            ItemStack plate = ItemStack.of(list.getCompound(0));

            if (plate.isEmpty())
                return;

            Item plateItem = plate.getItem();

            AtelierRecipes.getPlateRecipe(plateItem).ifPresent(recipe -> {
                float initialDamage = event.getAmount();
                float damage = initialDamage - recipe.protection;
                int itemDamage = plate.getDamageValue() + (int)initialDamage;
                int maxItemDamage = recipe.durability.orElse(plate.getMaxDamage());

                if (itemDamage >= maxItemDamage) {
                    list.remove(0);
                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 2, 1);
                    if (entity instanceof ServerPlayer player)
                        player.awardStat(Stats.ITEM_BROKEN.get(plateItem));
                } else plate.setDamageValue(itemDamage);

                if (damage <= 0) event.setCanceled(true);
                else event.setAmount(damage);
            });
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingTickEvent event) {
        Atelier.Queue.runQueue(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
            NetworkHandler.CHANNEL.sendTo(
                SyncConfigPacket.fromConfig(),
                serverPlayer.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
    }
}
