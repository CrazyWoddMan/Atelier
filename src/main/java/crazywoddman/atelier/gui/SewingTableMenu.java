package crazywoddman.atelier.gui;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.blocks.AtelierBlockEntities;
import crazywoddman.atelier.blocks.AtelierBlocks;
import crazywoddman.atelier.blocks.SewingTable;
import crazywoddman.atelier.blocks.SewingTableBlockEntity;
import crazywoddman.atelier.data.AtelierData;
import crazywoddman.atelier.data.AtelierSounds;
import crazywoddman.atelier.data.SewingRecipe;
import crazywoddman.atelier.data.CountableIngredient;
import crazywoddman.atelier.data.ServerUtils;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

public final class SewingTableMenu extends AbstractContainerMenu {
    static final int TABLE_OFFSET_Y = -19;
    static final int HOTBAR_SIZE = 9;
    enum Mode {
        CRAFTING, COLORING;
    }
    final ContainerLevelAccess access;
    final Inventory inventory;
    final Slot result;
    final Slot spool;
    final Slot[] ingredients = new Slot[HOTBAR_SIZE];
    final CustomizationSlot customization;
    final SewingRecipe[] recipes;
    private final DataSlot selected = DataSlot.standalone();
    Mode mode = Mode.CRAFTING;

    public SewingTableMenu(int id, Inventory inventory, BlockPos pos) {
        super(AtelierMenuTypes.SEWING_TABLE.get(), id);
        this.inventory = inventory;
        ServerUtils.grantAdvancement(inventory.player, "root");
        Level level = inventory.player.level();
        BlockState state = level.getBlockState(pos);
        SewingTableBlockEntity blockEntity = level.getBlockEntity(switch (state.getValue(SewingTable.PART)) {
            case LEFT -> pos;
            case RIGHT -> pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
            case MACHINE -> pos.below();
        }, AtelierBlockEntities.SEWING_TABLE.get()).orElse(null);

        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
        this.recipes = level
            .getRecipeManager()
            .getAllRecipesFor(AtelierData.SEWING_RECIPE_TYPE.get())
            .stream()
            .sorted(Comparator.comparing(SewingRecipe::getId))
            .toArray(SewingRecipe[]::new);

        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(
                this.inventory,
                col,
                col * 18 + 8,
                142
            ));
        }

        // Inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(
                    this.inventory,
                    row * 9 + col + 9,
                    col * 18 + 8,
                    row * 18 + 84
                ));
        }

        this.spool = addSlot(new SpoolSlot(blockEntity));
        Container ingredientsContainer = new SimpleContainer(this.ingredients.length);

        for (int i = 0; i < this.ingredients.length; i++)
            this.ingredients[i] = addSlot(new IngredientSlot(ingredientsContainer, i));
        
        addSlot(this.customization = new CustomizationSlot());
        this.result = addSlot(new ResultSlot(pos));
        this.addDataSlot(this.selected);
        this.selected.set(-1);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        
        if (!slot.mayPickup(player) || !slot.hasItem())
            return ItemStack.EMPTY;
        
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        
        if (index < Inventory.INVENTORY_SIZE) {
            if (!this.moveItemStackTo(stack, Inventory.INVENTORY_SIZE, this.slots.size(), false)) {
                if (Inventory.isHotbarSlot(index)) {
                    if (!this.moveItemStackTo(stack, HOTBAR_SIZE, Inventory.INVENTORY_SIZE, false))
                        return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(stack, 0, HOTBAR_SIZE, false))
                    return ItemStack.EMPTY;
            }
        } else if (index == this.customization.index) {
            if (this.moveItemStackTo(stack, 0, Inventory.INVENTORY_SIZE, true)) {
                this.customization.container.clearContent();
            } else return ItemStack.EMPTY;
        } else if (!this.moveItemStackTo(stack, 0, Inventory.INVENTORY_SIZE, index == this.result.index))
            return ItemStack.EMPTY;

        
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean clickMenuButton(Player player, int index) {
        updateSelected(index);
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, AtelierBlocks.SEWING_TABLE.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, this.customization.container);
        clearContainer(player, this.ingredients[0].container);
    }

    int getSelectedIndex() {
        return this.selected.get();
    }

    Optional<SewingRecipe> getSelectedRecipe() {
        int index = getSelectedIndex();
        return index == -1 || this.recipes.length == 0 ? Optional.empty() : Optional.of(this.recipes[index]);
    }

    private void clearSlot(Slot slot) {
        this.inventory.placeItemBackInInventory(slot.getItem());
        slot.set(ItemStack.EMPTY);
    }

    private void updateSelected(int index) {
        if (index != getSelectedIndex())
            this.selected.set(index);

        for (int i = 0; i < this.ingredients.length; i++ ) {
            ItemStack item = this.ingredients[i].getItem();

            if (!item.isEmpty() && !this.ingredients[i].mayPlace(item))
                clearSlot(this.ingredients[i]);
        }

        if (this.mode == Mode.CRAFTING) {
            getSelectedRecipe().ifPresent(recipe ->
                recipe.getSpool()
                      .filter(spool -> this.spool.hasItem() && !spool.ingredient.test(this.spool.getItem()))
                      .ifPresent(s -> clearSlot(this.spool))
            );
        }

        updateResult();
    }

    private void updateResult() {
        switch (this.mode) {
            case CRAFTING -> tryCraft();
            case COLORING -> tryColor();
        }
    }

    private void tryColor() {
        DyeColor[] dyes = Arrays
            .stream(this.ingredients)
            .map(Slot::getItem)
            .filter(stack -> !stack.isEmpty())
            .map(DyeColor::getColor)
            .toArray(DyeColor[]::new);
        ItemStack stack = this.customization.createPreview();

        if (dyes.length > 0) {
            IDyeable.blendDyeColors(
                stack,
                getSelectedIndex(),
                dyes
            );
        }
    }

    private void tryCraft() {
        if (getSelectedRecipe()
            .filter(r -> r.getSpool().map(s -> this.spool.hasItem() && s.test(this.spool.getItem())).orElse(true))
            .map(recipe -> {
                if (recipe
                    .getSpool()
                    .map(required -> this.spool.hasItem() && required.test(this.spool.getItem()))
                    .orElse(true)
                ) {
                    boolean enoughIngredients = true;

                    for (int i = 0; i < recipe.ingredients.length; i++) {
                        ItemStack stack = this.ingredients[i].getItem();
                        
                        if (stack.isEmpty() || !recipe.ingredients[i].test(stack)) {
                            enoughIngredients = false;
                            break;
                        }
                    }

                    if (enoughIngredients) {
                        ItemStack result = new ItemStack(recipe.result);

                        if (recipe.result instanceof IDyeable dyable) {
                            for (int i = 0; i < dyable.getDefaultColors().length; i++) {
                                int index = i;
                                tryGetColor(this.ingredients[index].getItem()).ifPresent(color ->
                                    IDyeable.setColor(result, color, index)
                                );
                            }
                        }

                        this.result.set(result);
                        return false;
                    }
                }

                return true;
            }).orElse(true)
        ) {
            this.result.set(ItemStack.EMPTY);
        }
    }

    private Optional<Integer> tryGetColor(ItemStack stack) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();

            if (item instanceof IDyeable) {
                return IDyeable.getColorOptional(stack, 0);
            } else if (item instanceof DyeItem dye) {
                return Optional.of(IDyeable.convert(dye.getDyeColor().getTextureDiffuseColors()));
            } else {
                String name = ForgeRegistries.ITEMS.getKey(item).getPath();
                for (DyeColor color : DyeColor.values())
                    if (name.contains(color.getName()))
                        return Optional.of(IDyeable.convert(color.getTextureDiffuseColors()));
            }
        }

        return Optional.empty();
    }

    private class ResultUpdatingSlot extends UnmodifiableSlot {
        ResultUpdatingSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            updateResult();
        };

        @Override
        public void setByPlayer(ItemStack stack) {
            super.setByPlayer(stack);

            if (!stack.isEmpty())
                updateResult();
        };
    }

    private class SpoolSlot extends ResultUpdatingSlot {
        SpoolSlot(@Nullable SewingTableBlockEntity blockEntity) {
            super(blockEntity == null ? new SimpleContainer(1) : new SpoolContainer(blockEntity), 0, 7, TABLE_OFFSET_Y + 36);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (super.mayPlace(stack)) {
                if (SewingTableMenu.this.mode == Mode.CRAFTING) {
                    CountableIngredient spool = getSelectedRecipe().flatMap(SewingRecipe::getSpool).orElse(null);

                    if (spool != null)
                        return spool.ingredient.test(stack);
                }

                return AtelierData.SPOOL_ITEMS.contains(stack.getItem());
            }

            return false;
        }

        private static class SpoolContainer implements Container {
            private final ItemStackHandler handler;

            SpoolContainer(SewingTableBlockEntity blockEntity) {
                this.handler = blockEntity.spoolInventory;
            }

            @Override
            public void clearContent() {
                this.setItem(0, ItemStack.EMPTY);
            }

            @Override
            public int getContainerSize() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return this.getItem(0).isEmpty();
            }

            @Override
            public ItemStack getItem(int slot) {
                return this.handler.getStackInSlot(0);
            }

            @Override
            public ItemStack removeItem(int slot, int amount) {
                return this.handler.extractItem(slot, amount, false);
            }

            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                if (!getItem(0).isEmpty())
                    this.handler.setStackInSlot(0, ItemStack.EMPTY);
                
                return ItemStack.EMPTY;
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                this.handler.setStackInSlot(0, stack);
            }

            @Override
            public void setChanged() {}

            @Override
            public boolean stillValid(Player p_18946_) {
                return true;
            }
        }
    }

    private class IngredientSlot extends ResultUpdatingSlot {
        IngredientSlot(Container container, int index) {
            super(container, index, 8 + index * 18, TABLE_OFFSET_Y + 67);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (SewingTableMenu.this.mode == Mode.CRAFTING) {
                SewingRecipe recipe = getSelectedRecipe().orElse(null);

                if (recipe != null) {
                    int index = getSlotIndex();
                    return recipe.ingredients.length > index && recipe.ingredients[index].ingredient.test(stack);
                }
            }

            return stack.getItem() instanceof DyeItem;
        }
    }

    class CustomizationSlot extends UnmodifiableSlot {
        ItemStack preview;
        Runnable updateListener = () -> {};

        CustomizationSlot() {
            super(new SimpleContainer(1), 0, 59, TABLE_OFFSET_Y + 36);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof IDyeable;
        }

        @Override
        public boolean isActive() {
            return !SewingTableMenu.this.result.isActive();
        }

        @Override
        public ItemStack getItem() {
            return this.preview == null ? super.getItem() : this.preview;
        }

        @Override
        public ItemStack remove(int amount) {
            if (this.preview != null)
                applyColor();

            return super.remove(amount);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            consumeDye();
            super.onTake(player, stack);
            onItemReplace(Mode.CRAFTING, true);
        };

        @Override
        public void set(ItemStack stack) {
            if (stack.isEmpty())
                super.set(stack);
        }

        ItemStack createPreview() {
            return this.preview = this.container.getItem(0).copy();
        }

        @Override
        public void setByPlayer(ItemStack stack) {
            if (stack.isEmpty()) {
                super.set(stack);
                return;
            }
                
            boolean sameItem;

            if (this.preview != null) {
                sameItem = ItemStack.isSameItem(stack, this.preview);
                applyColor();
                consumeDye();
            }
            else sameItem = false;
            
            super.set(stack);
            onItemReplace(Mode.COLORING, !sameItem);
        }

        private void applyColor() {
            this.container.getItem(0).setTag(this.preview.getTag());
            this.preview = null;
        }

        private void consumeDye() {
            for (int i = 0; i < SewingTableMenu.this.ingredients.length; i++) {
                ItemStack ingredient = SewingTableMenu.this.ingredients[i].getItem();

                if (!ingredient.isEmpty())
                    ingredient.shrink(1);
            }
        }

        private void onItemReplace(Mode mode, boolean updateList) {
            SewingTableMenu.this.mode = mode;
            updateSelected(mode == Mode.CRAFTING ? -1 : 0);

            if (updateList)
                this.updateListener.run();
        }
    }

    private class ResultSlot extends UnmodifiableSlot {
        private final BlockPos pos;
        private int tickStamp;

        ResultSlot(BlockPos pos) {
            super(new SimpleContainer(1), 0, 59, TABLE_OFFSET_Y + 36);
            this.pos = pos;
        }

        @Override
        public boolean isActive() {
            return hasItem();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack item) {
            SewingRecipe recipe = getSelectedRecipe().get();
            recipe.getSpool().ifPresent(spool ->
                SewingTableMenu.this.spool.remove(spool.count)
            );

            for (int i = 0; i < recipe.ingredients.length; i++)
                SewingTableMenu.this.ingredients[i].getItem().shrink(recipe.ingredients[i].count);
            
            if (Math.abs(this.tickStamp - player.tickCount) > 1) {
                this.tickStamp = player.tickCount;
                player.level().playSound(player, this.pos, AtelierSounds.SEWING_MACHINE.get(), SoundSource.BLOCKS, 1, 0.9f + player.getRandom().nextFloat() * 0.1f);
            }

            updateResult();
            super.onTake(player, item);
        }
    }

    static class UnmodifiableSlot extends Slot {

        UnmodifiableSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean allowModification(Player player) {
            return false;
        }
    }
}