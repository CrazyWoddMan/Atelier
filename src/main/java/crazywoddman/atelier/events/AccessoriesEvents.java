package crazywoddman.atelier.events;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.ModulesDataProvider;
import crazywoddman.atelier.api.ModulesDataProvider.ModulesData;
import crazywoddman.atelier.api.ModulesDataProvider.ModulesData.ParentReference;
import crazywoddman.atelier.api.interfaces.IModular;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.events.AccessoryChangeCallback;
import io.wispforest.accessories.api.events.SlotStateChange;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AccessoriesEvents {
    private record Module(String slot, int amount) {}

    public static void register() {
        AccessoryChangeCallback.EVENT.register(AccessoriesEvents::onAccessoryChange);
    }

    private static void doModulesStuff(
        AccessoriesCapability inventory,
        LivingEntity entity,
        ModulesData modulesData,
        ParentReference parent,
        ItemStack newStack,
        ItemStack prevStack,
        Map<String, Integer> newModules,
        Map<String, Integer> prevModules
    ) {
        Set<Module> modifiersToAdd = null;

        if (!newModules.isEmpty()) {
            modifiersToAdd = new HashSet<>();

            for (String module : newModules.keySet()) {
                int slotsAmount = newModules.get(module);
                modifiersToAdd.add(new Module(module, slotsAmount));
            }
        }

        Set<Module> modulesToLoad = modifiersToAdd == null ? null : Set.copyOf(modifiersToAdd);
        
        if (!prevModules.isEmpty())
            for (String slot : prevModules.keySet()) {
                int slotsAmount = prevModules.get(slot);
                Module module = new Module(slot, slotsAmount);
                ExpandedSimpleContainer accessories = inventory.getContainer(new SlotTypeReference(slot)).getAccessories();
                List<ParentReference> hierarchy = modulesData.get(slot);
                int startSlot = hierarchy.indexOf(parent);

                if (startSlot == -1)
                    throw new IllegalStateException(entity.getName().getString() + "'s parent slot for [" + slot + "] is not in modules data");

                int nextToLastIndex = startSlot + slotsAmount;

                if (modifiersToAdd != null && modifiersToAdd.contains(module)) {
                    Atelier.Queue.addToQueue(inventory.entity(), () -> {
                        for (int i = startSlot; i < nextToLastIndex; i++)
                            if (!accessories.getItem(i).isEmpty())
                                accessories.setItem(i, ItemStack.EMPTY);    
                    }, 1);
                    modifiersToAdd.remove(module);
                } else {
                    if (hierarchy.size() > nextToLastIndex) {
                        int offset = nextToLastIndex - startSlot;    
                        int i = nextToLastIndex;
                        
                        while (i < hierarchy.size()) {
                            ItemStack item = accessories.getItem(i);

                            if (i >= hierarchy.size() - offset && !accessories.getItem(i).isEmpty())
                                accessories.setItem(i, ItemStack.EMPTY);

                            accessories.setItem(i - offset, item);
                            i++;
                        }

                        while (i - offset < nextToLastIndex) {
                            accessories.setItem(i - offset, ItemStack.EMPTY);
                            i++;
                        }
                    } else
                        Atelier.Queue.addToQueue(inventory.entity(), () -> {
                            for (int i = startSlot; i < nextToLastIndex; i++)
                                if (!accessories.getItem(i).isEmpty())
                                    accessories.setItem(i, ItemStack.EMPTY);        
                        }, 1);
                    
                    while (hierarchy.remove(parent));
                    Atelier.Queue.addToQueue(entity, () -> inventory.getContainer(new SlotTypeReference(slot)).removeModifier(parent.createUUID()), 1);
                }
            }

        if (modulesToLoad != null) {
            CompoundTag modules = Optional.ofNullable(newStack.getTag()).map(tag -> tag.getCompound("modules")).orElse(new CompoundTag());

            for (Module slot : modulesToLoad) {
                AccessoriesContainer container = inventory.getContainer(new SlotTypeReference(slot.slot));
                List<ParentReference> hierarchy = modulesData.get(slot.slot);

                if (modifiersToAdd.contains(slot) && !hierarchy.contains(parent)) {
                    for (int i = 0; i < slot.amount; i++)
                        hierarchy.add(parent);

                    container.addTransientModifier(new AttributeModifier(
                        parent.createUUID(),
                        slot.slot, slot.amount,
                        AttributeModifier.Operation.ADDITION
                    ));
                }

                if (!modules.isEmpty())
                    Atelier.Queue.addToQueue(entity, () -> {
                        ListTag items = modules.getList(slot.slot, ListTag.TAG_COMPOUND);

                        if (!items.isEmpty()) {
                            ExpandedSimpleContainer accessories = container.getAccessories();

                            for (int i = 0; i < items.size(); i++) {
                                ItemStack item = ItemStack.of(items.getCompound(i));

                                if (!item.isEmpty())
                                    accessories.setItem(hierarchy.indexOf(parent) + i, item);
                            }
                        }
                    }, 1);
            }
        }
    }

    private static void onAccessoryChange(ItemStack prevStack, ItemStack newStack, SlotReference reference, SlotStateChange stateChange) {
        LivingEntity entity = reference.entity();
        Item newItem = newStack.getItem();
        Item prevItem = prevStack.getItem();
        String eventSlot = reference.slotName();
        byte index = (byte)reference.slot();
        Map<String, Integer> newModules = IModular.getModules(newItem, reference.slotName(), index);
        Map<String, Integer> prevModules = IModular.getModules(prevItem, reference.slotName(), index);
        AccessoriesCapability inventory = reference.capability();
        ModulesData modulesData = ModulesDataProvider.get(entity);

        if (stateChange == SlotStateChange.REPLACEMENT && (!newModules.isEmpty() || !prevModules.isEmpty()))
            doModulesStuff(
                inventory,
                entity,
                modulesData,
                new ParentReference(reference),
                newStack, prevStack,
                newModules, prevModules
            );
        
        List<ParentReference> hierarchy = modulesData.get(eventSlot);

        if (hierarchy.size() <= index)
                return;
            
        ParentReference parent = hierarchy.get(index);
        int localIndex = index - hierarchy.indexOf(parent);
        SlotReference accessory = parent.accessoriesSlot();
        ItemStack parentItem = (
            accessory == null
            ? entity.getItemBySlot(parent.equipmentSlot())
            : inventory
                .getContainer(new SlotTypeReference(accessory.slotName()))
                .getAccessories()
                .getItem(accessory.slot())
        );

        writeToCompound(parentItem, newStack, eventSlot, localIndex);
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        EquipmentSlot parent = event.getSlot();

        if (!parent.isArmor())
            return;

        LivingEntity entity = event.getEntity();
        ItemStack newStack = event.getTo();
        ItemStack prevStack = event.getFrom();
        Item newItem = newStack.getItem();
        Item prevItem = prevStack.getItem();
        Map<String, Integer> newModules = IModular.getModules(newItem);
        Map<String, Integer> prevModules = IModular.getModules(prevItem);
        
        if (!newModules.isEmpty() || !prevModules.isEmpty())
            AccessoriesCapability.getOptionally(entity).ifPresent(inventory -> doModulesStuff(
                inventory,
                entity,
                ModulesDataProvider.get(entity),
                new ParentReference(parent),
                newStack, prevStack,
                newModules, prevModules
            ));
    }

    public static void writeToCompound(ItemStack parent, ItemStack stack, String module, int index) {
        if (stack.isEmpty()) {
            CompoundTag parentTag = parent.getTag();

            if (parentTag == null || !parentTag.contains("modules"))
                return;

            CompoundTag parentModules = parentTag.getCompound("modules");

            if (!parentModules.contains(module))
                return;

            ListTag items = parentModules.getList(module, ListTag.TAG_COMPOUND);

            if (index < items.size()) {
                items.set(index, new CompoundTag());

                for (int i = items.size() - 1; i >= 0; i--)
                    if (items.getCompound(i).isEmpty())
                        items.remove(i);
                    else break;
            }

            if (items.isEmpty()) {
                parentModules.remove(module);

                if (parentModules.isEmpty()) {
                    parentTag.remove("modules");

                    if (parentTag.isEmpty())
                        parent.setTag(null);
                }  
            }
        } else {
            CompoundTag parentModules = getOrCreateTag(parent.getOrCreateTag(), "modules");
            ListTag items = parentModules.getList(module, ListTag.TAG_COMPOUND);

            while(items.size() - 1 < index)
                items.add(new CompoundTag());
            
            items.set(index, stack.serializeNBT());

            if (!parentModules.contains(module))
                parentModules.put(module, items);
        }
    }

    private static CompoundTag getOrCreateTag(CompoundTag root, String... path) {
        CompoundTag tree = root;

        for (String compound : path) {
            if (!tree.contains(compound))
                tree.put(compound, new CompoundTag());

            tree = tree.getCompound(compound);
        }

        return tree;
    }

    
}