package crazywoddman.atelier.api.interfaces;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.SlotAccessHelper;
import crazywoddman.atelier.data.ArmorPlates;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IModular {
    static final Map<Item, List<String>> CACHE = new HashMap<>();
    static final String
    MODULES_TAG = "Modules",
    POUCH = "pouch",
    PATCH = "patch",
    ARMOR_PLATE = "armor_plate",
    GAS_FILTER = "gas_filter",
    CIGARETTE_PACK = "cigarette_pack",
    HOLSTER = "holster",
    SLING = "sling";

    default boolean modulePredicate(String module, ItemStack stack) {
        return defaultPredicate(module, stack);
    }

    static boolean isModular(Item item) {
        return CACHE.containsKey(item);
    }

    static Optional<List<String>> getModules(Item item) {
        return Optional.ofNullable(CACHE.get(item));
    }

    static Optional<CompoundTag> getModules(ItemStack stack) {
        return Optional.ofNullable(stack.getTagElement(MODULES_TAG));
    }

    static Optional<CompoundTag> getModule(ItemStack stack, String module) {
        return getModules(stack)
            .filter(m -> m.contains(module))
            .map(m -> m.getCompound(module));
    }

    static ItemStack getStackInSlot(ItemStack stack, SimpleSlot module) {
        return getModule(stack, module.name)
            .filter(m -> m.contains(Integer.toString(module.index)))
            .map(m -> ItemStack.of(m.getCompound(Integer.toString(module.index))))
            .orElse(ItemStack.EMPTY);
    }

    static SlotAccess getAccess(ItemStack stack, SimpleSlot module) {
        return SlotAccessHelper.make(
            () -> getStackInSlot(stack, module),
            s -> {
                if (IModular.predicate(stack.getItem(), module.name, s)) {
                    insert(stack, s, module);
                    return true;
                }
                return false;
            }
        );
    }

    static ItemStack[] getItems(ItemStack stack) {
        List<String> modules = CACHE.get(stack.getItem());

        if (modules == null)
            return new ItemStack[]{};

        int size = modules.size();
        ItemStack[] items = new ItemStack[size];
        CompoundTag equipped = stack.getTagElement(MODULES_TAG);

        if (equipped == null) {
            Arrays.fill(items, ItemStack.EMPTY);
        } else {
            for (int i = 0; i < size; i++) {
                String name = modules.get(i);
                CompoundTag module = equipped.getCompound(name);

                if (!module.isEmpty()) {
                    CompoundTag item = module.getCompound(Integer.toString(i - modules.indexOf(name)));

                    if (!item.isEmpty()) {
                        items[i] = ItemStack.of(item);
                        continue;
                    }
                }

                items[i] = ItemStack.EMPTY;
            }
        }

        return items;
    }

    static void forEachEquipped(ItemStack stack, BiConsumer<SimpleSlot, ItemStack> action) {
        getModules(stack).ifPresent(modules -> {
             for (String name : modules.getAllKeys()) {
                CompoundTag items = modules.getCompound(name);
                for (String index : items.getAllKeys())
                    action.accept(SimpleSlot.of(name, Integer.valueOf(index)), ItemStack.of(items.getCompound(index)));
             }
        });
    }

    static void forEachEquipped(ItemStack stack, String module, BiConsumer<Integer, ItemStack> action) {
        getModule(stack, module).ifPresent(items -> {
            for (String index : items.getAllKeys())
                action.accept(Integer.valueOf(index), ItemStack.of(items.getCompound(index)));
        });
    }

    static ItemStack insert(ItemStack parent, ItemStack input, SimpleSlot module) {
        if (input.isEmpty()) {
            CompoundTag tag = parent.getTag();

            if (tag != null) {
                CompoundTag modules = tag.getCompound(MODULES_TAG);

                if (!modules.isEmpty()) {
                    CompoundTag items = modules.getCompound(module.name);

                    if (!items.isEmpty()) {
                        String index = Integer.toString(module.index);
                        CompoundTag item = items.getCompound(index);

                        if (!item.isEmpty()) {
                            ItemStack output = ItemStack.of(item);
                            items.remove(index);
                            
                            if (items.isEmpty()) {
                                modules.remove(module.name);

                                if (modules.isEmpty()) {
                                    tag.remove(MODULES_TAG);

                                    if (tag.isEmpty())
                                        parent.setTag(null);
                                }
                            }
                            return output;
                        }
                    }
                }
            }
            return ItemStack.EMPTY;
        } else {
            CompoundTag modules = parent.getOrCreateTagElement(MODULES_TAG);
            CompoundTag items = modules.getCompound(module.name);
            String i = Integer.toString(module.index);
            ItemStack output = items.contains(i) ? ItemStack.of(items.getCompound(i)) : ItemStack.EMPTY;
            items.put(i, input.serializeNBT());

            if (!modules.contains(module.name))
                modules.put(module.name, items);

            return output;
        }
    }

    static boolean predicate(Item parent, String module, ItemStack stack) {
        return stack.isEmpty() || (
            parent instanceof IModular modular
            ? modular.modulePredicate(module, stack)
            : defaultPredicate(module, stack)
        );
    }

    private static boolean defaultPredicate(String module, ItemStack stack) {
        return (module.equals(ARMOR_PLATE) && ArmorPlates.isPlate(stack.getItem()))
            || stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "slots/" + module)));
    }
}