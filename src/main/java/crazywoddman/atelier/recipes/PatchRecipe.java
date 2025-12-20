package crazywoddman.atelier.recipes;

import java.util.Optional;

import org.joml.Vector3f;
import org.joml.Vector3i;

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

public class PatchRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final String slot;
    private final String parent;
    private final BodyPart bodyPart;
    private final Ingredient wearable;
    private final float scale;
    private final Vector3i rotation;
    private final Vector3f translate;
    
    public enum BodyPart {
        HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG;
    }

    public PatchRecipe(ResourceLocation id, String slot, String parent, BodyPart bodyPart, Ingredient wearable, float scale, Vector3i rotation, Vector3f translate) {
        this.id = id;
        this.slot = slot;
        this.parent = parent;
        this.bodyPart = bodyPart;
        this.wearable = wearable;
        this.rotation = rotation;
        this.scale = scale;
        this.translate = translate;
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
        return PatchesSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return PatchRecipeType.INSTANCE;
    }

    public String getSlot() {
        return this.slot;
    }

    public String getParent() {
        return this.parent;
    }

    public BodyPart getBodyPart() {
        return this.bodyPart;
    }

    public Ingredient getWearable() {
        return this.wearable;
    }

    public Optional<Vector3i> getRotation() {
        return this.rotation.equals(new Vector3i()) ? Optional.empty() : Optional.of(this.rotation);
    }

    public Optional<Float> getScale() {
        return this.scale == 1.0f ? Optional.empty() : Optional.of(this.scale);
    }

    public Optional<Vector3f> getTranslate() {
        return this.translate.equals(new Vector3f()) ? Optional.empty() : Optional.of(this.translate);
    }

    public static class PatchRecipeType implements RecipeType<PatchRecipe> {
        public static final PatchRecipeType INSTANCE = new PatchRecipeType();
    }

    public static class PatchesSerializer implements RecipeSerializer<PatchRecipe> {
        public static final PatchesSerializer INSTANCE = new PatchesSerializer();

        @Override
        public PatchRecipe fromJson(ResourceLocation recipeId, JsonObject recipeJson) {
            String slot = GsonHelper.getAsString(recipeJson, "slot");
            String parent = GsonHelper.getAsString(recipeJson, "parent");

            BodyPart bodyPart = BodyPart.valueOf(GsonHelper.getAsString(recipeJson, "bodypart"));

            Ingredient wearable = CraftingHelper.getIngredient(recipeJson.get("wearable"), false);

            Vector3i rotation = new Vector3i();

            if (recipeJson.has("rotation")) {
                JsonObject rotationJson = GsonHelper.getAsJsonObject(recipeJson, "rotation");
                rotation.x = GsonHelper.getAsInt(rotationJson, "x");
                rotation.y = GsonHelper.getAsInt(rotationJson, "y");
                rotation.z = GsonHelper.getAsInt(rotationJson, "z");
            }

            float scale = recipeJson.has("scale") ? GsonHelper.getAsFloat(recipeJson, "scale") : 1.0f;

            Vector3f translate = new Vector3f();

            if (recipeJson.has("translate")) {
                JsonObject translateJson = GsonHelper.getAsJsonObject(recipeJson, "translate");
                translate.x = GsonHelper.getAsFloat(translateJson, "x");
                translate.y = GsonHelper.getAsFloat(translateJson, "y");
                translate.z = GsonHelper.getAsFloat(translateJson, "z");
            }

            return new PatchRecipe(recipeId, slot, parent, bodyPart, wearable, scale, rotation, translate);
        }

        @Override
        public PatchRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String slot = buffer.readUtf();
            String parent = buffer.readUtf();
            BodyPart bodyPart = buffer.readEnum(BodyPart.class);
            Ingredient wearable = Ingredient.fromNetwork(buffer);
            float scale = buffer.readFloat();
            
            Vector3i rotation = new Vector3i(
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt()
            );
            
            Vector3f translate = new Vector3f(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readFloat()
            );
            
            return new PatchRecipe(recipeId, slot, parent, bodyPart, wearable, scale, rotation, translate);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PatchRecipe recipe) {
            buffer.writeUtf(recipe.slot);
            buffer.writeUtf(recipe.parent);
            buffer.writeEnum(recipe.bodyPart);
            recipe.wearable.toNetwork(buffer);
            buffer.writeFloat(recipe.scale);
            
            buffer.writeVarInt(recipe.rotation.x);
            buffer.writeVarInt(recipe.rotation.y);
            buffer.writeVarInt(recipe.rotation.z);
            
            buffer.writeFloat(recipe.translate.x);
            buffer.writeFloat(recipe.translate.y);
            buffer.writeFloat(recipe.translate.z);
        }
    }
}