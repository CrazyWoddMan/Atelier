package crazywoddman.atelier.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

public class SewingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack result;
    private final NonNullList<CountableIngredient> ingredients;
    private final CountableIngredient spool;

    public SewingRecipe(
        ResourceLocation id,
        ItemStack result, 
        NonNullList<CountableIngredient> ingredients, 
        CountableIngredient spool
    ) {
        this.id = id;
        this.result = result;
        this.ingredients = ingredients;
        this.spool = spool;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (level.isClientSide())
            return false;

        return true;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SewingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return SewingRecipeType.INSTANCE;
    }

    public NonNullList<CountableIngredient> getCountableIngredients() {
        return ingredients;
    }

    public CountableIngredient getSpool() {
        return spool;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        for (CountableIngredient ingredient : ingredients) 
            list.add(ingredient.asIngredient());
        
        return list;
    }

    public static class CountableIngredient {
        private final Ingredient ingredient;
        private final int count;

        public CountableIngredient(Ingredient ingredient, int count) {
            this.ingredient = ingredient;
            this.count = count;
        }

        public static CountableIngredient empty() {
            return new CountableIngredient(Ingredient.EMPTY, 0);
        }

        public boolean isEmpty() {
            return ingredient.isEmpty() || count <= 0;
        }

        public boolean test(ItemStack stack) {
            return ingredient.test(stack);
        }

        public Ingredient asIngredient() {
            return ingredient;
        }

        public int getCount() {
            return count;
        }

        public ItemStack[] getItems() {
            ItemStack[] stacks = ingredient.getItems();

            for (ItemStack stack : stacks)
                stack.setCount(count);

            return stacks;
        }

        public void toNetwork(FriendlyByteBuf buffer) {
            ingredient.toNetwork(buffer);
            buffer.writeVarInt(count);
        }

        public static CountableIngredient fromNetwork(FriendlyByteBuf buffer) {
            return new CountableIngredient(Ingredient.fromNetwork(buffer), buffer.readVarInt());
        }

        public JsonObject toJson() {
            JsonObject json = ingredient.toJson().getAsJsonObject();

            if (count > 1)
                json.addProperty("count", count);

            return json;
        }

        public static CountableIngredient fromJson(JsonElement jsonElement) {
            JsonObject json = jsonElement.getAsJsonObject();
            JsonObject ingredientJson = new JsonObject();

            for (String key : json.keySet())
                if (!key.equals("count"))
                    ingredientJson.add(key, json.get(key));
            
            return new CountableIngredient(
                Ingredient.fromJson(ingredientJson),
                GsonHelper.getAsInt(json, "count", 1)
            );
        }
    }

    public static class SewingRecipeType implements RecipeType<SewingRecipe> {
        public static final SewingRecipeType INSTANCE = new SewingRecipeType();
    }

    public static class SewingRecipeSerializer implements RecipeSerializer<SewingRecipe> {
        public static final SewingRecipeSerializer INSTANCE = new SewingRecipeSerializer();

        @Override
        public SewingRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            
            if (ingredientsJson.size() < 1 || ingredientsJson.size() > 9)
                throw new JsonParseException("Sewing recipe must have 1-9 ingredients, found: " + ingredientsJson.size());
            
            NonNullList<CountableIngredient> ingredients = NonNullList.create();
            
            for (int i = 0; i < ingredientsJson.size(); i++)
                ingredients.add(CountableIngredient.fromJson(ingredientsJson.get(i)));

            if (ingredients.isEmpty())
                throw new JsonParseException("No valid ingredients for sewing recipe");

            return new SewingRecipe(
                id,
                CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true, true),
                ingredients,
                json.has("spool") ? CountableIngredient.fromJson(json.get("spool")) : CountableIngredient.empty()
            );
        }

        @Override
        public SewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ItemStack result = buffer.readItem();
            int ingredientCount = buffer.readVarInt();
            NonNullList<CountableIngredient> ingredients = NonNullList.withSize(
                ingredientCount, 
                CountableIngredient.empty()
            );
            
            for (int i = 0; i < ingredientCount; i++)
                ingredients.set(i, CountableIngredient.fromNetwork(buffer));

            return new SewingRecipe(
                id,
                result,
                ingredients,
                CountableIngredient.fromNetwork(buffer)
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SewingRecipe recipe) {
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.ingredients.size());

            for (CountableIngredient ingredient : recipe.ingredients)
                ingredient.toNetwork(buffer);

            recipe.spool.toNetwork(buffer);
        }
    }
}