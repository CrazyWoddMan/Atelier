package crazywoddman.atelier.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.client.ClientUtils;
import crazywoddman.atelier.compat.jei.AtelierJEI;
import crazywoddman.atelier.data.CountableIngredient;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public final class SewingTableScreen extends AbstractContainerScreen<SewingTableMenu> {
    private static final ResourceLocation INVENTORY = ClientUtils.makeTexturePath("minecraft", "gui/container/crafting_table");
    public static final ResourceLocation BACKGROUND = ClientUtils.makeTexturePath(Atelier.MODID, "gui/sewing_table");
    private static final int
    LIST_X = 83,
    LIST_Y = 6,
    LIST_HEIGHT = 55,
    SCROLLBAR_X = LIST_X + 68,
    SCROLLBAR_Y = LIST_Y + 1,
    SCROLLBAR_WIDTH = 12,
    SCROLLBAR_HEIGHT = LIST_HEIGHT - 2,
    SLIDER_HEIGHT = 15,
    SLIDER_RANGE = SCROLLBAR_HEIGHT - SLIDER_HEIGHT,
    BUTTON_WIDTH = 16,
    BUTTON_HEIGHT = 18,
    BUTTONS_START_X = LIST_X + 1,
    BUTTONS_PER_ROW = 4,
    BUTTONS_VISIBLE = 12;
    private double scrollOffset;
    private boolean scrolling;
    private int listSize, startIndex, offScreenRows, topPos;
    // TODO: render as sprites
    private ResourceLocation[] textures;

    public SewingTableScreen(SewingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        updateList();
        this.menu.customization.updateListener = this::updateList;
    }

    @Override
    protected void init() {
        super.init();
        this.topPos = super.topPos + SewingTableMenu.TABLE_OFFSET_Y;
    }

    @SuppressWarnings("deprecation")
    private void updateList() {
        this.textures = null;
        switch (this.menu.mode) {
            case CRAFTING -> this.listSize = this.menu.recipes.length;
            case COLORING -> {
                ItemStack stack = this.menu.customization.getItem();
                this.listSize = IDyeable.getDefaultColors(stack).length;
                this.textures = this.minecraft
                    .getItemRenderer()
                    .getModel(stack, this.minecraft.level, this.minecraft.player, 0)
                    .getQuads(null, null, this.minecraft.level.random)
                    .stream()
                    .map(quad -> quad.getSprite().contents().name().withPath(path -> "textures/" + path + ".png"))
                    .distinct()
                    .toArray(ResourceLocation[]::new);
            }
        };
        this.scrollOffset = 0;
        this.offScreenRows = (this.listSize + BUTTONS_PER_ROW - 1) / BUTTONS_PER_ROW - 3;
        updateStartIndex();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        renderBackground(graphics);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(this.leftPos, super.topPos, 0);
        graphics.blit(INVENTORY, 0, 0, 0, 0, this.imageWidth, this.imageHeight);
        graphics.blit(BACKGROUND, 0, SewingTableMenu.TABLE_OFFSET_Y, 0, 0, this.imageWidth, 89);
        
        if (this.listSize != 0) {
            pose.pushPose();
            pose.translate(0, SewingTableMenu.TABLE_OFFSET_Y, 0);
            renderListWindow(graphics);
            renderSlider(graphics);
            renderButtons(graphics, mouseX, mouseY);
            pose.popPose();
        }

        renderIngredients(graphics);
        pose.popPose();
    }

    private void renderListWindow(GuiGraphics graphics) {
        graphics.blit(
            BACKGROUND,
            LIST_X,
            LIST_Y,
            0,
            89,
            81,
            LIST_HEIGHT
        );
    }
    
    private void renderSlider(GuiGraphics graphics) {
        graphics.blit(
            BACKGROUND, 
            SCROLLBAR_X,
            SCROLLBAR_Y + (int)(SLIDER_RANGE * this.scrollOffset),
            this.isScrollBarActive() ? 176 : 188,
            0,
            SCROLLBAR_WIDTH,
            SLIDER_HEIGHT
        );
    }

    private void renderButtons(GuiGraphics graphics, int mouseX, int mouseY) {
        int selected = this.menu.getSelectedIndex();

        for (int i = 0, recipe; i < BUTTONS_VISIBLE && (recipe = i + this.startIndex) < this.listSize; i++) {
            int x = BUTTONS_START_X + i % BUTTONS_PER_ROW * BUTTON_WIDTH;
            int y = LIST_Y + i / BUTTONS_PER_ROW * BUTTON_HEIGHT + 2;
            
            // Buttons
            int buttonTextureY = SLIDER_HEIGHT;
            if (recipe == selected)
                buttonTextureY += BUTTON_HEIGHT; // Chosen button
            else if (hovering(BUTTONS_START_X + i % BUTTONS_PER_ROW * BUTTON_WIDTH + 1, LIST_Y + i / BUTTONS_PER_ROW * BUTTON_HEIGHT + 2, BUTTON_WIDTH - 2, BUTTON_HEIGHT - 2, mouseX, mouseY))
                buttonTextureY += BUTTON_HEIGHT * 2; // Button under the cursor
            graphics.blit(BACKGROUND, x, y - 1, this.imageWidth, buttonTextureY, BUTTON_WIDTH, BUTTON_HEIGHT);

             // Icons
            switch (this.menu.mode) {
                case CRAFTING -> graphics.renderItem(new ItemStack(this.menu.recipes[recipe].result), x, y);
                case COLORING -> {
                    ItemStack stack = this.menu.customization.getItem();

                    for (int layer = 0; layer < this.listSize; layer++) {
                        float[] color = IDyeable.getFloatColor(stack, layer);
                        graphics.setColor(color[0], color[1], color[2], layer == recipe ? 1 : 0.2f);
                        graphics.blit(
                            this.textures[layer],
                            x, y,
                            0, 0,
                            16, 16,
                            16, 16
                        );
                        graphics.setColor(1, 1, 1, 1);
                    }

                    if (this.listSize < this.textures.length) {
                        graphics.setColor(1, 1, 1, 0.2f);
                        graphics.blit(
                            this.textures[this.listSize],
                            x, y,
                            0, 0,
                            16, 16,
                            16, 16
                        );
                        graphics.setColor(1, 1, 1, 1);
                    }
                }
            }
        }
    }

    private void renderIngredients(GuiGraphics graphics) {
        if (this.menu.mode == SewingTableMenu.Mode.CRAFTING) {
            if (this.listSize == 0)
                return;

            this.menu.getSelectedRecipe().ifPresent(recipe -> {
                if (!this.menu.spool.hasItem()) {
                    recipe.getSpool().ifPresent(spool -> {
                        ItemStack display = cycleEverySecond(spool.getItems()).copy();
                        renderGhostItem(graphics, display, this.menu.spool.x, this.menu.spool.y);

                        if (display.getCount() > 1) {
                            graphics.setColor(1, 1, 1, 0.3f);
                            graphics.renderItemDecorations(this.font, display, this.menu.spool.x, this.menu.spool.y);
                            graphics.setColor(1, 1, 1, 1);
                        }
                    });
                }
                
                for (int i = 0; i < recipe.ingredients.length; i++) {
                    Slot slot = this.menu.ingredients[i];

                    if (slot.hasItem())
                        continue;

                    CountableIngredient ingredient = recipe.ingredients[i];
                    ItemStack display = cycleEverySecond(ingredient.getItems()).copy();
                    renderGhostItem(graphics, display, slot.x, slot.y);
                    
                    if (ingredient.count < 2)
                        continue;
                    
                    graphics.setColor(1, 1, 1, 0.3F);
                    graphics.renderItemDecorations(this.font, display, slot.x, slot.y);
                    graphics.setColor(1, 1, 1, 1);
                }
            });
        } else {
            DyeColor[] colors = DyeColor.values();
            for (int i = 0; i < this.menu.ingredients.length; i++) {
                Slot slot = this.menu.ingredients[i];
                if (!slot.hasItem())
                    renderGhostItem(graphics, new ItemStack(DyeItem.byColor(colors[i])), slot.x, slot.y);
            }
        }
    }

    @SafeVarargs
    private static <T> T cycleEverySecond(T... objects) {
        return objects[objects.length > 1 ? (int)(System.currentTimeMillis() / 1000 % objects.length) : 0];
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
        graphics.flush(); // might cause issues
        pose.popPose();
    }

    private void renderIngredientTooltip(Ingredient ingredient, GuiGraphics graphics, int x, int y) {
        ItemStack[] stacks = ingredient.getItems();
        ItemStack display = cycleEverySecond(stacks);

        if (!Atelier.JEI_LOADED || !AtelierJEI.renderJeiTooltip(display, stacks, graphics, x, y))
            graphics.renderTooltip(this.font, display, x, y);
	}

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        
        if (this.menu.mode != SewingTableMenu.Mode.CRAFTING || this.listSize == 0)
            return;

        this.menu.getSelectedRecipe().ifPresent(recipe -> {
            recipe.getSpool()
            .filter(s -> !this.menu.spool.hasItem() && hovering(this.menu.spool, mouseX, mouseY))
            .ifPresent(spool -> renderIngredientTooltip(spool.ingredient, graphics, mouseX, mouseY));

            for (int i = 0; i < recipe.ingredients.length; i++) {
                Slot ingredient = this.menu.ingredients[i];

                if (!ingredient.hasItem() && hovering(ingredient, mouseX, mouseY))
                    renderIngredientTooltip(recipe.ingredients[i].ingredient, graphics, mouseX, mouseY);
            }
        });

        for (int i = 0, r; i < BUTTONS_VISIBLE && (r = i + this.startIndex) < this.listSize; i++) {
            if (hovering(
                BUTTONS_START_X + i % BUTTONS_PER_ROW * BUTTON_WIDTH + 1,
                LIST_Y + i / BUTTONS_PER_ROW * BUTTON_HEIGHT + 2,
                BUTTON_WIDTH - 2,
                BUTTON_HEIGHT - 2,
                mouseX, mouseY
            )) graphics.renderTooltip(
                this.font,
                new ItemStack(this.menu.recipes[r].result),
                mouseX, mouseY
            );
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        
        for (int i = 0, recipe; i < BUTTONS_VISIBLE && (recipe = i + this.startIndex) < this.listSize; i++) {
            if (hovering(
                BUTTONS_START_X + i % BUTTONS_PER_ROW * BUTTON_WIDTH + 1,
                LIST_Y + i / BUTTONS_PER_ROW * BUTTON_HEIGHT + 2,
                BUTTON_WIDTH - 2,
                BUTTON_HEIGHT - 2,
                mouseX, mouseY
            )) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1));
                this.menu.clickMenuButton(this.minecraft.player, recipe);
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, recipe);
                return true;
            }
        }
        
        if (hovering(SCROLLBAR_X, SCROLLBAR_Y, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT, mouseX, mouseY)) {
            this.scrolling = true;
            if (this.isScrollBarActive())
                setSliderToMouse(mouseY);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean setSliderToMouse(double mouseY) {
        this.scrollOffset = Mth.clamp((mouseY - this.topPos - SCROLLBAR_Y - SLIDER_HEIGHT / 2.0) / SLIDER_RANGE, 0, 1);
        updateStartIndex();
        return true;
    }

    private boolean hovering(Slot slot, int mouseX, int mouseY) {
        return isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
    }

    private boolean hovering(int fromX, int fromY, int width, int height, double mouseX, double mouseY) {
        return isHovering(fromX, fromY + SewingTableMenu.TABLE_OFFSET_Y, width, height, mouseX, mouseY);
    }

    private void updateStartIndex() {
        this.startIndex = (int)((this.scrollOffset * this.offScreenRows) + 0.5) * BUTTONS_PER_ROW;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return (this.scrolling && this.isScrollBarActive() && setSliderToMouse(mouseY))
                || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        if (this.isScrollBarActive()) {
            this.scrollOffset = Mth.clamp(this.scrollOffset - scrollDelta / this.offScreenRows, 0, 1);
            updateStartIndex();
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollDelta);
    }

    private boolean isScrollBarActive() {
        return this.listSize > BUTTONS_VISIBLE;
    }
}