package crazywoddman.atelier.recipes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import crazywoddman.atelier.Atelier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AtelierRecipes {
    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = 
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, Atelier.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = 
        DeferredRegister.create(Registries.RECIPE_TYPE, Atelier.MODID);

    public static final RegistryObject<RecipeSerializer<SewingRecipe>> SEWING_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register("sewing_table", SewingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<SewingRecipe>> SEWING_RECIPE_TYPE =
        RECIPE_TYPES.register("sewing_table", SewingRecipe.Type::new);

    public static final RegistryObject<RecipeSerializer<PlateRecipe>> PLATES_SERIALIZER = 
        RECIPE_SERIALIZERS.register("armor_plate", PlateRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<PlateRecipe>> PLATE_RECIPE_TYPE = 
        RECIPE_TYPES.register("armor_plate", PlateRecipe.Type::new);

    public static final RegistryObject<RecipeSerializer<ConfigurableModule>> CONFIGURABLE_MODULES_SERIALIZER = 
        RECIPE_SERIALIZERS.register("module", ConfigurableModule.Serializer::new);
    public static final RegistryObject<RecipeType<ConfigurableModule>> CONFIGURABLE_MODULES_RECIPE_TYPE = 
        RECIPE_TYPES.register("module", ConfigurableModule.Type::new);

    public static final Set<Item> SPOOL_ITEMS = new HashSet<>();
    public static final Map<Item, PlateRecipe> PLATE_ITEMS = new HashMap<>();
    public static final Map<String, Map<Item, Map<String, ConfigurableModule>>> CONFIGURABLE_MODULES = new HashMap<>();

    private static void addConfigurableModule(ConfigurableModule recipe) {
        String parent = recipe.parent.orElse("") + recipe.parentIndex.map(index -> " " + index).orElse("");
        Map<Item, Map<String, ConfigurableModule>> items = CONFIGURABLE_MODULES.get(recipe.module);

        if (items == null) {
            items = new HashMap<>();
            Map<String, ConfigurableModule> parents = new HashMap<>();
            parents.put(parent, recipe);

            for (ItemStack item : recipe.wearable.getItems())
                items.put(item.getItem(), parents);

            CONFIGURABLE_MODULES.put(recipe.module, items);
        } else
            for (ItemStack stack : recipe.wearable.getItems()) {
                Item item = stack.getItem();

                if (!items.containsKey(item)) {
                    Map<String, ConfigurableModule> parents = new HashMap<>();
                    parents.put(parent, recipe);
                    items.put(item, parents);
                } else
                    items.get(item).put(parent, recipe);
            }
    }

    public static void reload(RecipeManager recipeManager) {
        PLATE_ITEMS.clear();
        CONFIGURABLE_MODULES.clear();
        SPOOL_ITEMS.clear();

        for (SewingRecipe recipe : recipeManager.getAllRecipesFor(SEWING_RECIPE_TYPE.get()))
            for (ItemStack item : recipe.spool.getItems())
                SPOOL_ITEMS.add(item.getItem());
        
        for (PlateRecipe recipe : recipeManager.getAllRecipesFor(PLATE_RECIPE_TYPE.get()))
            for (ItemStack stack : recipe.plate.getItems())
                PLATE_ITEMS.put(stack.getItem(), recipe);

        for (ConfigurableModule recipe : recipeManager.getAllRecipesFor(CONFIGURABLE_MODULES_RECIPE_TYPE.get()))
            addConfigurableModule(recipe);
    }

    public static Optional<ConfigurableModule> getConfigurableModule(String slot, Item item, String parent, byte index) {
        return Optional.ofNullable(CONFIGURABLE_MODULES.get(slot)).flatMap(items ->
            Optional.ofNullable(items.get(item)).flatMap(parents ->
                Optional.ofNullable(parents.getOrDefault(parent + " " + index, parents.getOrDefault(parent, parents.get(""))))
            )
        );
    }

    public static Map<String, Integer> getConfigurableModules(Item item, String parent, byte index) {
        Map<String, Integer> modules = new HashMap<>();

        for (String slot : CONFIGURABLE_MODULES.keySet())
            Optional.ofNullable(CONFIGURABLE_MODULES.get(slot).get(item)).ifPresent(parents -> {
                if (parents.containsKey("") || parents.containsKey(parent) || parents.containsKey(parent + " " + index))
                    modules.put(slot, parents.getOrDefault(parent + " " + index, parents.getOrDefault(parent, parents.get(""))).variants.length);
            });
        
        return modules;

    }

    public static boolean hasConfigurableModules(Item item) {
        for (String slot : CONFIGURABLE_MODULES.keySet())
            if (CONFIGURABLE_MODULES.get(slot).containsKey(item))
                return true;

        return false;
    }

    public static boolean isPlate(Item item) {
        return PLATE_ITEMS.containsKey(item);
    }

    public static Optional<PlateRecipe> getPlateRecipe(Item item) {
        return Optional.ofNullable(PLATE_ITEMS.get(item));
    }

    public static Optional<Integer> getPlateDurability(Item item) {
        return getPlateRecipe(item).flatMap(recipe -> recipe.durability);
    }
}