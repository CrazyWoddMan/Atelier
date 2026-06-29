package crazywoddman.atelier.data;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SewingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    public final Item result;
    private final @Nullable CountableIngredient spool;
    public final CountableIngredient[] ingredients;

    public SewingRecipe(
        ResourceLocation id,
        Item result,
        CountableIngredient spool,
        CountableIngredient... ingredients
    ) {
        this.id = id;
        this.result = result;
        this.ingredients = ingredients;
        this.spool = spool;
    }

    public Optional<CountableIngredient> getSpool() {
        return Optional.ofNullable(this.spool);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AtelierData.SEWING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AtelierData.SEWING_RECIPE_TYPE.get();
    }
    
    public static class Serializer implements RecipeSerializer<SewingRecipe> {

        @Override
        public SewingRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            CountableIngredient[] ingredients = new CountableIngredient[ingredientsJson.size()];
            
            if (ingredients.length > 9)
                throw new JsonParseException("Sewing recipe must have 0-9 ingredients, but only found " + ingredientsJson.size() + " in " + id);
            
            for (int i = 0; i < ingredientsJson.size(); i++)
                ingredients[i] = CountableIngredient.fromJson(ingredientsJson.get(i));

            return new SewingRecipe(
                id,
                GsonHelper.getAsItem(json, "result"),
                json.has("spool") ? CountableIngredient.fromJson(json.get("spool")) : null,
                ingredients
            );
        }

        @Override
        public SewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Item result = buffer.readItem().getItem();
            CountableIngredient spool = buffer.readBoolean() ? CountableIngredient.fromNetwork(buffer) : null;
            CountableIngredient[] ingredients = new CountableIngredient[buffer.readByte()];
            
            for (int i = 0; i < ingredients.length; i++)
                ingredients[i] = CountableIngredient.fromNetwork(buffer);

            return new SewingRecipe(
                id,
                result,
                spool,
                ingredients
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SewingRecipe recipe) {
            buffer.writeItem(new ItemStack(recipe.result));
            boolean spool = recipe.spool != null;
            buffer.writeBoolean(spool);

            if (spool)
                recipe.spool.toNetwork(buffer);
            
            buffer.writeByte(recipe.ingredients.length);

            for (CountableIngredient ingredient : recipe.ingredients)
                ingredient.toNetwork(buffer);
        }
    }
}