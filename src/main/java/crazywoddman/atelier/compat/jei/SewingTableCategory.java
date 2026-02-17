package crazywoddman.atelier.compat.jei;

import java.util.List;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.gui.SewingTableScreen;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.recipes.SewingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class SewingTableCategory implements IRecipeCategory<SewingRecipe> {
    public static final RecipeType<SewingRecipe> TYPE = RecipeType.create(Atelier.MODID, AtelierItems.SEWING_TABLE.getId().getPath(), SewingRecipe.class);
    private final ItemStack categoryItem = new ItemStack(AtelierItems.SEWING_TABLE.get());
    private final Component title = Component.translatable("block." + Atelier.MODID + "." + AtelierItems.SEWING_TABLE.getId().getPath());
    private final IDrawable icon;

    public SewingTableCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(this.categoryItem);
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return 168;
    }

    @Override
    public int getHeight() {
        return 81;
    }

    @Override
    public RecipeType<SewingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void draw(SewingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        graphics.blit(SewingTableScreen.BACKGROUND, -4, -4, 0, 0, 176, 89);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SewingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(3, 32).addItemStacks(List.of(recipe.spool.getItems()));

        for (int i = 0; i < 9; i++) {
            IRecipeSlotBuilder slot = builder.addInputSlot(4 + i * 18, 63);

            if (i < recipe.ingredients.size())
                slot.addItemStacks(List.of(recipe.ingredients.get(i).getItems()));
        }

        builder.addOutputSlot(55, 32).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
    }
}
