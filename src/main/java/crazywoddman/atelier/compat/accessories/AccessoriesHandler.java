package crazywoddman.atelier.compat.accessories;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.compat.CompatHandler;
import crazywoddman.atelier.data.ArmorPlates;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.events.AccessoryChangeCallback;
import io.wispforest.accessories.api.events.CanEquipCallback;
import io.wispforest.accessories.api.slot.SlotBasedPredicate;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AccessoriesHandler extends CompatHandler {
    public static final CompatHandler INSTANCE = new AccessoriesHandler();

    static SimpleSlot slotOf(SlotReference reference) {
        return SimpleSlot.of(reference.slotName(), reference.slot());
    }

    static SlotReference slotOf(SimpleSlot slot, LivingEntity entity) {
        return SlotReference.of(entity, slot.name, slot.index);
    }

    @Override
    public void registerRenderer(Item item, Supplier<IAccessoryRenderer> renderer) {
        if (renderer == null) {
            AccessoriesRendererRegistry.registerNoRenderer(item);
        } else AccessoriesRendererRegistry.registerRenderer(
            item,
            () -> new AccessoryRendererImpl(renderer)
        );
    }

    @Override
    public Optional<Container> getSlotContainer(LivingEntity entity, String slot) {
        return AccessoriesCapability.getOptionally(entity).flatMap(cap -> 
            Optional.ofNullable(cap.getContainer(new SlotTypeReference(slot))).map(container -> container.getAccessories())
        );
    }

    @Override
    public boolean shouldRender(LivingEntity entity, SimpleSlot slot) {
        return AccessoriesCapability.get(entity).getContainer(new SlotTypeReference(slot.name)).shouldRender(slot.index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<SimpleSlot, ItemStack>[] findEquipped(LivingEntity entity, Predicate<ItemStack> filter) {
        return AccessoriesCapability
            .getOptionally(entity)
            .map(cap -> cap.getEquipped(filter).stream().map(r -> Pair.of(slotOf(r.reference()), r.stack())).toArray(Pair[]::new))
            .orElse(new Pair[]{});
    }

    @Override
    public void breakEvent(LivingEntity entity, SimpleSlot slot) {
        AccessoriesAPI.breakStack(slotOf(slot, entity));
    }

    @Override
    public void registerEvents() {
        CanEquipCallback.EVENT.register((stack, reference) ->
            canEquipWearable(stack, slotOf(reference), reference.entity()) ? TriState.DEFAULT : TriState.FALSE
        );
        AccessoryChangeCallback.EVENT.register((prevStack, newStack, reference, state) ->
            onWearableChange(prevStack, newStack, slotOf(reference), reference.entity())
        );
    }

    @Override
    public void onCommonSetup() {
        AccessoriesAPI.registerPredicate(
            ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "armor_plate"),
            SlotBasedPredicate.ofItem(ArmorPlates::isPlate)
        );
    }

    @Override
    public void registerItem(Item item) {
        if (item instanceof IWearableAccessory accessory)
            AccessoriesAPI.registerAccessory(item, new AccessoryImpl(accessory));
    }
}
