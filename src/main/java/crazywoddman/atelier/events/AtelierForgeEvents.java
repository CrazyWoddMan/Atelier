package crazywoddman.atelier.events;

import java.util.function.Consumer;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.Queue;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.client.AtelierKeyMappings;
import crazywoddman.atelier.client.ClientUtils;
import crazywoddman.atelier.data.ArmorPlates;
import crazywoddman.atelier.data.AtelierCommands;
import crazywoddman.atelier.data.AtelierData;
import crazywoddman.atelier.data.AtelierSounds;
import crazywoddman.atelier.data.ModulesDataSerializer;
import crazywoddman.atelier.data.QuickAccessSlot;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.data.WearablesCapability.WearableState;
import crazywoddman.atelier.effects.AtelierEffects;
import crazywoddman.atelier.gui.ClientContainerItemTooltip;
import crazywoddman.atelier.gui.ClientModuleTooltip;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.items.templates.AbstractGasMask;
import crazywoddman.atelier.items.templates.FilterItem;
import crazywoddman.atelier.network.packets.EquipmentUpdatePacket;
import crazywoddman.atelier.network.packets.StackClickedPacket;
import crazywoddman.atelier.network.packets.SyncModulesPacket;
import crazywoddman.atelier.network.packets.WearCapToClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = Atelier.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class AtelierForgeEvents {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        AtelierData.reload(event.getServer().getRecipeManager(), false);
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer eventPlayer = event.getPlayer();

        if (eventPlayer == null) {
            AtelierData.reload(event.getPlayerList().getServer().getRecipeManager(), false);

            for (ServerPlayer player : event.getPlayerList().getPlayers())
                SyncModulesPacket.send(player.connection.connection);
        }
        else SyncModulesPacket.send(eventPlayer.connection.connection);

    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(AddReloadListenerEvent event) {
        ModulesDataSerializer.register(event);
        ArmorPlates.register(event);
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        if (Atelier.ACCESSORIES_LOADED)
            AtelierCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        WearablesCapability.attach(event);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer tracked && event.getEntity() instanceof ServerPlayer tracking) {
            tracked.getCapability(WearablesCapability.WEARABLES_CAPABILITY).ifPresent(cap ->
                WearCapToClientPacket.send(tracking, tracked, WearableState.ALL)
            );
        }
    }

    @SubscribeEvent
    public static void onGameModeChange(PlayerEvent.PlayerChangeGameModeEvent event) {
        Player player = event.getEntity();

        if (event.getNewGameMode() == GameType.SPECTATOR) {
            player.removeEffect(AtelierEffects.FILTER_PROTECTED.get());
        } else {
            CompatHelper.getSlotContainer(player, IWearableAccessory.FACE).ifPresent(container -> {
                for (int i = 0; i < container.getContainerSize(); i++)
                    if (AbstractGasMask.onChange(container.getItem(i), player))
                        return;
            });
        }
    }

    @SubscribeEvent
    public static void onLogout(LevelEvent.Unload event) {
        QuickAccessSlot.CACHE.clear();
        Queue.of(event.getLevel()).clear();
    }

    @SubscribeEvent
    public static void onEndermanAnger(EnderManAngerEvent event) {
        CompatHelper.getSlotContainer(event.getPlayer(), IWearableAccessory.FACE).ifPresent(face -> {
            for (int i = 0; i < face.getContainerSize(); i++)
                if (face.getItem(i).is(AtelierItems.WELDING_GOGGLES.get()))
                    event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();

        CompatHelper.getSlotContainer(entity, IWearableAccessory.FACE).ifPresent(face -> {
            for (int slot = 0; slot < face.getContainerSize(); slot++) {
                ItemStack stack = face.getItem(slot);

                if (stack.isEmpty() || stack.is(AtelierTags.Items.GASMASKS))
                    continue;

                IModular.forEachEquipped(stack, IModular.GAS_FILTER, (index, filter) -> {
                    CompoundTag tag = filter.getTag();
                    MobEffect effect = event.getEffectInstance().getEffect();

                    if (tag.getList(FilterItem.EFFECTS_TAG, ListTag.TAG_STRING).contains(StringTag.valueOf(ForgeRegistries.MOB_EFFECTS.getKey(effect).toString()))
                    || (tag.getBoolean(FilterItem.CREATIVE_TAG) && effect.getCategory() == MobEffectCategory.HARMFUL)
                    ) {
                        event.setResult(Result.DENY);
                        return;
                    }
                });
            }
        });
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        EquipmentSlot slot = event.getSlot();

        if (!slot.isArmor())
            return;

        EquipmentUpdatePacket.send(player, SimpleSlot.of(slot));
    }

    @SubscribeEvent
    public static void onStackClicked(ItemStackedOnOtherEvent event) {
        ItemStack stack = event.getCarriedItem();

        if (stack.isEmpty() || event.getClickAction() != ClickAction.SECONDARY)
            return;

        IModular.getModules(stack.getItem()).ifPresent(modules -> {
            event.setCanceled(true);
            Player player = event.getPlayer();

            if (!player.level().isClientSide || !player.isLocalPlayer())
                return;

            String name = modules.get(ClientContainerItemTooltip.chosen);
            SimpleSlot module = SimpleSlot.of(name, ClientContainerItemTooltip.chosen - modules.indexOf(name));
            ItemStack carried = event.getStackedOnItem();
            boolean carriedEmpty = carried.isEmpty();
            boolean creativeInventory = ClientUtils.creativeInventoryOpen();
            boolean carriedDevided = false;
            boolean previewDown = AtelierKeyMappings.MODULAR_PREVIEW.isDown();

            if (!carriedEmpty || previewDown) {
                ItemStack chosen = IModular.getStackInSlot(stack, module);
                Item carriedItem = carried.getItem();

                if (!chosen.isEmpty() && chosen.getItem() instanceof IContainerItem && !(carriedItem instanceof IContainerItem)) {
                    Consumer<ItemStack> setter = s -> IModular.insert(stack, s, module);
                    IContainerItem.insert(
                        chosen,
                        carried,
                        event.getCarriedSlotAccess(),
                        ClientModuleTooltip.preview
                    );
                    setter.accept(chosen);

                    if (!creativeInventory)
                        StackClickedPacket.send(event.getSlot(), module, previewDown ? ClientModuleTooltip.preview : StackClickedPacket.NO_PREVIEW);

                    return;
                }
                
                if (!IModular.predicate(stack.getItem(), module.name, carried))
                    return;

                if (carried.getCount() > 1) {
                    if (!IModular.getStackInSlot(stack, module).isEmpty()) {
                        return;
                    } else {
                        carried.shrink(1);
                        carriedDevided = true;
                    }
                }
            }

            ItemStack output = IModular.insert(stack, carried.copyWithCount(1), module);

            if (!carried.isEmpty()) {
                EquipSound.BUNDLE.playEquip(player, player);
            } else {
                EquipSound.BUNDLE.playUnequip(player, player);
            }

            if (!creativeInventory)
                StackClickedPacket.send(event.getSlot(), module, StackClickedPacket.NO_PREVIEW);

            if (!carriedDevided)
                event.getCarriedSlotAccess().set(output);
        });
    }

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEarnEvent event) {
        Player player = event.getEntity();

        if (event.getAdvancement().getId().equals(ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "cotton_field")) && player instanceof ServerPlayer serverPlayer)
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
        DamageSource source = event.getSource();
        
        if (source.is(DamageTypes.FALL)) {
            if (event.getEntity().getItemBySlot(EquipmentSlot.LEGS).is(AtelierItems.KNEEPADS.get()))
                event.setAmount(event.getAmount() * (1 - AtelierConfig.Server.KNEEPADS_PROTECT.get().floatValue()));
        } else if (Atelier.WARIUM_LOADED ? source.getMsgId().equals("armor_bypass_damage") : source.is(DamageTypes.ARROW)) {
            LivingEntity entity = event.getEntity();
            ItemStack vest = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
                
            if (!AtelierTags.Items.get(AtelierTags.Items.PLATE_CARRIERS).contains(vest.getItem()))
                return;

            ItemStack stack = IModular.getStackInSlot(vest, SimpleSlot.of(IModular.ARMOR_PLATE, 0));

            if (stack.isEmpty())
                return;

            ArmorPlates.get(stack.getItem()).ifPresent(plate -> {
                float initialDamage = event.getAmount();
                float damage = initialDamage - plate.protection;
                int itemDamage = stack.getDamageValue() + (int)initialDamage;
                int maxItemDamage = plate.getDurability().orElse(stack.getMaxDamage());

                if (itemDamage >= maxItemDamage) {
                    IModular.insert(vest, ItemStack.EMPTY, SimpleSlot.of(IModular.ARMOR_PLATE, 0));
                    entity.level().playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.ITEM_BREAK,
                        SoundSource.PLAYERS,
                        2, 1
                    );
                } else stack.setDamageValue(itemDamage);

                if (damage <= 0) event.setCanceled(true);
                else event.setAmount(damage);
            });
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        tick(event);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        tick(event);
    }

    private static void tick(TickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            Queue.of(event.side).tick();
    }
}
