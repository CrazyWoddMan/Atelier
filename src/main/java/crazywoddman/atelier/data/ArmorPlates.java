package crazywoddman.atelier.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.event.AddReloadListenerEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;

import crazywoddman.atelier.Atelier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;

public class ArmorPlates extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    public static class Plate {
        public final int protection;
        private final int durability;

        Plate(int protection, int durability) {
            this.protection = protection;
            this.durability = durability;
        }

        public Optional<Integer> getDurability() {
            return this.durability > 0 ? Optional.of(this.durability) : Optional.empty();
        }
    }
    private static final Map<Ingredient, Plate> PLATES = new HashMap<>();
    public static final Map<Item, Plate> CACHE = new HashMap<>();

    public static Optional<Plate> get(Item item) {
        return Optional.ofNullable(CACHE.get(item));
    }

    public static boolean isPlate(Item item) {
        return CACHE.containsKey(item);
    }

    public static void reload() {
        CACHE.clear();

        for (Ingredient ingredient : PLATES.keySet())
            for (ItemStack stack : ingredient.getItems())
                CACHE.put(stack.getItem(), PLATES.get(ingredient));
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new ArmorPlates());
    }

    private ArmorPlates() {
        super(GSON, Atelier.MODID + "/armor_plates");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager resourceManager, ProfilerFiller profiler) {
        PLATES.clear();
        
        data.forEach((id, json) -> {
            JsonObject obj = json.getAsJsonObject();
            try {
                int protection = GsonHelper.getAsInt(obj, "protection");

                if (protection < 1 || protection > 100)
                    throw new IllegalStateException("protection value out of bounds (1-100): " + protection);

                PLATES.put(
                    Ingredient.fromJson(obj.get("plate")),
                    new Plate(protection, GsonHelper.getAsInt(obj, "durability", 0))
                );
            } catch (Exception e) {
                LOGGER.error("Failed to load armor plate from {}", id, e);
            }
        });
    }
}