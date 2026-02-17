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
    public final Ingredient plate;
    public final byte protection;
    public final Optional<Integer> durability;

    public PlateRecipe(ResourceLocation id, Ingredient plate, byte protection, int durability) {
        this.id = id;
        this.plate = plate;
        this.protection = protection;
        this.durability = durability == 0 ? Optional.empty() : Optional.of(durability);
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
        return AtelierRecipes.PLATES_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AtelierRecipes.PLATE_RECIPE_TYPE.get();
    }

    public boolean matches(ItemStack stack) {
        return this.plate.test(stack);
    }

    public static class Type implements RecipeType<PlateRecipe> {}
    public static class Serializer implements RecipeSerializer<PlateRecipe> {

        @Override
        public PlateRecipe fromJson(ResourceLocation id, JsonObject recipeJson) {
            Ingredient plate = CraftingHelper.getIngredient(recipeJson.get("plate"), false);
            byte protection = GsonHelper.getAsByte(recipeJson, "protection");
            int durability = GsonHelper.getAsInt(recipeJson, "durability", 0);

            return new PlateRecipe(id, plate, protection, durability);
        }

        @Override
        public PlateRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient plate = Ingredient.fromNetwork(buffer);
            byte protection = buffer.readByte();
            int durability = buffer.readVarInt();

            return new PlateRecipe(id, plate, protection, durability);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PlateRecipe recipe) {
            recipe.plate.toNetwork(buffer);
            buffer.writeByte(recipe.protection);
            buffer.writeVarInt(recipe.durability.orElse(0));
        }
    }
}