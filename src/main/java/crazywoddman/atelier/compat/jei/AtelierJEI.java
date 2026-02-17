package crazywoddman.atelier.compat.jei;

import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;

import java.util.ArrayList;
import java.util.List;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierTags;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.recipes.AtelierRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.SafeIngredientUtil;
import mezz.jei.library.gui.ingredients.TagContentTooltipComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;

@JeiPlugin
public class AtelierJEI implements IModPlugin {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "jei");
    public static IJeiRuntime jeiRuntime;

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            new SewingTableCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(SewingTableCategory.TYPE, AtelierItems.SEWING_TABLE.get());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(
            SewingTableCategory.TYPE,
            manager.getAllRecipesFor(AtelierRecipes.SEWING_RECIPE_TYPE.get())
        );
        IVanillaRecipeFactory vanillaRecipeFactory = registration.getVanillaRecipeFactory();
        List<IJeiBrewingRecipe> recipes = new ArrayList<>();

        for (Item item : AtelierTags.Items.get(AtelierTags.Items.GAS_FILTERS)) {
            ItemStack filter = new ItemStack(item);
            ItemStack prepared = filter.copy();
            prepared.getOrCreateTag().putBoolean("isPrepared", true);
            recipes.add(vanillaRecipeFactory.createBrewingRecipe(
                List.of(new ItemStack(Items.MILK_BUCKET)),
                filter,
                prepared,
                ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "brewing/filter/prepared")
            ));
            ItemStack wither = filter.copy();
            ListTag tag = new ListTag();
            tag.add(StringTag.valueOf("minecraft:wither"));
            wither.getOrCreateTag().put("effects", tag);
            recipes.add(vanillaRecipeFactory.createBrewingRecipe(
                List.of(new ItemStack(Items.WITHER_ROSE)),
                prepared,
                wither,
                ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "brewing/filter/wither")
            ));

            for (Potion potion : AtelierTags.Potions.get(AtelierTags.Potions.GAS_FILTER)) {
                ItemStack result = filter.copy();
                ListTag effects = new ListTag();

                for (MobEffectInstance effect : potion.getEffects())
                    effects.add(StringTag.valueOf(ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect()).toString()));

                result.getOrCreateTag().put("effects", effects);
                recipes.add(vanillaRecipeFactory.createBrewingRecipe(
                    List.of(PotionUtils.setPotion(new ItemStack(Items.POTION), potion)),
                    prepared,
                    result,
                    ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "brewing/filter/" + ForgeRegistries.POTIONS.getKey(potion).getPath())
                ));
            }
        }
        registration.addRecipes(
            RecipeTypes.BREWING,
            recipes
        );
    }

    public static boolean renderJeiTooltip(ItemStack display, ItemStack[] items, GuiGraphics graphics, int x, int y) {
        IJeiRuntime jeiRuntime = AtelierJEI.jeiRuntime;

        if (jeiRuntime != null) { 
            IClientConfig jeiConfig = Internal.getJeiClientConfigs().getClientConfig();

            if (jeiConfig.isTagContentTooltipEnabled()) {
                if (items.length > 1) {
                    if (display.getCount() > 1);
                        for (ItemStack stack : items)
                            stack.setCount(1);
                        
                    JeiTooltip tooltip = new JeiTooltip();
                    IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
                    IIngredientType<ItemStack> type = ingredientManager.getIngredientTypeChecked(ItemStack.class).get();
                    IIngredientRenderer<ItemStack> renderer = ingredientManager.getIngredientRenderer(type);
                    SafeIngredientUtil.getTooltip(tooltip, ingredientManager, renderer, ingredientManager.createTypedIngredient(type, display).get());
                    List<ItemStack> ingredients = List.of(items);

                    if (!jeiConfig.isHideSingleIngredientTagsEnabled() || ingredients.size() != 1) {
                        IIngredientHelper<ItemStack> ingredientHelper = ingredientManager.getIngredientHelper(type);
                        ingredientHelper.getTagKeyEquivalent(ingredients).ifPresent(tag -> {
                            tooltip.add(Component
                                .translatable("jei.tooltip.recipe.tag", "")
                                .withStyle(ChatFormatting.GRAY)
                            );
                            tooltip.add(Services.PLATFORM
                                .getRenderHelper()
                                .getName(tag)
                                .copy()
                                .withStyle(ChatFormatting.GRAY)
                            );
                        });
                    }

                    tooltip.add(new TagContentTooltipComponent<ItemStack>(renderer, ingredients));
                    tooltip.draw(graphics, x, y);
                    
                    return true;
                }
            }
        }

        return false;
	}
}
