package crazywoddman.atelier.api;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotAccessHelper {

    public static SlotAccess make(Supplier<ItemStack> getter, Predicate<ItemStack> setter) {
        return new SlotAccess() {
            @Override
            public ItemStack get() {
                return getter.get();
            }

            @Override
            public boolean set(ItemStack stack) {
                return setter.test(stack);
            }
        };
    }

    public static SlotAccess make(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
        return new SlotAccess() {
            @Override
            public ItemStack get() {
                return getter.get();
            }

            @Override
            public boolean set(ItemStack stack) {
                setter.accept(stack);
                return true;
            }
        };
    }

    public static Slot fakeSlot(SlotAccess access) {
        return new Slot(new FakeContainer(access), 0, 0, 0);
    }
}