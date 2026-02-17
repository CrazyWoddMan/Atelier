package crazywoddman.atelier.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import crazywoddman.atelier.AtelierSounds;
import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.blocks.AtelierBlocks;
import crazywoddman.atelier.blocks.SewingTable;
import crazywoddman.atelier.blocks.SewingTableBlockEntity;
import crazywoddman.atelier.events.AccessoriesEvents;
import crazywoddman.atelier.recipes.AtelierRecipes;
import crazywoddman.atelier.recipes.SewingRecipe;
import crazywoddman.atelier.recipes.SewingRecipe.CountableIngredient;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class SewingTableMenu extends AbstractContainerMenu {
    public static final int SPOOL_SLOT = 0;
    public static final int MODIFICATION_SLOT = SPOOL_SLOT + 1;
    public static final int INGREDIENTS_START = MODIFICATION_SLOT + 1;
    public static final int RESULT_SLOT = INGREDIENTS_START + 1;
    public static final int INVENTORY_START = RESULT_SLOT + 1;
    public static final int HOTBAR_START = INVENTORY_START + 27;
    public static final int INVENTORY_END = INVENTORY_START + 35;
    public static final int SPOOL_SLOT_X = 7;
    public static final int SPOOL_SLOT_Y = 26;

    private final ContainerLevelAccess access;
    private final Level level;
    private final Inventory inventory;
    private final Slot resultSlot;
    public final Container ingredientSlots;
    private final Container modification;
    private final Slot modificationSlot;
    protected List<String> modificationModules = null;
    private final SewingTableBlockEntity sewingTable;
    protected final List<SewingRecipe> recipes;

    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private Runnable slotUpdateListener = () -> {};

    public SewingTableMenu(int id, Inventory playerInventory, BlockPos pos) {
        super(AtelierMenuTypes.SEWING_TABLE.get(), id);
        this.inventory = playerInventory;
        this.level = playerInventory.player.level();
        BlockState state = level.getBlockState(pos);
        
        if (!(state.getBlock() instanceof SewingTable))
            throw new IllegalStateException("Block at " + pos + " is not a " + SewingTable.class.getSimpleName());

        BlockEntity blockEntity = switch (state.getValue(SewingTable.PART)) {
            case LEFT -> level.getBlockEntity(pos);
            case RIGHT -> level.getBlockEntity(pos.relative(state.getValue(SewingTable.FACING).getClockWise()));
            case MACHINE -> level.getBlockEntity(pos.below());
        };

        if (!(blockEntity instanceof SewingTableBlockEntity sewingTable))
            throw new IllegalStateException("BlockEntity at " + pos.toShortString() + " is not a " + SewingTableBlockEntity.class.getSimpleName());

        this.sewingTable = sewingTable;
        this.selectedRecipeIndex.set(-1);
        this.access = ContainerLevelAccess.create(playerInventory.player.level(), pos);
        this.recipes = level
            .getRecipeManager()
            .getAllRecipesFor(AtelierRecipes.SEWING_RECIPE_TYPE.get())
            .stream()
            .sorted((r1, r2) -> r1.getId().compareTo(r2.getId()))
            .toList();

        // Spool
        addSlot(new SlotItemHandler(this.sewingTable.getSpoolInventory(), 0, SPOOL_SLOT_X, SPOOL_SLOT_Y) {
            @Override
            public void setChanged() {
                super.setChanged();
                updateResult();
            }

            public boolean mayPlace(ItemStack stack) {
                return super.mayPlace(stack) && ((getRecipeIndex() == -1 || getRecipe().spool.isEmpty()) ? AtelierRecipes.SPOOL_ITEMS.contains(stack.getItem()) : getRecipe().spool.test(stack));
            };
        });

        // Modification
        this.modification = new SimpleContainer(1);
        this.modificationSlot = addSlot(new ResultChangingSlot(modification, 0, 59, 26) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                updateIndex(-1);

                if (hasItem() && modificationModules == null) {
                    ItemStack stack = getItem();
                    Map<String, Integer> modules = IModular.getModules(stack.getItem());
                    modificationModules = new ArrayList<>();
                    modules.keySet()
                        .stream()
                        .sorted()
                        .forEach(key -> {
                            for (int i = 0; i < modules.get(key); i++)
                                modificationModules.add(key);
                        });
                    Optional.ofNullable(stack.getTag()).map(tag -> tag.getCompound("modules")).ifPresent(tag -> {
                        for (String module : tag.getAllKeys()) {
                            ListTag items = tag.getList(module, ListTag.TAG_COMPOUND);

                            for (int i = 0; i < items.size(); i++)
                                ingredientSlots.setItem(modificationModules.indexOf(module) + i, ItemStack.of(items.getCompound(i)));
                        }
                    });
                }
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return super.isActive() && IModular.isModular(stack.getItem());
            }

            @Override
            public boolean isActive() {
                return !resultSlot.isActive() && super.isActive();
            }

            public void onTake(Player player, ItemStack stack) {
                modificationModules = null;
                ingredientSlots.clearContent();
            };
        });

        // Ingredients
        this.ingredientSlots = new SimpleContainer(9);
        for (int i = 0; i < 9; i++) {
            int ind = i;
            addSlot(new Slot(ingredientSlots, i, 8 + i * 18, 57) {
                private final int localIndex = ind;

                public void setChanged() {
                    super.setChanged();

                    if (modificationModules != null) {
                        String slot = modificationModules.get(this.localIndex);
                        AccessoriesEvents.writeToCompound(modificationSlot.getItem(), getItem(), slot, this.localIndex - modificationModules.indexOf(slot));
                    } else
                        updateResult();
                };

                @Override
                public boolean mayPlace(ItemStack stack) {
                    if (!super.mayPlace(stack))
                        return false;

                    if (modificationModules != null) {
                        if (this.localIndex >= modificationModules.size())
                            return false;

                        String slot = modificationModules.get(this.localIndex);
                        return AccessoriesAPI.canInsertIntoSlot(stack, SlotReference.of(inventory.player, slot, this.localIndex - modificationModules.indexOf(slot)));
                    }

                    int selectedIndex = getRecipeIndex();
                    
                    if (selectedIndex == -1)
                        return false;

                    List<CountableIngredient> ingredients = getRecipe(selectedIndex).ingredients;
                    
                    return ingredients.size() > this.localIndex && ingredients.get(this.localIndex).test(stack);
                }
            });
        }

        // Result
        this.resultSlot = addSlot(new Slot(new SimpleContainer(1), 0, 59, 26) {
            @Override
            public boolean isActive() {
                return super.isActive() && !this.getItem().isEmpty() && modification.isEmpty();
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                boolean mayPickup = super.mayPickup(player) && hasIngredients();
                return mayPickup;

            }

            @Override
            public void onTake(Player player, ItemStack item) {
                SewingRecipe recipe = getRecipe();
                sewingTable.getSpoolStack().shrink(recipe.spool.getCount());

                for (CountableIngredient ingredient : recipe.ingredients) {
                    if (ingredient.isEmpty())
                        continue;
                        
                    int remaining = ingredient.getCount();
                    
                    for (int i = INGREDIENTS_START; i < RESULT_SLOT && remaining > 0; i++) {
                        ItemStack stack = slots.get(i).getItem();
                        
                        if (ingredient.test(stack)) {
                            int toRemove = Math.min(remaining, stack.getCount());
                            stack.shrink(toRemove);
                            remaining -= toRemove;
                        }
                    }
                }

                updateResult();
                player.level().playSound(player, sewingTable.getBlockPos(), AtelierSounds.SEWING_MACHINE.get(), SoundSource.BLOCKS, 1, 0.9f + player.getRandom().nextFloat() * 0.1f);
                super.onTake(player, item);
            }
        });

        // Inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(
                    playerInventory,
                    col + row * 9 + 9,
                    8 + col * 18,
                    84 + row * 18
                ));
        }

        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(
                playerInventory,
                col,
                8 + col * 18,
                142
            ));
        }

        this.addDataSlot(this.selectedRecipeIndex);
    }

    public int getAvailableCount(Ingredient ingredient) {
        int available = 0;

        for (int i = INVENTORY_START; i <= INVENTORY_END; i++) {
            ItemStack stack = this.slots.get(i).getItem();

            if (ingredient.test(stack))
                available += stack.getCount();
        }
        
        return available;
    }

    public boolean hasIngredients() {
        int selectedIndex = getRecipeIndex();
        
        if (selectedIndex == -1)
            return false;

        SewingRecipe recipe = getRecipe(selectedIndex);

        if (!recipe.spool.isEmpty()) {
            ItemStack spool = sewingTable.getSpoolStack();

            if (!recipe.spool.test(spool) || spool.getCount() < recipe.spool.getCount())
                return false;
        }
        
        for (CountableIngredient required : recipe.ingredients) {
            if (required.isEmpty())
                continue;
                
            int totalCount = 0;
            
            for (int i = INGREDIENTS_START; i < RESULT_SLOT; i++) {
                ItemStack stack = this.slots.get(i).getItem();

                if (required.test(stack))
                    totalCount += stack.getCount();
            }
            
            if (totalCount < required.getCount())
                return false;
        }
        
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {        
        Slot slot = this.slots.get(index);
        
        if (!slot.hasItem())
            return ItemStack.EMPTY;
        
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        
        if (index == RESULT_SLOT || index == MODIFICATION_SLOT) {
            if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END + 1, true))
                return ItemStack.EMPTY;
            
            if (index == RESULT_SLOT) {
                slot.onQuickCraft(stack, original);
                
                if (stack.isEmpty())
                    slot.setByPlayer(ItemStack.EMPTY);
                else
                    slot.setChanged();
                    
                if (stack.getCount() == original.getCount())
                    return ItemStack.EMPTY;
                    
                slot.onTake(player, stack);
            }
        } else if (index >= INGREDIENTS_START && index < INVENTORY_START) {
            if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END + 1, false))
                return ItemStack.EMPTY;
        } else if (index == SPOOL_SLOT) {
            if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END + 1, false))
                return ItemStack.EMPTY;
        } else if (!this.moveItemStackTo(stack, SPOOL_SLOT, RESULT_SLOT, false)) {
            if (index < HOTBAR_START) {
                if (!this.moveItemStackTo(stack, HOTBAR_START, INVENTORY_END + 1, false))
                    return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(stack, INVENTORY_START, HOTBAR_START, false))
                return ItemStack.EMPTY;
        }
        
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, AtelierBlocks.SEWING_TABLE.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        this.access.execute((level, pos) -> {
            clearContainer(player, this.modification);
            clearContainer(player, this.ingredientSlots);
        });
    }

    public int getRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public SewingRecipe getRecipe(int index) {
        return this.recipes.get(index);
    }

    public SewingRecipe getRecipe() {
        return getRecipe(getRecipeIndex());
    }

    private void updateIndex(int index) {
        if (index < -1 || index >= this.recipes.size() || index == getRecipeIndex())
            return;

        this.selectedRecipeIndex.set(index);

        for (int i = 0; i < ingredientSlots.getContainerSize(); i++ ) {
            ItemStack item = ingredientSlots.getItem(i);

            if (!item.isEmpty() && !getSlot(INGREDIENTS_START + i).mayPlace(item))
                this.inventory.placeItemBackInInventory(ingredientSlots.removeItemNoUpdate(i));
        }

        if (index == -1) {
            if (this.resultSlot.hasItem())
                this.resultSlot.set(ItemStack.EMPTY);
        } else {
            ItemStack spool = sewingTable.getSpoolStack();

            if (!spool.isEmpty()) {
                Ingredient required = getRecipe(index).spool.asIngredient();

                if (!required.isEmpty() && !required.test(spool))
                    this.inventory.placeItemBackInInventory(sewingTable.getSpoolInventory().extractItem(0, spool.getCount(), false));
            }

            updateResult(index);
        }
    }

    private void updateResult() {
        updateResult(getRecipeIndex());
        slotUpdateListener.run();
    }

    private void updateResult(int index) {
        if (hasIngredients()) {
            ItemStack result = getRecipe(index).getResultItem(this.level.registryAccess());

            if (result.getItem() instanceof IDyable dyable)
                calculateColor().ifPresent(color -> dyable.setColor(result, color));

            this.resultSlot.set(result);
        } else if (this.resultSlot.hasItem())
            this.resultSlot.set(ItemStack.EMPTY);
    }

    @Override
    public boolean clickMenuButton(Player player, int index) {
        updateIndex(index == getRecipeIndex() ? -1 : index);
        return true;
    }

    public void registerUpdateListener(Runnable runnable) {
        this.slotUpdateListener = runnable;
    }

    // TODO: Needs rework
    private static Optional<Integer> blendDyeColors(DyeColor color) {
        if (color == null)
            return Optional.empty();
        
        int[] RGB = new int[3];
        int maxColorSum = 0;
        int totalColors = 0;
        
        float[] floatRGB = color.getTextureDiffuseColors();

        int red = (int)(floatRGB[0] * 255.0F);
        int green = (int)(floatRGB[1] * 255.0F);
        int blue = (int)(floatRGB[2] * 255.0F);
        
        maxColorSum += Math.max(red, Math.max(green, blue));

        RGB[0] += red;
        RGB[1] += green;
        RGB[2] += blue;
        
        totalColors++;
        
        int avarageRed = RGB[0] / totalColors;
        int avarageGreen = RGB[1] / totalColors;
        int avarageBlue = RGB[2] / totalColors;
        
        float avarageMaxColor = (float) maxColorSum / totalColors;
        float actualMaxColor = (float) Math.max(avarageRed, Math.max(avarageGreen, avarageBlue));
        
        avarageRed = (int) ((float) avarageRed * avarageMaxColor / actualMaxColor);
        avarageGreen = (int) ((float) avarageGreen * avarageMaxColor / actualMaxColor);
        avarageBlue = (int) ((float) avarageBlue * avarageMaxColor / actualMaxColor);
        
        return Optional.of((avarageRed << 16) | (avarageGreen << 8) | avarageBlue);
    }

    private Optional<Integer> calculateColor() {
        ItemStack stack = this.ingredientSlots.getItem(0);
        Item item = stack.getItem();

        return item instanceof DyeableLeatherItem dyable ? Optional.of(dyable.getColor(stack)) : blendDyeColors(Arrays.stream(DyeColor.values())
            .filter(dyeColor -> ForgeRegistries.ITEMS.getKey(item).getPath().contains(dyeColor.name().toLowerCase()))
            .findFirst()
            .orElse(null));
    }

    private class ResultChangingSlot extends Slot {
        public ResultChangingSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            updateResult();
        }
    }
}