package crazywoddman.atelier.accessories;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.recipes.AtelierRecipes;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.events.AccessoryChangeCallback;
import io.wispforest.accessories.api.events.SlotStateChange;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.tags.ITag;

public class AccessoriesEvents {

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "AccessoriesLoader");
        thread.setDaemon(true);

        return thread;
    });

    private static void loadItems(AccessoriesCapability inventory, CompoundTag modules, String slotID, int slotsAmount) {
        if (!modules.contains(slotID))
            return;

        AccessoriesContainer slot = inventory.getContainer(new SlotTypeReference(slotID));

        if (slot == null)
            return;

        ExpandedSimpleContainer accessories = slot.getAccessories();

        if (accessories.getContainerSize() < slotsAmount)
            return;

        ListTag slots = modules.getList(slotID, ListTag.TAG_COMPOUND);
                    
        for (int i = 0; i < accessories.getContainerSize() && i < slots.size(); i++) {
            ItemStack stack = ItemStack.of(slots.getCompound(i));

            if (!stack.isEmpty())
                accessories.setItem(i, stack);
        }
    }

    private static void loadModule(AccessoriesCapability inventory, CompoundTag modules, String slotID, int slotsAmount, boolean addSlot) {
        AccessoriesContainer slot = inventory.getContainer(new SlotTypeReference(slotID));

        if (slot == null)
            return;

        if (addSlot)
            slot.addTransientModifier(
                new AttributeModifier(
                    UUIDUtil.createOfflinePlayerUUID(slotID),
                    slotID,
                    slotsAmount,
                    AttributeModifier.Operation.ADDITION
                )
            );

        SCHEDULER.schedule(
            () -> loadItems(inventory, modules, slotID, slotsAmount),
            100,
            TimeUnit.MILLISECONDS
        );
    }

    private static void unloadModule(AccessoriesCapability inventory, String slotID, boolean removeSlot) {
        AccessoriesContainer slot = inventory.getContainer(new SlotTypeReference(slotID));

        if (slot == null)
            return;

        SCHEDULER.schedule(
            () -> {
                slot.getAccessories().clearContent();

                if (removeSlot)
                    slot.removeModifier(UUIDUtil.createOfflinePlayerUUID(slotID));
            },
            100,
            TimeUnit.MILLISECONDS
        );
    }

    private static void saveModuleToCompound(ItemStack modularItem, ItemStack stackInModule, String module, int index) {
        CompoundTag modules = getOrCreateTag(modularItem.getOrCreateTag(), "modules");
        ListTag list = modules.getList(module, ListTag.TAG_COMPOUND);

        while (list.size() <= index)
            list.add(new CompoundTag());

        list.set(index, stackInModule.isEmpty() ? new CompoundTag() : stackInModule.serializeNBT());

        if (!modules.contains(module))
            modules.put(module, list);
    }

    private static void onAccessoryChange(ItemStack prevStack, ItemStack newStack, SlotReference reference, SlotStateChange stateChange) {
        LivingEntity entity = reference.entity();
        Item newItem = newStack.getItem();
        Item prevItem = prevStack.getItem();
        String eventSlot = reference.slotName();
        int index = reference.slot();
        Set<Item> patch_wearables = AtelierRecipes.getPatchWearables();

        boolean newHasUniqueModules = newItem instanceof IModular;
        boolean prevHasUniqueModules = prevItem instanceof IModular;
        boolean newIsPatchable = patch_wearables.contains(newItem);
        boolean prevIsPatchable = patch_wearables.contains(prevItem);

        if (stateChange == SlotStateChange.REPLACEMENT && (newHasUniqueModules || prevHasUniqueModules || newIsPatchable || prevIsPatchable)) {
            AccessoriesCapability.getOptionally(entity).ifPresent(inventory -> {
                if (prevHasUniqueModules)
                    for (String slot : ((IModular)prevItem).getModules().keySet())
                        unloadModule(inventory, slot, !(newHasUniqueModules && ((IModular)newItem).getModules().containsKey(slot)));

                AtelierRecipes.getPatchSlot(prevItem).ifPresent(patch -> 
                    unloadModule(inventory, patch, !newIsPatchable)
                );

                if (newHasUniqueModules || newIsPatchable) {
                    CompoundTag modules = newStack.getOrCreateTag().getCompound("modules");

                    AtelierRecipes.getPatchSlot(prevItem).ifPresent(patch -> 
                        loadModule(inventory, modules, patch, 1, !prevIsPatchable)
                    );

                    if (newHasUniqueModules) {
                        Map<String, Integer> slots = ((IModular)newItem).getModules();

                        for (String slot : slots.keySet())
                            loadModule(inventory, modules, slot, slots.get(slot), !(prevHasUniqueModules && ((IModular)prevItem).getModules().containsKey(slot)));
                    }
                }
            });
        }

        for (ItemStack armor : entity.getArmorSlots()) {
            if (!armor.isEmpty()) {
                Item armorItem = armor.getItem();

                if (
                    (eventSlot.equals("armor_plate") && AtelierItems.Tags.get(AtelierItems.Tags.PLATE_CARRIERS).contains(armorItem)) ||
                    (armorItem instanceof IModular modular && modular.getModules().containsKey(eventSlot)) ||
                    AtelierRecipes.getPatchSlot(armorItem).map(patch -> patch.equals(eventSlot)).orElse(false)
                ) {
                    saveModuleToCompound(armor, newStack, eventSlot, index);
                    return;
                }
            }
        }

        AccessoriesCapability.getOptionally(entity).ifPresent(inventory -> {
            List<SlotEntryReference> accessories = inventory.getAllEquipped();

            for (int i = 0; i < accessories.size(); i++) {
                ItemStack accessoryStack = accessories.get(i).stack();
                Item accessoryItem = accessoryStack.getItem();

                if (
                    (accessoryItem instanceof IModular modular && modular.getModules().containsKey(eventSlot)) ||
                    AtelierRecipes.getPatchSlot(accessoryItem).map(patch -> patch.equals(eventSlot)).orElse(false)
                )
                    saveModuleToCompound(accessoryStack, newStack, eventSlot, index);
            }
        });
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();

        if (!event.getSlot().isArmor())
            return;
        
        ItemStack newStack = event.getTo();
        Item newItem = newStack.getItem();
        Item prevItem = event.getFrom().getItem();
        ITag<Item> plate_carriers = AtelierItems.Tags.get(AtelierItems.Tags.PLATE_CARRIERS);
        Set<Item> patch_wearables = AtelierRecipes.getPatchWearables();

        boolean newHasUniqueModules = newItem instanceof IModular;
        boolean prevHasUniqueModules = prevItem instanceof IModular;
        boolean newIsPlateCarrier = plate_carriers.contains(newItem);
        boolean prevIsPlateCarrier = plate_carriers.contains(prevItem);
        boolean newIsPatchable = patch_wearables.contains(newItem);
        boolean prevIsPatchable = patch_wearables.contains(prevItem);


        if (!(newHasUniqueModules || prevHasUniqueModules || newIsPlateCarrier || prevIsPlateCarrier || newIsPatchable || prevIsPatchable))
            return;

        AccessoriesCapability.getOptionally(entity).ifPresent(inventory -> {
            if (prevIsPlateCarrier)
                AccessoriesEvents.unloadModule(inventory, "armor_plate", !newIsPlateCarrier);

            AtelierRecipes.getPatchSlot(prevItem).ifPresent(patch ->
                AccessoriesEvents.unloadModule(inventory, patch, !newIsPatchable)
            );

            if (prevHasUniqueModules)
                for (String slot : ((IModular)prevItem).getModules().keySet())
                    AccessoriesEvents.unloadModule(inventory, slot, !(newHasUniqueModules && ((IModular)newItem).getModules().containsKey(slot)));

            if (!(newHasUniqueModules || newIsPlateCarrier || newIsPatchable))
                return;

            CompoundTag modules = newStack.getOrCreateTag().getCompound("modules");

            if (newIsPlateCarrier)
                AccessoriesEvents.loadModule(inventory, modules, "armor_plate", 1, !prevIsPlateCarrier);

            AtelierRecipes.getPatchSlot(newItem).ifPresent(patch ->
                AccessoriesEvents.loadModule(inventory, modules, patch, 1, !prevIsPatchable)
            );

            if (newHasUniqueModules) {
                Map<String, Integer> slots = ((IModular)newItem).getModules();

                for (String slot : slots.keySet())
                    AccessoriesEvents.loadModule(inventory, modules, slot, slots.get(slot), !(prevHasUniqueModules && ((IModular)prevItem).getModules().containsKey(slot)));
            }
        });
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!event.getSource().getMsgId().equals("armor_bypass_damage"))
            return;

        ItemStack vest = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
            
        if (!AtelierItems.Tags.get(AtelierItems.Tags.PLATE_CARRIERS).contains(vest.getItem()))
            return;

        ListTag list = vest.getOrCreateTag().getCompound("modules").getList("armor_plate", ListTag.TAG_COMPOUND);
        ItemStack plate = ItemStack.of(list.getCompound(0));

        if (plate.isEmpty())
            return;

        AtelierRecipes.getPlateRecipe(plate).ifPresent(recipe -> {
            float damage = event.getAmount();
            float absorption = damage * recipe.getProtection();
            int itemDamage = plate.getDamageValue() + Math.round(absorption * 10);
            int maxItemDamage = recipe.getDurability().orElse(plate.getMaxDamage());

            if (itemDamage > maxItemDamage) {
                absorption -= (itemDamage - maxItemDamage) / 10.0f;
                plate.shrink(plate.getCount());
            } else
                plate.setDamageValue(itemDamage);

            event.setAmount(damage - absorption);
        });
    }

    public static void register() {
        AccessoryChangeCallback.EVENT.register(
            AccessoriesEvents::onAccessoryChange
        );
    }

    private static CompoundTag getOrCreateTag(CompoundTag root, String... compounds) {
        CompoundTag path = root;

        for (String compound : compounds) {
            if (!path.contains(compound))
                path.put(compound, new CompoundTag());

            path = path.getCompound(compound);
        }

        return path;
    }
}