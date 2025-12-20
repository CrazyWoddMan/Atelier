package crazywoddman.atelier.api.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

// TODO doesn't work
public interface IBundleItem {
    String TAG_ITEMS = "Items";
    int DEFAULT_MAX_WEIGHT = 64;
    int BUNDLE_IN_BUNDLE_WEIGHT = 4;
    int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    default int getMaxWeight() {
        return DEFAULT_MAX_WEIGHT;
    }

    default int getBundleBarColor() {
        return BAR_COLOR;
    }

    default boolean bundleOverrideStackedOnOther(ItemStack bundle, Slot slot, ClickAction action, Player player) {
        if (bundle.getCount() != 1 || action != ClickAction.SECONDARY) {
            return false;
        }
        
        ItemStack slotStack = slot.getItem();
        
        if (slotStack.isEmpty()) {
            playRemoveOneSound(player);
            removeOne(bundle).ifPresent(removed -> {
                add(bundle, slot.safeInsert(removed));
            });
        } else if (slotStack.getItem().canFitInsideContainerItems()) {
            int maxWeight = getMaxWeight();
            int availableWeight = maxWeight - getContentWeight(bundle);
            int itemWeight = getWeight(slotStack);
            int maxCount = availableWeight / itemWeight;
            
            int added = add(bundle, slot.safeTake(slotStack.getCount(), maxCount, player));
            
            if (added > 0) {
                playInsertSound(player);
            }
        }
        
        return true;
    }

    default boolean bundleOverrideOtherStackedOnMe(ItemStack bundle, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (bundle.getCount() != 1) return false;
        
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (other.isEmpty()) {
                removeOne(bundle).ifPresent(removed -> {
                    playRemoveOneSound(player);
                    access.set(removed);
                });
            } else {
                int added = add(bundle, other);
                
                if (added > 0) {
                    playInsertSound(player);
                    other.shrink(added);
                }
            }
            
            return true;
        }
        
        return false;
    }

    default InteractionResultHolder<ItemStack> bundleUse(Level level, Player player, InteractionHand hand) {
        ItemStack bundle = player.getItemInHand(hand);
        
        if (dropContents(bundle, player)) {
            playDropContentsSound(player);
            return InteractionResultHolder.sidedSuccess(bundle, level.isClientSide());
        }
        
        return InteractionResultHolder.fail(bundle);
    }

    default boolean bundleIsBarVisible(ItemStack bundle) {
        return getContentWeight(bundle) > 0;
    }

    default int bundleGetBarWidth(ItemStack bundle) {
        int maxWeight = getMaxWeight();
        return Math.min(1 + 12 * getContentWeight(bundle) / maxWeight, 13);
    }

    default int bundleGetBarColor(ItemStack bundle) {
        return getBundleBarColor();
    }

    default Optional<TooltipComponent> bundleGetTooltipImage(ItemStack bundle) {
        NonNullList<ItemStack> contents = NonNullList.create();
        getContents(bundle).forEach(contents::add);
        return Optional.of(new BundleTooltip(contents, getContentWeight(bundle)));
    }

    default void bundleAppendHoverText(ItemStack bundle, Level level, List<Component> tooltip, TooltipFlag flag) {
        int weight = getContentWeight(bundle);
        int maxWeight = getMaxWeight();
        tooltip.add(
            Component.translatable("item.minecraft.bundle.fullness", weight, maxWeight)
                .withStyle(ChatFormatting.GRAY)
        );
    }

    default void bundleOnDestroyed(ItemEntity itemEntity) {
        getContents(itemEntity.getItem()).forEach(stack -> {
            ItemEntity drop = new ItemEntity(
                itemEntity.level(),
                itemEntity.getX(),
                itemEntity.getY(),
                itemEntity.getZ(),
                stack
            );
            itemEntity.level().addFreshEntity(drop);
        });
    }

    default int add(ItemStack bundle, ItemStack toAdd) {
        if (toAdd.isEmpty() || !toAdd.getItem().canFitInsideContainerItems()) {
            return 0;
        }

        CompoundTag tag = bundle.getOrCreateTag();
        if (!tag.contains(TAG_ITEMS)) {
            tag.put(TAG_ITEMS, new ListTag());
        }

        int currentWeight = getContentWeight(bundle);
        int itemWeight = getWeight(toAdd);
        int maxCount = Math.min(toAdd.getCount(), (getMaxWeight() - currentWeight) / itemWeight);
        
        if (maxCount == 0) {
            return 0;
        }

        ListTag items = tag.getList(TAG_ITEMS, 10);
        Optional<CompoundTag> existing = getMatchingItem(toAdd, items);

        if (existing.isPresent()) {
            CompoundTag existingTag = existing.get();
            ItemStack existingStack = ItemStack.of(existingTag);
            existingStack.grow(maxCount);
            existingStack.save(existingTag);
            items.remove(existingTag);
            items.add(0, existingTag);
        } else {
            ItemStack newStack = toAdd.copyWithCount(maxCount);
            CompoundTag newTag = new CompoundTag();
            newStack.save(newTag);
            items.add(0, newTag);
        }

        return maxCount;
    }

    default Optional<ItemStack> removeOne(ItemStack bundle) {
        CompoundTag tag = bundle.getOrCreateTag();
        
        if (!tag.contains(TAG_ITEMS)) {
            return Optional.empty();
        }

        ListTag items = tag.getList(TAG_ITEMS, 10);
        
        if (items.isEmpty()) {
            return Optional.empty();
        }

        CompoundTag firstItem = items.getCompound(0);
        ItemStack removed = ItemStack.of(firstItem);
        items.remove(0);

        if (items.isEmpty()) {
            bundle.removeTagKey(TAG_ITEMS);
        }

        return Optional.of(removed);
    }

    default boolean dropContents(ItemStack bundle, Player player) {
        CompoundTag tag = bundle.getOrCreateTag();
        
        if (!tag.contains(TAG_ITEMS)) {
            return false;
        }

        if (player instanceof ServerPlayer) {
            ListTag items = tag.getList(TAG_ITEMS, 10);

            for (int i = 0; i < items.size(); i++) {
                CompoundTag itemTag = items.getCompound(i);
                ItemStack stack = ItemStack.of(itemTag);
                player.drop(stack, true);
            }
        }

        bundle.removeTagKey(TAG_ITEMS);
        return true;
    }

    default Stream<ItemStack> getContents(ItemStack bundle) {
        CompoundTag tag = bundle.getTag();
        
        if (tag == null) {
            return Stream.empty();
        }

        ListTag items = tag.getList(TAG_ITEMS, 10);
        return items.stream()
            .map(CompoundTag.class::cast)
            .map(ItemStack::of);
    }

    default int getContentWeight(ItemStack bundle) {
        return getContents(bundle)
            .mapToInt(stack -> getWeight(stack) * stack.getCount())
            .sum();
    }

    default int getWeight(ItemStack stack) {
        if (stack.getItem() instanceof IBundleItem bundleItem)
            return BUNDLE_IN_BUNDLE_WEIGHT + bundleItem.getContentWeight(stack);
        
        if ((stack.is(Items.BEEHIVE) || stack.is(Items.BEE_NEST)) && stack.hasTag()) {
            CompoundTag beData = stack.getTagElement("BlockEntityTag");

            if (beData != null && !beData.getList("Bees", 10).isEmpty())
                return getMaxWeight();
        }

        return getMaxWeight() / stack.getMaxStackSize();
    }

    default Optional<CompoundTag> getMatchingItem(ItemStack stack, ListTag items) {
        if (stack.getItem() instanceof IBundleItem)
            return Optional.empty();

        return items.stream()
            .filter(CompoundTag.class::isInstance)
            .map(CompoundTag.class::cast)
            .filter(tag -> ItemStack.isSameItemSameTags(ItemStack.of(tag), stack))
            .findFirst();
    }

    default float getFullnessDisplay(ItemStack bundle) {
        return (float)getContentWeight(bundle) / (float)getMaxWeight();
    }

    default void playRemoveOneSound(Entity entity) {
        entity.playSound(
            SoundEvents.BUNDLE_REMOVE_ONE,
            0.8F,
            0.8F + entity.level().getRandom().nextFloat() * 0.4F
        );
    }

    default void playInsertSound(Entity entity) {
        entity.playSound(
            SoundEvents.BUNDLE_INSERT,
            0.8F,
            0.8F + entity.level().getRandom().nextFloat() * 0.4F
        );
    }

    default void playDropContentsSound(Entity entity) {
        entity.playSound(
            SoundEvents.BUNDLE_DROP_CONTENTS,
            0.8F,
            0.8F + entity.level().getRandom().nextFloat() * 0.4F
        );
    }
}