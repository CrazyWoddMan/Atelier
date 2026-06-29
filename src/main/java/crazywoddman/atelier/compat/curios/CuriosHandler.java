package crazywoddman.atelier.compat.curios;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.compat.CompatHandler;
import crazywoddman.atelier.data.ArmorPlates;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

public class CuriosHandler extends CompatHandler {
    public static final CompatHandler INSTANCE = new CuriosHandler();

    static SimpleSlot slotOf(SlotContext context) {
        return SimpleSlot.of(context.identifier(), context.index());
    }

    static SlotContext slotOf(SimpleSlot slot, LivingEntity entity) {
        return new SlotContext(slot.name, entity, slot.index, false, true);
    }

    @Override
    public void registerRenderer(Item item, Supplier<IAccessoryRenderer> renderer) {
        if (renderer != null) {
            CuriosRendererRegistry.register(
                item,
                () -> new CurioRendererImpl(renderer)
            );
        }
    }

    @Override
    public Optional<Container> getSlotContainer(LivingEntity entity, String slot) {
        return CuriosApi.getCuriosInventory(entity).resolve().flatMap(
            cap -> cap.getStacksHandler(slot).map(handler -> new CuriosContainer(handler.getStacks()))
        );
    }

    @Override
    public boolean shouldRender(LivingEntity entity, SimpleSlot slot) {
        return CuriosApi.getCuriosInventory(entity).resolve().get().getStacksHandler(slot.name).get().getRenders().get(slot.index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<SimpleSlot, ItemStack>[] findEquipped(LivingEntity entity, Predicate<ItemStack> filter) {
        return CuriosApi
            .getCuriosInventory(entity)
            .map(cap -> cap.findCurios(filter).stream().map(r -> Pair.of(slotOf(r.slotContext()), r.stack())).toArray(Pair[]::new))
            .orElse(new Pair[]{});
    }

    @Override
    public void breakEvent(LivingEntity entity, SimpleSlot slot) {
        CuriosApi.broadcastCurioBreakEvent(slotOf(slot, entity));
    }

    @Override
    public void registerEvents() {
        MinecraftForge.EVENT_BUS.addListener((CurioEquipEvent event) -> {
            SlotContext context = event.getSlotContext();
            
            if (!canEquipWearable(event.getStack(), slotOf(context), context.entity()))
                event.setResult(Result.DENY);
        });
        MinecraftForge.EVENT_BUS.addListener((CurioChangeEvent event) ->
            onWearableChange(event.getFrom(), event.getTo(), SimpleSlot.of(event.getIdentifier(), event.getSlotIndex()), event.getEntity())
        );
    }

    @Override
    public void onCommonSetup() {
        CuriosApi.registerCurioPredicate(
            ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "armor_plate"),
            context -> ArmorPlates.isPlate(context.stack().getItem())
        );
    }

    @Override
    public void registerItem(Item item) {
        if (item instanceof IWearableAccessory accessory)
            CuriosApi.registerCurio(item, new CurioItemImpl(accessory));
    }
}
