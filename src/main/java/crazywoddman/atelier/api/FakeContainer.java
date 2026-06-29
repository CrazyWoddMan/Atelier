package crazywoddman.atelier.api;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.Container;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FakeContainer implements Container {
    private final Supplier<ItemStack> getter;
    private final Consumer<ItemStack> setter;

    public FakeContainer(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
        this.setter = setter;
        this.getter = getter;
    }

    public FakeContainer(SlotAccess access) {
        this(access::get, access::set);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getItem(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.getter.get();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return removeItemNoUpdate(slot);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(0);
        setItem(0, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.setter.accept(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        setItem(0, ItemStack.EMPTY);
    }
}