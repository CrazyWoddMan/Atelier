package crazywoddman.atelier.api;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.compat.CompatHandler;
import crazywoddman.atelier.compat.accessories.AccessoriesHandler;
import crazywoddman.atelier.compat.curios.CuriosHandler;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CompatHelper {
    private static final CompatHandler IMPLEMENTATION = Atelier.ACCESSORIES_LOADED ? AccessoriesHandler.INSTANCE : CuriosHandler.INSTANCE;

    public static void registerRenderer(Item item, Supplier<IAccessoryRenderer> renderer) {
       IMPLEMENTATION.registerRenderer(item, renderer);
    }

    public static Optional<Container> getSlotContainer(LivingEntity entity, String slot) {
        return IMPLEMENTATION.getSlotContainer(entity, slot);
    }

    public static Optional<ItemStack> getStackInSlot(LivingEntity entity, SimpleSlot slot) {
        return getSlotContainer(entity, slot.name)
            .filter(c -> c.getContainerSize() > slot.index)
            .map(c -> c.getItem(slot.index));
    }

    public static Optional<SlotAccess> getSlotAccess(LivingEntity entity, SimpleSlot slot) {
        return getSlotContainer(entity, slot.name)
            .filter(c -> c.getContainerSize() > slot.index)
            .map(c -> SlotAccessHelper.make(() -> c.getItem(slot.index), s -> {c.setItem(slot.index, s);}));
    }

    public static boolean shouldRender(LivingEntity entity, SimpleSlot slot) {
        return IMPLEMENTATION.shouldRender(entity, slot);
    }

    public static Pair<SimpleSlot, ItemStack>[] findEquipped(LivingEntity entity, Predicate<ItemStack> filter) {
        return IMPLEMENTATION.findEquipped(entity, filter);
    }

    public static void breakEvent(LivingEntity entity, SimpleSlot slot) {
        IMPLEMENTATION.breakEvent(entity, slot);
    }

    public static void registerEvents() {
        IMPLEMENTATION.registerEvents();
    }

    public static void onCommonSetup() {
        IMPLEMENTATION.onCommonSetup();
    }

    public static void registerItem(Item item) {
        IMPLEMENTATION.registerItem(item);
    }
}
