package crazywoddman.atelier.api.templates;

import java.util.Optional;

import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.api.interfaces.InteractSound;
import crazywoddman.atelier.client.ClientUtils;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.gui.ClientContainerItemTooltip;
import crazywoddman.atelier.gui.ContainerItemTooltip;
import crazywoddman.atelier.network.packets.ContainerItemPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ContainerItem extends Item implements IContainerItem, IQuickAccess {
    public static final String CONTENTS_TAG = "Items";

    public ContainerItem(Properties properties) {
        super(properties);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new ContainerItemTooltip(stack));
    }

    public static Optional<ListTag> getTag(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(CONTENTS_TAG)
        ? Optional.of(tag.getList(CONTENTS_TAG, ListTag.TAG_COMPOUND))
        : Optional.empty();
    }

    @Override
    public int getContainerCapacity() {
        return 1;
    }

    @Override
    public int getVisibleSlots(ItemStack stack, ItemStack carried) {
        return getVisible(stack, carried);
    }

    public static int getVisible(ItemStack stack, ItemStack carried) {
        return getTag(stack)
            .map(tag -> carried.isEmpty() ? Math.max(tag.size(), 1) : Math.min(tag.size() + 1, IContainerItem.getCapacity(stack)))
            .orElse(1);
    }

    @Override
    public ItemStack[] getContainerItems(ItemStack stack) {
        return getItems(stack);
    }

    public static ItemStack[] getItems(ItemStack stack) {
        return getTag(stack)
            .map(list -> list.stream().map(tag -> ItemStack.of((CompoundTag)tag)).toArray(ItemStack[]::new))
            .orElse(new ItemStack[]{});
    }
    
    @Override
    public boolean insertAtIndex(ItemStack stack, ItemStack input, SlotAccess output, int index) {
        return insert(stack, input, output, index);
    }

    public static boolean insert(ItemStack stack, ItemStack input, SlotAccess output, int index) {
        if (input.isEmpty()) {
            CompoundTag tag = stack.getTag();
            
            if (tag != null) {
                ListTag items = tag.getList(CONTENTS_TAG, ListTag.TAG_COMPOUND);

                if (index < items.size()) {
                    ItemStack out = ItemStack.of((CompoundTag)items.remove(index));
                    
                    if (items.isEmpty())
                        tag.remove(CONTENTS_TAG);

                    if (tag.isEmpty())
                        stack.setTag(null);

                    output.set(out);
                    return true;
                }
            }
        } else {
            CompoundTag tag = stack.getOrCreateTag();
            ListTag items = tag.getList(CONTENTS_TAG, ListTag.TAG_COMPOUND);

            if (items.isEmpty()) {
                items.add(input.serializeNBT());
                input = ItemStack.EMPTY;
            } else {
                int size = items.size();
                boolean freeSlot = size < IContainerItem.getCapacity(stack);

                if (index == 0 && freeSlot) {
                    for (int i = 0; i < size; i++) {
                        ItemStack item = ItemStack.of(items.getCompound(i));
                        int count = item.getCount();
                        int canAdd = item.getMaxStackSize() - count;

                        if (canAdd > 0 && ItemStack.isSameItemSameTags(item, input)) {
                            int added = Math.min(canAdd, input.getCount());
                            item.grow(added);
                            items.setTag(i, item.serializeNBT());
                            input.shrink(added);

                            if (input.isEmpty())
                                break;
                        }
                    }
                    if (!input.isEmpty()) {
                        items.addTag(0, input.serializeNBT());
                        input = ItemStack.EMPTY;
                    }
                } else {
                    if (freeSlot)
                        index--;
                    
                    ItemStack item = ItemStack.of(items.getCompound(index));

                    if (ItemStack.isSameItemSameTags(item, input)) {
                        int count = item.getCount();
                        int canAdd = item.getMaxStackSize() - count;

                        if (canAdd > 0) {
                            int added = Math.min(canAdd, input.getCount());
                            item.grow(added);
                            items.setTag(index, item.serializeNBT());
                            input.shrink(added);
                        }
                    } else {
                        items.set(index, input.serializeNBT());
                        input = item;
                    }
                }
            }

            if (!tag.contains(CONTENTS_TAG))
                tag.put(CONTENTS_TAG, items);

            output.set(input);
            return true;
        }

        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack carried, Slot slot, ClickAction click, Player player, SlotAccess carriedAccess) {
        return clickedOn(stack, carried, slot, click, player, carriedAccess)
            || super.overrideOtherStackedOnMe(stack, carried, slot, click, player, carriedAccess);
    }

    public static boolean clickedOn(ItemStack stack, ItemStack carried, Slot slot, ClickAction click, Player player, SlotAccess carriedAccess) {
        if (click == ClickAction.SECONDARY && slot.allowModification(player) && IContainerItem.get(carried.getItem()).isEmpty()) {
            if (player.isLocalPlayer() && IContainerItem.insert(stack, carried, carriedAccess, ClientContainerItemTooltip.chosen)) {
                if (stack.getItem() instanceof InteractSound interact)
                    interact.interactSound().playEquip(player, player);

                if (!ClientUtils.creativeInventoryOpen())
                    ContainerItemPacket.send(slot);

                if (carriedAccess.get().isEmpty() && ClientContainerItemTooltip.chosen > 0)
                    ClientContainerItemTooltip.chosen--;

            }
            return true;
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) {
            insertAtIndex(player.getItemInHand(hand), player.getItemBySlot(EquipmentSlot.MAINHAND), SlotAccess.forEquipmentSlot(player, EquipmentSlot.MAINHAND), 0);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean quickAccess(Player player, ItemStack stack) {
        return insertAtIndex(stack, player.getItemBySlot(EquipmentSlot.MAINHAND), SlotAccess.forEquipmentSlot(player, EquipmentSlot.MAINHAND), 0);
    }

    @Override
    public ItemStack quickAccessPreview(ItemStack stack) {
        return preview(stack);
    }
    
    public static ItemStack preview(ItemStack stack) {
        return getTag(stack)
            .map(list -> ItemStack.of(list.getCompound(0)))
            .orElse(ItemStack.EMPTY);
    }
}