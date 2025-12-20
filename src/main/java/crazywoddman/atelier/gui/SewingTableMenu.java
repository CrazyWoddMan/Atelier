package crazywoddman.atelier.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.blocks.AtelierBlocks;
import crazywoddman.atelier.blocks.SewingTable;
import crazywoddman.atelier.blocks.SewingTableBlockEntity;
import crazywoddman.atelier.recipes.AtelierRecipes;
import crazywoddman.atelier.recipes.SewingRecipe;
import crazywoddman.atelier.recipes.SewingRecipe.CountableIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class SewingTableMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final Level level;
    private final Slot resultSlot;
    private final Container modificationSlot;
    private final SewingTableBlockEntity blockEntity;
    protected final List<SewingRecipe> recipes;

    public final static int SPOOL_SLOT = 0;
    public final static int MODIFICATION_SLOT = 1;
    public final static int RESULT_SLOT = 2;
    public final static int INVENTORY_START = 3;
    public final static int HOTBAR_START = INVENTORY_START + 27;
    public final static int INVENTORY_END = INVENTORY_START + 35;

    public final static int SPOOL_SLOT_X = 7;
    public final static int SPOOL_SLOT_Y = 26;

    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private Runnable slotUpdateListener = () -> {};

    public SewingTableMenu(int id, Inventory playerInventory, BlockPos pos) {
        super(AtelierMenuTypes.SEWING_TABLE.get(), id);
        this.level = playerInventory.player.level();
        BlockState state = level.getBlockState(pos);
        
        if (!(state.getBlock() instanceof SewingTable))
            throw new IllegalStateException("Block at " + pos + " is not a " + SewingTable.class.getSimpleName());

        BlockEntity blockEntity = switch (state.getValue(SewingTable.PART)) {
            case LEFT -> level.getBlockEntity(pos);
            case RIGHT -> level.getBlockEntity(pos.relative(state.getValue(SewingTable.FACING).getClockWise()));
            case MACHINE -> level.getBlockEntity(pos.below());
        };

        if (!(blockEntity instanceof SewingTableBlockEntity))
            throw new IllegalStateException("BlockEntity at " + pos.toShortString() + " is not a " + SewingTableBlockEntity.class.getSimpleName());

        this.blockEntity = (SewingTableBlockEntity) blockEntity;
        this.selectedRecipeIndex.set(-1);
        this.access = ContainerLevelAccess.create(playerInventory.player.level(), pos);
        this.recipes = level
            .getRecipeManager()
            .getAllRecipesFor(AtelierRecipes.SEWING_RECIPE_TYPE.get())
            .stream()
            .sorted((r1, r2) -> r1.getId().compareTo(r2.getId()))
            .toList();

        // Spool
        addSlot(new SlotItemHandler(this.blockEntity.getSpoolInventory(), 0, SPOOL_SLOT_X, SPOOL_SLOT_Y) {
            @Override
            public void setChanged() {
                super.setChanged();
                updateResult();
            }
        });

        this.modificationSlot = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                slotUpdateListener.run();
            }
        };
        // Modification
        addSlot(new ResultChangingSlot(modificationSlot, 0, 59, 26) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return IModular.isModular(stack.getItem());
            }

            @Override
            public boolean isActive() {
                return !resultSlot.isActive() && super.isActive();
            }
        });

        // Result
        this.resultSlot = addSlot(new Slot(new SimpleContainer(1), 0, 59, 26) {
            @Override
            public boolean isActive() {
                return super.isActive() && !this.getItem().isEmpty() && modificationSlot.isEmpty();
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
            public void onTake(Player player, ItemStack stack) {
                craftItem();
                super.onTake(player, stack);
                System.out.println("Result taken");
            }
        });

        // Inventory
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new ResultChangingSlot(
                    playerInventory,
                    col + row * 9 + 9,
                    8 + col * 18,
                    84 + row * 18
                ));

        // Hotbar
        for (int col = 0; col < 9; col++)
            addSlot(new ResultChangingSlot(
                playerInventory,
                col,
                8 + col * 18,
                142
            ));

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

    // TODO for the future
    @SuppressWarnings("unused")
    private Optional<Integer> calculateColor() {
        int selectedIndex = this.selectedRecipeIndex.get();
        SewingRecipe recipe = this.recipes.get(selectedIndex);
        List<DyeColor> colors = new ArrayList<>();

        for (CountableIngredient ingredient : recipe.getCountableIngredients()) {
            for (int i = INVENTORY_START; i <= INVENTORY_END; i++) {
                ItemStack stack = this.slots.get(i).getItem();

                if (ingredient.test(stack)) {
                    String itemID = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();
                    Arrays
                    .stream(DyeColor.values())
                    .filter(dyeColor -> itemID.contains(dyeColor.name().toLowerCase()))
                    .findFirst()
                    .ifPresent(colors::add);
                } 
            }
        }
        
        return blendDyeColors(colors);
    }

    // TODO for the future
    private static Optional<Integer> blendDyeColors(List<DyeColor> colors) {
        if (colors.isEmpty())
            return Optional.empty();
        
        int[] RGB = new int[3];
        int maxColorSum = 0;
        int totalColors = 0;
        
        for (DyeColor dyeColor : colors) {
            float[] floatRGB = dyeColor.getTextureDiffuseColors();

            int red = (int)(floatRGB[0] * 255.0F);
            int green = (int)(floatRGB[1] * 255.0F);
            int blue = (int)(floatRGB[2] * 255.0F);
            
            maxColorSum += Math.max(red, Math.max(green, blue));

            RGB[0] += red;
            RGB[1] += green;
            RGB[2] += blue;
            
            totalColors++;
        }
        
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

    public boolean hasIngredients() {
        int selectedIndex = this.selectedRecipeIndex.get();
        SewingRecipe recipe = this.recipes.get(selectedIndex);
        CountableIngredient requiredSpool = recipe.getSpool();

        if (!requiredSpool.isEmpty()) {
            ItemStack spool = blockEntity.getSpoolStack();

            if (!requiredSpool.test(spool) || spool.getCount() < requiredSpool.getCount())
                return false;
        }
        
        for (CountableIngredient ingredient : recipe.getCountableIngredients()) {
            if (ingredient.isEmpty())
                continue;
                
            if (getAvailableCount(ingredient.asIngredient()) < ingredient.getCount())
                return false;
        }
        
        System.out.println("Enough ingredients!");
        return true;
    }
    
    private void craftItem() {
        SewingRecipe recipe = this.recipes.get(this.selectedRecipeIndex.get());
        
        ItemStack spoolStack = blockEntity.getSpoolStack();
        spoolStack.shrink(recipe.getSpool().getCount());

        for (CountableIngredient ingredient : recipe.getCountableIngredients()) {
            if (ingredient.isEmpty())
                continue;
                
            int remaining = ingredient.getCount();
            
            for (int i = INVENTORY_START; i <= INVENTORY_END && remaining > 0; i++) {
                ItemStack stack = this.slots.get(i).getItem();
                
                if (ingredient.test(stack)) {
                    int toRemove = Math.min(remaining, stack.getCount());
                    stack.shrink(toRemove);
                    remaining -= toRemove;
                }
            }
        }

        updateResult();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        
        if (!slot.hasItem())
            return ItemStack.EMPTY;
        
        ItemStack stack = slot.getItem();
        
        if (index == RESULT_SLOT) {
            if (slot.mayPickup(player) && this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END + 1, true)) {
                slot.onTake(player, stack);
                quickMoveStack(player, index);
            } else 
                return ItemStack.EMPTY;
        }
        else {
            if ((index == MODIFICATION_SLOT || index == SPOOL_SLOT)) {
                if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END + 1, false))
                    return ItemStack.EMPTY;
            }
            else if (!this.moveItemStackTo(stack, SPOOL_SLOT, MODIFICATION_SLOT + 1, false))
                return ItemStack.EMPTY;

            slot.onTake(player, stack);
        }
        
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, AtelierBlocks.SEWING_TABLE.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        this.access.execute((level, pos) -> 
            clearContainer(player, this.modificationSlot)
        );
    }

    public SewingTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int getRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    private void updateIndex(int index) {
        if (index >= -1 && index < this.recipes.size()) {
            this.selectedRecipeIndex.set(index);
            updateResult(index);
        }
    }

    private void updateResult() {
        int index = this.selectedRecipeIndex.get();
        updateResult(index);
    }

    private void updateResult(int index) {
        if (index == -1 || !hasIngredients())
            this.resultSlot.set(ItemStack.EMPTY);
        else
            this.resultSlot.set(this.recipes.get(index).getResultItem(this.level.registryAccess()).copy());
    }

    @Override
    public boolean clickMenuButton(Player player, int index) {
        updateIndex(this.selectedRecipeIndex.get() == index ? -1 : index);
        return true;
    }

    public void registerUpdateListener(Runnable runnable) {
        this.slotUpdateListener = runnable;
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