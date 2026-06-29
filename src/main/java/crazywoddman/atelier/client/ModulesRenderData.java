package crazywoddman.atelier.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModulesRenderData extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Item, Map<String, Map<SimpleSlot, ModuleRenderData[]>>> RENDER_DATA = new HashMap<>();

    public static Optional<ModuleRenderData> get(Item item, SimpleSlot module, SimpleSlot parent) {
        Map<String, Map<SimpleSlot, ModuleRenderData[]>> modules = RENDER_DATA.get(item);

        if (modules != null) {
            Map<SimpleSlot, ModuleRenderData[]> parents = modules.get(module.name);

            if (parents != null) {
                ModuleRenderData[] data = parents.get(null);

                if (data == null) {
                    data = parents.get(parent);

                    if (data == null && parent.index >= SimpleSlot.NO_INDEX) {
                        data = parents.get(SimpleSlot.of(null, parent.index));

                        if (data == null)
                            data = parents.get(SimpleSlot.of(parent.name, SimpleSlot.NO_INDEX));
                    }
                }

                if (data != null && data.length > module.index)
                    return Optional.of(data[module.index]);
            }
        }

        return Optional.empty();
    }

    public enum BodyPart {
        HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG;
        public ModelPart getModelPart(HumanoidModel<?> model) {
            return switch (this) {
                case HEAD -> model.head;
                case BODY -> model.body;
                case LEFT_ARM -> model.leftArm;
                case RIGHT_ARM -> model.rightArm;
                case LEFT_LEG -> model.leftLeg;
                case RIGHT_LEG -> model.rightLeg;
            };
        }
    }

    public static void register(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ModulesRenderData());
    }

    private ModulesRenderData() {
        super(GSON, Atelier.MODID + "/modules");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager resourceManager, ProfilerFiller profiler) {
        RENDER_DATA.clear();
        
        data.forEach((id, json) -> {
            try {
                JsonObject obj = json.getAsJsonObject();
                JsonArray variantsArray = GsonHelper.getAsJsonArray(obj, "variants");
                ModuleRenderData[] variants = new ModuleRenderData[variantsArray.size()];

                for (int i = 0; i < variants.length; i++) {
                    JsonObject variantJson = variantsArray.get(i).getAsJsonObject();
                    variants[i] = new ModuleRenderData(
                        BodyPart.valueOf(GsonHelper.getAsString(variantJson, "bodypart")),
                        parseArray(variantJson, "translate"),
                        parseArray(variantJson, "rotation"),
                        parseArray(variantJson, "scale")
                    );
                }

                String module = GsonHelper.getAsString(obj, "module");
                String parentName = GsonHelper.getAsString(obj, "parent", null);
                SimpleSlot parent;

                try {
                    parent = SimpleSlot.of(EquipmentSlot.byName(parentName));
                } catch (IllegalArgumentException e) {
                    parent = SimpleSlot.of(parentName, GsonHelper.getAsByte(obj, "index", SimpleSlot.NO_INDEX));
                }

                for (ItemStack stack : Ingredient.fromJson(obj.get("wearable")).getItems()) {
                    Item item = stack.getItem();
                    Map<String, Map<SimpleSlot, ModuleRenderData[]>> modules = RENDER_DATA.get(item);

                    if (modules == null) {
                        modules = new HashMap<>();
                        RENDER_DATA.put(item, modules);
                    }

                    Map<SimpleSlot, ModuleRenderData[]> parents = modules.get(module);

                    if (parents == null) {
                        parents = new HashMap<>();
                        modules.put(module, parents);
                    }

                    parents.put(parent, variants);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load module render data from {}", id, e);
            }
        });
    }

    private static float[] parseArray(JsonObject json, String key) {
        if (!json.has(key))
            return null;

        float[] array = new float[3];
        JsonArray jsonArray = GsonHelper.getAsJsonArray(json, key);
        array[0] = jsonArray.get(0).getAsFloat();
        array[1] = jsonArray.get(1).getAsFloat();
        array[2] = jsonArray.get(2).getAsFloat();

        return array;
    }

    public static class ModuleRenderData {
        public final BodyPart bodyPart;
        private final float[] translate;
        private final float[] rotation;
        private final float[] scale;

        public ModuleRenderData(BodyPart bodyPart, float[] translate, float[] rotation, float[] scale) {
            this.bodyPart = bodyPart;
            this.translate = translate;
            this.rotation = rotation;
            this.scale = scale;
        }

        public Optional<float[]> getTranslate() {
            return Optional.ofNullable(this.translate);
        }

        public Optional<float[]> getRotation() {
            return Optional.ofNullable(this.rotation);
        }

        public Optional<float[]> getScale() {
            return Optional.ofNullable(this.scale);
        }
    }
}