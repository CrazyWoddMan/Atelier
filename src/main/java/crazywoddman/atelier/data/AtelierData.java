package crazywoddman.atelier.data;

import java.util.HashSet;
import java.util.Set;

import crazywoddman.atelier.Atelier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AtelierData {
    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
    }

    private static class RecipeTypeRegister {
        private final DeferredRegister<RecipeType<?>> delegate;

        private RecipeTypeRegister(String modid) {
            this.delegate = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, modid);
        }

        private void register(IEventBus bus) {
            delegate.register(bus);
        }

        private <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(String name) {
            return this.delegate.register(name, () -> new RecipeType<T>(){});
        }
    }

    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Atelier.MODID);
    private static final RecipeTypeRegister RECIPE_TYPES = new RecipeTypeRegister(Atelier.MODID);

    public static final RegistryObject<RecipeSerializer<SewingRecipe>> SEWING_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register("sewing_table", SewingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<SewingRecipe>> SEWING_RECIPE_TYPE = RECIPE_TYPES.register("sewing_table");

    public static final RegistryObject<RecipeSerializer<DyeableTransformRecipe>> DYEABLE_TRANSFORM_SERIALIZER = 
        RECIPE_SERIALIZERS.register("dyeable_transform", DyeableTransformRecipe.Serializer::new);

    public static final Set<Item> SPOOL_ITEMS = new HashSet<>();
    public static boolean isDirty;

    public static void reload(RecipeManager recipeManager, boolean isClient) {
        if (!isClient) {
            ModulesDataSerializer.cache();
            ArmorPlates.reload();
        }
        
        SPOOL_ITEMS.clear();

        for (SewingRecipe recipe : recipeManager.getAllRecipesFor(SEWING_RECIPE_TYPE.get())) {
            recipe.getSpool().ifPresent(spool -> {
                for (ItemStack item : spool.ingredient.getItems())
                    SPOOL_ITEMS.add(item.getItem());
            });
        }
    }
}