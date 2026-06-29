package crazywoddman.atelier.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddReloadListenerEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IModular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

public class ModulesDataSerializer extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<Ingredient, Set<SimpleSlot>> MODULES = new HashMap<>();

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new ModulesDataSerializer());
    }

    private ModulesDataSerializer() {
        super(GSON, Atelier.MODID + "/modules");
    }
    

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager resourceManager, ProfilerFiller profiler) {
        MODULES.clear();
        
        data.forEach((id, json) -> {
            try {
                JsonObject obj = json.getAsJsonObject();
                Ingredient ingredient = CraftingHelper.getIngredient(obj.get("wearable"), false);
                Set<SimpleSlot> modules = MODULES.get(ingredient);

                if (modules == null) {
                    modules = new HashSet<>();
                    MODULES.put(ingredient, modules);
                }

                modules.add(SimpleSlot.of(
                    GsonHelper.getAsString(obj, "module"),
                    GsonHelper.getAsInt(obj, "amount", 1)
                ));
            } catch (Exception e) {
                LOGGER.error("Failed to load module data from {}", id, e);
            }
        });
    }

    public static void cache() {
        IModular.CACHE.clear();
        MODULES.forEach((ingredient, modules) -> {
            for (SimpleSlot module : modules) {
                for (ItemStack stack : ingredient.getItems()) {
                    Item item = stack.getItem();
                    List<String> list = IModular.CACHE.get(item);

                    if (list == null) {
                        list = new ArrayList<>();
                        IModular.CACHE.put(item, list);
                    }

                    for (int i = 0; i < module.index; i++)
                        list.add(module.name);
                }
            }
        });
        IModular.CACHE.values().forEach(Collections::sort);
    }
}