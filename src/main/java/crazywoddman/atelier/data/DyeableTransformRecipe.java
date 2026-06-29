package crazywoddman.atelier.data;

import com.google.gson.JsonObject;

import crazywoddman.atelier.api.interfaces.IDyeable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DyeableTransformRecipe extends ShapelessRecipe {
    public DyeableTransformRecipe(ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AtelierData.DYEABLE_TRANSFORM_SERIALIZER.get();
    }
    
    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ItemStack result = super.assemble(container, access);

        for (ItemStack stack : container.getItems()) {
            if (!stack.isEmpty() && stack.getItem() instanceof IDyeable) {
                IDyeable.copyColors(stack, result);
                break;
            }
        }

        return result;
    }

    public static class Serializer implements RecipeSerializer<DyeableTransformRecipe> {
        public DyeableTransformRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new DyeableTransformRecipe(RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json));
        }

        public DyeableTransformRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new DyeableTransformRecipe(RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buffer));
        }

        public void toNetwork(FriendlyByteBuf buffer, DyeableTransformRecipe recipe) {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
        }
   }
}