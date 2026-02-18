package crazywoddman.atelier.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.compat.jei.AtelierJEI;
import crazywoddman.atelier.recipes.SewingRecipe;
import crazywoddman.atelier.recipes.SewingRecipe.CountableIngredient;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class SewingTableScreen extends AbstractContainerScreen<SewingTableMenu> {
    private static final ResourceLocation INVENTORY = ResourceLocation.fromNamespaceAndPath(
        "minecraft",
        "textures/gui/container/crafting_table.png"
    );
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
        Atelier.MODID,
        "textures/gui/sewing_table.png"
    );
    private static final int INGREDIENTS_START_X = 8;
    private static final int INGREDIENTS_Y = 57;
    private static final int RECIPE_LIST_X = 84;
    private static final int RECIPE_LIST_Y = -4;
    private static final int SCROLLBAR_X = RECIPE_LIST_X + 67;
    private static final int SCROLLBAR_Y = RECIPE_LIST_Y + 1;
    private static final int SCROLLBAR_CLICK_WIDTH = 12;
    private static final int SCROLLBAR_CLICK_HEIGHT = 56;
    private static final int BUTTON_TEXTURE_Y = 15;
    private static final int RECIPES_PER_ROW = 4;
    private static final int RECIPE_BUTTON_WIDTH = 16;
    private static final int RECIPE_BUTTON_HEIGHT = 18;
    private static final int VISIBLE_RECIPES = 12;
    private final int offscreenRows = (this.menu.recipes.size() + RECIPES_PER_ROW - 1) / RECIPES_PER_ROW - 3;
    
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public SewingTableScreen(SewingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        menu.registerUpdateListener(() -> {
            boolean hasItemInModification = this.menu.getSlot(SewingTableMenu.MODIFICATION_SLOT).hasItem();
        
            if (this.displayRecipes && hasItemInModification)
                this.displayRecipes = false;
            else if (!this.displayRecipes && !hasItemInModification)
                this.displayRecipes = true;
        });
        this.displayRecipes = true;
        this.imageHeight = 166;
        this.inventoryLabelY = -this.imageHeight;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        renderBackground(graphics);
        
        // Background
        graphics.blit(INVENTORY, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        graphics.blit(BACKGROUND, this.leftPos, this.topPos - 10, 0, 0, this.imageWidth, 89);
        
        if (this.displayRecipes) {
            graphics.blit(BACKGROUND, this.leftPos + 83, this.topPos - 5, 0, 89, 81, 56);
            // Scroll bar
            graphics.blit(BACKGROUND, 
                this.leftPos + SCROLLBAR_X,
                this.topPos + SCROLLBAR_Y + (int)(41.0F * this.scrollOffs),
                this.isScrollBarActive() ? 176 : 188,
                0,
                12,
                15
            );
            // Recipe buttons
            int recipeListX = this.leftPos + RECIPE_LIST_X;
            int recipeListY = this.topPos + RECIPE_LIST_Y;
            int endIndex = this.startIndex + VISIBLE_RECIPES;
            for (int recipeIndex = this.startIndex; recipeIndex < endIndex && recipeIndex < this.menu.recipes.size(); recipeIndex++) {
                int slotIndex = recipeIndex - this.startIndex;
                int slotX = recipeListX + (slotIndex % RECIPES_PER_ROW) * RECIPE_BUTTON_WIDTH;
                int slotY = recipeListY + slotIndex / RECIPES_PER_ROW * RECIPE_BUTTON_HEIGHT + 2;
                
                int y = BUTTON_TEXTURE_Y;
                
                if (recipeIndex == this.menu.getRecipeIndex())
                    y += RECIPE_BUTTON_HEIGHT; // Chosen button
                else if (isHovering(RECIPE_LIST_X + slotIndex % RECIPES_PER_ROW * RECIPE_BUTTON_WIDTH + 1, RECIPE_LIST_Y + slotIndex / RECIPES_PER_ROW * RECIPE_BUTTON_HEIGHT + 2, RECIPE_BUTTON_WIDTH - 2, RECIPE_BUTTON_HEIGHT - 2, mouseX, mouseY))
                    y += RECIPE_BUTTON_HEIGHT * 2; // Current button under the cursor

                graphics.blit(BACKGROUND, slotX, slotY - 1, this.imageWidth, y, RECIPE_BUTTON_WIDTH, RECIPE_BUTTON_HEIGHT);
            }
            // Other buttons
            for (int recipeIndex = this.startIndex; recipeIndex < endIndex && recipeIndex < this.menu.recipes.size(); recipeIndex++) {
                int slotIndex = recipeIndex - this.startIndex;
                graphics.renderItem(
                    this.menu.getRecipe(recipeIndex).getResultItem(this.minecraft.level.registryAccess()), 
                    recipeListX + (slotIndex % RECIPES_PER_ROW) * RECIPE_BUTTON_WIDTH,
                    recipeListY + (slotIndex / RECIPES_PER_ROW) * RECIPE_BUTTON_HEIGHT + 2
                );
            }
        } else {
            RenderSystem.setShaderColor(0, 0, 0, 0.2f);

            for (int i = 0; i < this.menu.modificationModules.size(); i++) {
                if (this.menu.slots.get(SewingTableMenu.INGREDIENTS_START + i).hasItem())
                    continue;

                ResourceLocation path = SlotReference.of(this.minecraft.player, this.menu.modificationModules.get(i), 0).type().icon();
                graphics.blit(
                    ResourceLocation.fromNamespaceAndPath(path.getNamespace(), "textures/" + path.getPath() + ".png"),
                    // BACKGROUND,
                    this.leftPos + INGREDIENTS_START_X + i * 18, this.topPos + INGREDIENTS_Y,
                    0, 0,
                    16, 16,
                    16, 16
                );
            }
            
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
        
        graphics.pose().pushPose();
        graphics.pose().translate(this.leftPos, this.topPos, 0);
        
        int selectedIndex = this.menu.getRecipeIndex();
        
        if (selectedIndex != -1) {
            SewingRecipe recipe = this.menu.getRecipe(selectedIndex);
            
            if (!recipe.spool.isEmpty()) {
                Slot spoolSlot = this.menu.getSlot(SewingTableMenu.SPOOL_SLOT);
                
                if (!spoolSlot.hasItem()) {
                    ItemStack[] spoolAcceptable = recipe.spool.getItems();
                    ItemStack spoolStack = spoolAcceptable[cycleEverySecond(spoolAcceptable.length)].copy();
                    renderGhostItem(graphics, spoolStack, spoolSlot.x, spoolSlot.y);


                    if (spoolStack.getCount() > 1) {
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.3F);
                        graphics.renderItemDecorations(this.font, spoolStack, spoolSlot.x, spoolSlot.y);
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
            }
            
            for (int i = 0; i < Math.min(recipe.ingredients.size(), 9); i++) {
                Slot ingredientSlot = this.menu.getSlot(SewingTableMenu.INGREDIENTS_START + i);
            
                if (ingredientSlot.hasItem())
                    continue;

                CountableIngredient ingredient = recipe.ingredients.get(i);
                ItemStack[] suitableItems = ingredient.getItems();
                ItemStack displayIngredient = suitableItems[cycleEverySecond(suitableItems.length)].copy();
                int count = ingredient.getCount();
                displayIngredient.setCount(count);
                int x = INGREDIENTS_START_X + i * 18;
                int y = INGREDIENTS_Y;
                renderGhostItem(graphics, displayIngredient, x, y);
                
                if (count < 2)
                    continue;

                RenderSystem.setShaderColor(1, 1, 1, 0.3F);
                graphics.renderItemDecorations(this.font, displayIngredient, x, y);
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
        }

        graphics.pose().popPose();
    }

    private static int cycleEverySecond(int length) {
        if (length > 1)
            return (int)(System.currentTimeMillis() / 1000 % length);
        
        return 0;
    }

    private static class GhostItem implements MultiBufferSource {
        private final GuiGraphics graphics;
        
        private GhostItem(GuiGraphics graphics) {
            this.graphics = graphics;
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            VertexConsumer original = graphics.bufferSource().getBuffer(type);
            
            return new VertexConsumer() {
                @Override
                public VertexConsumer vertex(double x, double y, double z) {
                    return original.vertex(x, y, z);
                }
                
                @Override
                public VertexConsumer color(int red, int green, int blue, int alpha) {
                    return original.color(red, green, blue, alpha / 2);
                }
                
                @Override
                public VertexConsumer uv(float u, float v) {
                    return original.uv(u, v);
                }
                
                @Override
                public VertexConsumer overlayCoords(int u, int v) {
                    return original.overlayCoords(u, v);
                }
                
                @Override
                public VertexConsumer uv2(int u, int v) {
                    return original.uv2(u, v);
                }
                
                @Override
                public VertexConsumer normal(float x, float y, float z) {
                    return original.normal(x, y, z);
                }
                
                @Override
                public void endVertex() {
                    original.endVertex();
                }
                
                @Override
                public void defaultColor(int red, int green, int blue, int alpha) {
                    original.defaultColor(red, green, blue, alpha / 2);
                }
                
                @Override
                public void unsetDefaultColor() {
                    original.unsetDefaultColor();
                }
            };
        }
    }

    private void renderGhostItem(GuiGraphics graphics, ItemStack item, int x, int y) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + 8, y + 8, 16);
        pose.scale(16, 16, 1);
        pose.mulPose(Axis.XP.rotationDegrees(180));

        ItemRenderer render = this.minecraft.getItemRenderer();
        render.render(
            new ItemStack(Items.STICK), // workaround for BlockItem transparent render
            ItemDisplayContext.GUI,
            false,
            graphics.pose(),
            new GhostItem(graphics),
            15728880,
            OverlayTexture.NO_OVERLAY,
            render.getModel(item, this.minecraft.level, this.minecraft.player, 0)
        );
        graphics.flush();
        pose.popPose();
    }

    private void renderTooltip(Ingredient ingredient, GuiGraphics graphics, int x, int y) {
        ItemStack[] stacks = ingredient.getItems();
        ItemStack display = stacks[cycleEverySecond(stacks.length)];

        if (!Atelier.JEI_LOADED || !AtelierJEI.renderJeiTooltip(display, stacks, graphics, x, y))
            graphics.renderTooltip(this.font, display, x, y);
	}

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        
        if (!this.displayRecipes)
            return;

        int index = this.menu.getRecipeIndex();

        if (index != -1) {
            SewingRecipe recipe = this.menu.getRecipe(index);

            if (!recipe.spool.isEmpty() && !this.menu.slots.get(SewingTableMenu.SPOOL_SLOT).hasItem() && isHovering(SewingTableMenu.SPOOL_SLOT_X, SewingTableMenu.SPOOL_SLOT_Y, 16, 16, mouseX, mouseY))
                renderTooltip(recipe.spool.asIngredient(), graphics, mouseX, mouseY);

            List<Ingredient> ingredients = recipe.getIngredients();

            for (int i = 0; i < ingredients.size(); i++)
                if (!this.menu.slots.get(SewingTableMenu.INGREDIENTS_START + i).hasItem() && isHovering(INGREDIENTS_START_X + i * 18, INGREDIENTS_Y, 16, 16, mouseX, mouseY))
                    renderTooltip(ingredients.get(i), graphics, mouseX, mouseY);
        }

        for (int recipeIndex = this.startIndex; recipeIndex < this.startIndex + VISIBLE_RECIPES && recipeIndex < this.menu.recipes.size(); recipeIndex++) {
            int slotIndex = recipeIndex - this.startIndex;
            
            if (isHovering(RECIPE_LIST_X + slotIndex % RECIPES_PER_ROW * RECIPE_BUTTON_WIDTH + 1, RECIPE_LIST_Y + slotIndex / RECIPES_PER_ROW * RECIPE_BUTTON_HEIGHT + 2, RECIPE_BUTTON_WIDTH - 2, RECIPE_BUTTON_HEIGHT - 2, mouseX, mouseY))
                graphics.renderTooltip(
                    this.font,
                    this.menu.getRecipe(recipeIndex).getResultItem(this.minecraft.level.registryAccess()),
                    mouseX,
                    mouseY
                );
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        
        if (this.displayRecipes) {
            for (int recipeIndex = this.startIndex; recipeIndex < this.startIndex + VISIBLE_RECIPES; recipeIndex++) {
                int slotIndex = recipeIndex - this.startIndex;
                
                if (isHovering(RECIPE_LIST_X + slotIndex % RECIPES_PER_ROW * RECIPE_BUTTON_WIDTH + 1, RECIPE_LIST_Y + slotIndex / RECIPES_PER_ROW * RECIPE_BUTTON_HEIGHT + 2, RECIPE_BUTTON_WIDTH - 2, RECIPE_BUTTON_HEIGHT - 2, mouseX, mouseY)) {
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1));
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, recipeIndex);

                    return true;
                }
            }
            
            if (isHovering(SCROLLBAR_X, SCROLLBAR_Y, SCROLLBAR_CLICK_WIDTH, SCROLLBAR_CLICK_HEIGHT, mouseX, mouseY))
                this.scrolling = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void updateStartIndex() {
        this.startIndex = (int)((this.scrollOffs * this.offscreenRows) + 0.5) * RECIPES_PER_ROW;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling && this.isScrollBarActive()) {
            int scrollAreaTop = this.topPos + SCROLLBAR_Y;
            int scrollAreaBottom = scrollAreaTop + 54;
            
            this.scrollOffs = ((float)mouseY - scrollAreaTop - 7.5F) / ((scrollAreaBottom - scrollAreaTop) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            updateStartIndex();
            
            return true;
        }
        
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        if (this.isScrollBarActive()) {
            this.scrollOffs = Mth.clamp(this.scrollOffs - (float)(scrollDelta / this.offscreenRows), 0, 1);
            updateStartIndex();
        }

        return true;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && this.menu.recipes.size() > VISIBLE_RECIPES;
    }
}