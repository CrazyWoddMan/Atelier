package crazywoddman.atelier.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import crazywoddman.atelier.Atelier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AtelierRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = 
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, Atelier.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = 
        DeferredRegister.create(Registries.RECIPE_TYPE, Atelier.MODID);

    public static final RegistryObject<RecipeSerializer<SewingRecipe>> SEWING_RECIPE_SERIALIZER = 
        RECIPE_SERIALIZERS.register("sewing_table", () -> SewingRecipe.SewingRecipeSerializer.INSTANCE);
    public static final RegistryObject<RecipeType<SewingRecipe>> SEWING_RECIPE_TYPE = 
        RECIPE_TYPES.register("sewing_table", () -> SewingRecipe.SewingRecipeType.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PlateRecipe>> PLATES_SERIALIZER = 
        RECIPE_SERIALIZERS.register("armor_plate", () -> PlateRecipe.PlatesSerializer.INSTANCE);
    public static final RegistryObject<RecipeType<PlateRecipe>> PLATE_RECIPE_TYPE = 
        RECIPE_TYPES.register("armor_plate", () -> PlateRecipe.PlateRecipeType.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PatchRecipe>> PATCH_POS_SERIALIZER = 
        RECIPE_SERIALIZERS.register("patch_pos", () -> PatchRecipe.PatchesSerializer.INSTANCE);
    public static final RegistryObject<RecipeType<PatchRecipe>> PATCH_RECIPE_TYPE = 
        RECIPE_TYPES.register("patch_pos", () -> PatchRecipe.PatchRecipeType.INSTANCE);

    private static final Map<Item, PlateRecipe> PLATE_RECIPES = new HashMap<>();
    private static final Map<Item, String> PATCH_ITEMS = new HashMap<>();

    public static void reload(RecipeManager recipeManager) {
        PLATE_RECIPES.clear();
        PATCH_ITEMS.clear();
        
        recipeManager.getAllRecipesFor(PLATE_RECIPE_TYPE.get()).forEach(recipe -> {
            for (ItemStack stack : recipe.getPlate().getItems())
                PLATE_RECIPES.put(stack.getItem(), recipe);
        });
        recipeManager.getAllRecipesFor(PATCH_RECIPE_TYPE.get()).forEach(recipe -> {
            String slot = recipe.getSlot();

            for (ItemStack stack : recipe.getWearable().getItems())
                PATCH_ITEMS.put(stack.getItem(), slot);
        });
    }

    /**
    * Should only be called after world initialization
    */
    public static Optional<String> getPatchSlot(Item item) {
        return PATCH_ITEMS.containsKey(item) ? Optional.of(PATCH_ITEMS.get(item)) : Optional.empty();
    }

    /**
    * Should only be called after world initialization
    */
    public static boolean isPatch(Item item) {
        return PATCH_ITEMS.containsKey(item);
    }

    /**
    * Should only be called after world initialization
    */
    public static Optional<PlateRecipe> getPlateRecipe(Item item) {
        return Optional.ofNullable(PLATE_RECIPES.get(item));
    }

    /**
    * Should only be called after world initialization
    */
    public static boolean isPlate(Item item) {
        return PLATE_RECIPES.containsKey(item);
    }
}