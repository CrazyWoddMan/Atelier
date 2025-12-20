package crazywoddman.atelier.recipes;

import java.util.Optional;

import com.google.gson.JsonObject;

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

public class PlateRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient plate;
    private final float protection;
    private final int durability;

    public PlateRecipe(ResourceLocation id, Ingredient plate, float protection, int durability) {
        this.id = id;
        this.plate = plate;
        this.protection = protection;
        this.durability = durability;
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
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        ItemStack[] items = plate.getItems();
        return items.length > 0 ? items[0].copy() : ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PlatesSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return PlateRecipeType.INSTANCE;
    }

    public Ingredient getPlate() {
        return this.plate;
    }

    public float getProtection() {
        return this.protection;
    }

    public Optional<Integer> getDurability() {
        return this.durability == -1 ? Optional.empty() : Optional.of(this.durability);
    }

    public boolean matches(ItemStack stack) {
        return this.plate.test(stack);
    }

    public static class PlateRecipeType implements RecipeType<PlateRecipe> {
        public static final PlateRecipeType INSTANCE = new PlateRecipeType();
    }

    public static class PlatesSerializer implements RecipeSerializer<PlateRecipe> {
        public static final PlatesSerializer INSTANCE = new PlatesSerializer();

        @Override
        public PlateRecipe fromJson(ResourceLocation recipeId, JsonObject recipeJson) {
            Ingredient plate = CraftingHelper.getIngredient(recipeJson.get("plate"), false);
            float protection = GsonHelper.getAsFloat(recipeJson, "protection");
            int durability = GsonHelper.getAsInt(recipeJson, "durability", -1);

            return new PlateRecipe(recipeId, plate, protection, durability);
        }

        @Override
        public PlateRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient plate = Ingredient.fromNetwork(buffer);
            float protection = buffer.readFloat();
            int durability = buffer.readInt();

            return new PlateRecipe(recipeId, plate, protection, durability);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PlateRecipe recipe) {
            recipe.plate.toNetwork(buffer);
            buffer.writeFloat(recipe.protection);
            buffer.writeInt(recipe.durability);
        }
    }
}