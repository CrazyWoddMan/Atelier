package crazywoddman.atelier.recipes;

import java.util.Optional;

import org.joml.Vector3f;

import com.google.gson.JsonArray;
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

public class ConfigurableModule implements Recipe<Container> {
    private final ResourceLocation id;
    public final String module;
    public final Ingredient wearable;
    public final Optional<String> parent;
    public final Optional<Byte> parentIndex;
    public final Variant[] variants;
    
    public enum BodyPart {
        HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG;
    }

    public record Variant(BodyPart bodyPart, float scale, Vector3f rotation, Vector3f translate) {
        public Optional<Vector3f> getTranslate() {
            return this.translate.equals(new Vector3f()) ? Optional.empty() : Optional.of(this.translate);
        }

        public Optional<Vector3f> getRotation() {
            return this.rotation.equals(new Vector3f()) ? Optional.empty() : Optional.of(this.rotation);
        }

        public Optional<Float> getScale() {
            return this.scale == 1.0f ? Optional.empty() : Optional.of(this.scale);
        }
    }

    public ConfigurableModule(ResourceLocation id, String module, Ingredient wearable, Optional<String> parent, Optional<Byte> parentIndex, Variant[] variants) {
        this.id = id;
        this.module = module;
        this.wearable = wearable;
        this.parent = parent;
        this.parentIndex = parentIndex;
        this.variants = variants;
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
        return AtelierRecipes.CONFIGURABLE_MODULES_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AtelierRecipes.CONFIGURABLE_MODULES_RECIPE_TYPE.get();
    }

    public static class Type implements RecipeType<ConfigurableModule> {}

    public static class Serializer implements RecipeSerializer<ConfigurableModule> {

        @Override
        public ConfigurableModule fromJson(ResourceLocation id, JsonObject recipeJson) {
            try {
                Optional<String> parent = recipeJson.has("parent")
                    ? Optional.of(GsonHelper.getAsString(recipeJson, "parent"))
                    : Optional.empty();
                JsonArray variantsArray = GsonHelper.getAsJsonArray(recipeJson, "variants");
                Variant[] variants = new Variant[variantsArray.size()];
                
                for (int i = 0; i < variants.length; i++) {
                    JsonObject variantJson = variantsArray.get(i).getAsJsonObject();
                    
                    BodyPart bodyPart = BodyPart.valueOf(GsonHelper.getAsString(variantJson, "bodypart"));
                    float scale = variantJson.has("scale") ? GsonHelper.getAsFloat(variantJson, "scale") : 1.0f;
                    
                    Vector3f rotation = new Vector3f();
                    if (variantJson.has("rotation")) {
                        JsonArray rotationArray = GsonHelper.getAsJsonArray(variantJson, "rotation");
                        rotation.x = rotationArray.get(0).getAsFloat();
                        rotation.y = rotationArray.get(1).getAsFloat();
                        rotation.z = rotationArray.get(2).getAsFloat();
                    }
                    
                    Vector3f translate = new Vector3f();
                    if (variantJson.has("translate")) {
                        JsonArray translateArray = GsonHelper.getAsJsonArray(variantJson, "translate");
                        translate.x = translateArray.get(0).getAsFloat();
                        translate.y = translateArray.get(1).getAsFloat();
                        translate.z = translateArray.get(2).getAsFloat();
                    }
                    
                    variants[i] = new Variant(bodyPart, scale, rotation, translate);
                }
                
                return new ConfigurableModule(
                    id,
                    GsonHelper.getAsString(recipeJson, "module"),
                    CraftingHelper.getIngredient(recipeJson.get("wearable"), false),
                    parent,
                    recipeJson.has("parentindex") ? Optional.of(GsonHelper.getAsByte(recipeJson, "parentindex")) : Optional.empty(),
                    variants
                );
            } catch(Exception e) {
                throw e;
            }
        }

        @Override
        public ConfigurableModule fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String module = buffer.readUtf();
            Ingredient wearable = Ingredient.fromNetwork(buffer);
            Optional<String> parent;
            Optional<Byte> parentIndex;
            
            if (buffer.readBoolean()) {
                parent = Optional.of(buffer.readUtf());
                byte index = buffer.readByte();
                parentIndex = index == -1 ? Optional.of(index) : Optional.empty();
            } else {
                parent = Optional.empty();
                parentIndex = Optional.empty();
            }

            byte variantCount = buffer.readByte();
            Variant[] variants = new Variant[variantCount];
            
            for (int i = 0; i < variantCount; i++) {
                BodyPart bodyPart = buffer.readEnum(BodyPart.class);
                float scale = buffer.readFloat();
                
                Vector3f rotation = new Vector3f(
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat()
                );
                
                Vector3f translate = new Vector3f(
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat()
                );
                
                variants[i] = new Variant(bodyPart, scale, rotation, translate);
            }
            
            return new ConfigurableModule(recipeId, module, wearable, parent, parentIndex, variants);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ConfigurableModule recipe) {
            buffer.writeUtf(recipe.module);
            recipe.wearable.toNetwork(buffer);
            buffer.writeBoolean(recipe.parent.isPresent());
            recipe.parent.ifPresent(parent -> {
                buffer.writeUtf(parent);
                buffer.writeByte(recipe.parentIndex.orElse((byte)-1));
            });
            buffer.writeByte(recipe.variants.length);

            for (Variant variant : recipe.variants) {
                buffer.writeEnum(variant.bodyPart);
                buffer.writeFloat(variant.scale);
                
                buffer.writeFloat(variant.rotation.x);
                buffer.writeFloat(variant.rotation.y);
                buffer.writeFloat(variant.rotation.z);
                
                buffer.writeFloat(variant.translate.x);
                buffer.writeFloat(variant.translate.y);
                buffer.writeFloat(variant.translate.z);
            }
        }
    }
}