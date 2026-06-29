package crazywoddman.atelier.api.interfaces;

import java.util.Optional;

import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.SimpleSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IContainerItem extends InteractSound {

    int getContainerCapacity();
    int getVisibleSlots(ItemStack stack, ItemStack carried);
    ItemStack[] getContainerItems(ItemStack stack);
    boolean insertAtIndex(ItemStack stack, ItemStack input, SlotAccess output, int index);

    default EquipSound insertSound() {
        return EquipSound.BUNDLE;
    }

    static int getCapacity(ItemStack stack) {
        return get(stack.getItem()).map(IContainerItem::getContainerCapacity).orElse(0);
    }

    static ItemStack[] getItems(ItemStack stack) {
        return get(stack.getItem()).map(i -> i.getContainerItems(stack)).orElse(new ItemStack[]{});
    }

    static boolean insert(ItemStack stack, ItemStack input, SlotAccess output, int index) {
        return get(stack.getItem()).map(i -> i.insertAtIndex(stack, input, output, index)).orElse(false);
    }

    public static float getFullness(ItemStack stack) {
        float fullness = 0;

        for (ItemStack content : getItems(stack))
            fullness += content.getMaxStackSize() / content.getCount();

        return fullness / getCapacity(stack);
    }

    static Optional<IContainerItem> get(Item item) {
        if (item instanceof IContainerItem container)
            return Optional.of(container);

        return IModular.getModules(item).map(modules ->
            new IContainerItem() {
                @Override
                public int getContainerCapacity() {
                    return modules.size();
                }

                @Override
                public int getVisibleSlots(ItemStack stack, ItemStack carried) {
                    return getContainerCapacity();
                }

                @Override
                public ItemStack[] getContainerItems(ItemStack stack) {
                    return IModular.getItems(stack);
                }

                @Override
                public boolean insertAtIndex(ItemStack stack, ItemStack input, SlotAccess output, int index) {
                    return IModular.getModules(item).map(modules -> {
                        String module = modules.get(index);
                        if (IModular.predicate(item, module, input)) {
                            IModular.insert(stack, input, SimpleSlot.of(module, index - modules.indexOf(module)));
                            return true;
                        }
                        return false;
                    }).orElse(false);
                }
            }
        );
    }
}