package crazywoddman.atelier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import crazywoddman.atelier.api.WearablesRegister;
import crazywoddman.atelier.blocks.AtelierBlockEntities;
import crazywoddman.atelier.blocks.AtelierBlocks;
import crazywoddman.atelier.compat.terrablender.CottonField;
import crazywoddman.atelier.config.Config;
import crazywoddman.atelier.effects.AtelierEffects;
import crazywoddman.atelier.events.AccessoriesEvents;
import crazywoddman.atelier.gui.AtelierMenuTypes;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.network.NetworkHandler;
import crazywoddman.atelier.recipes.AtelierRecipes;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotBasedPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(Atelier.MODID)
public class Atelier {
    private static class FilterBrewing implements IBrewingRecipe {
        private final Predicate<CompoundTag> input;
        private final Predicate<ItemStack> ingredient;
        private final BiConsumer<CompoundTag, CompoundTag> result;
        private FilterBrewing(Predicate<CompoundTag> input, Predicate<ItemStack> ingredient, BiConsumer<CompoundTag, CompoundTag> result) {
            this.input = input;
            this.ingredient = ingredient;
            this.result = result;
        }

        @Override
        public boolean isInput(ItemStack input) {
            return input.is(AtelierTags.Items.GAS_FILTERS) ? this.input.test(input.getTag()) : false;
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            return this.ingredient.test(ingredient);
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (!isInput(input) || !isIngredient(ingredient))
                return  ItemStack.EMPTY;
            
            ItemStack result = input.copy();
            this.result.accept(result.getOrCreateTag(), ingredient.getTag());
            return result;
        }
        
    }
    public static final String MODID = "atelier";

    private static final ModList modlist = ModList.get();
    public static final boolean CLOTH_CONFIG_LOADED = modlist.isLoaded("cloth_config");
    public static final boolean WARIUM_LOADED = modlist.isLoaded("crusty_chunks");
    public static final boolean JEI_LOADED = modlist.isLoaded("jei");
    public static final boolean TERRABLENDER_LOADED = modlist.isLoaded("terrablender");

    public Atelier(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();

        bus.addListener(this::commonSetup);
        
        NetworkHandler.register();
        AtelierItems.register(bus);
        AtelierBlocks.register(bus);
        AtelierBlockEntities.register(bus);
        AtelierMenuTypes.register(bus);
        AtelierRecipes.register(bus);
        AtelierEffects.register(bus);
        AtelierSounds.register(bus);
        AccessoriesEvents.register();
        MinecraftForge.EVENT_BUS.register(AccessoriesEvents.class);
        Config.register(context);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (TERRABLENDER_LOADED)
                CottonField.register();

            BrewingRecipeRegistry.addRecipe(new FilterBrewing(
                input -> input == null || (!input.getBoolean("isPrepared") && !input.contains("effects")),
                ingredient -> ingredient.is(Items.MILK_BUCKET),
                (input, ingredient) -> input.putBoolean("isPrepared", true)
            ));
            BrewingRecipeRegistry.addRecipe(new FilterBrewing(
                input -> input != null && input.getBoolean("isPrepared"),
                ingredient -> ingredient.getItem() instanceof PotionItem && AtelierTags.Potions.get(AtelierTags.Potions.GAS_FILTER).contains(PotionUtils.getPotion(ingredient)),
                (input, ingredient) -> {
                    input.remove("isPrepared");
                    ListTag effects = new ListTag();

                    for (MobEffectInstance effect : PotionUtils.getAllEffects(ingredient))
                        effects.add(StringTag.valueOf(ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect()).toString()));

                    input.put("effects", effects);
                }
            ));
            BrewingRecipeRegistry.addRecipe(new FilterBrewing(
                input -> input != null && input.getBoolean("isPrepared"),
                ingredient -> ingredient.is(Items.WITHER_ROSE),
                (input, ingredient) -> {
                    input.remove("isPrepared");
                    ListTag effects = new ListTag();
                    effects.add(StringTag.valueOf("minecraft:wither"));
                    input.put("effects", effects);
                }
            ));
            AccessoriesAPI.registerPredicate(
                ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "armor_plate"),
                SlotBasedPredicate.ofItem(AtelierRecipes::isPlate)
            );

            for (RegistryObject<Item> registry : WearablesRegister.ALL) {
                Item item = registry.get();

                if (item instanceof Accessory accessory)
                    AccessoriesAPI.registerAccessory(item, accessory);
            }
        });
    }

    public static class Queue {
        private static Map<LivingEntity, List<DelayedRunnable>> SERVER = new HashMap<>();
        private static Map<LivingEntity, Map<String, DelayedRunnable>> SERVER_NAMED = new HashMap<>();
        private static Map<LivingEntity, List<DelayedRunnable>> CLIENT = new HashMap<>();
        private static Map<LivingEntity, Map<String, DelayedRunnable>> CLIENT_NAMED = new HashMap<>();

        public static boolean hasTask(LivingEntity entity, String task) {
            Map<String, DelayedRunnable> runnables = (entity.level().isClientSide ? CLIENT_NAMED : SERVER_NAMED).get(entity);
            return runnables == null ? false : runnables.containsKey(task);
        }

        public static void addToQueue(LivingEntity entity, Runnable runnable, int tickDelay) {
            Map<LivingEntity, List<DelayedRunnable>> queue = entity.level().isClientSide ? Queue.CLIENT : Queue.SERVER;
            
            if (!queue.containsKey(entity))
                queue.put(entity, new ArrayList<>());
            
            queue.get(entity).add(new DelayedRunnable(runnable, tickDelay));
        }

        public static void addToQueue(LivingEntity entity, Runnable runnable) {
            addToQueue(entity, runnable, 0);
        }

        public static void addToQueue(LivingEntity entity, String name, Runnable runnable, int tickDelay) {
            Map<LivingEntity, Map<String, DelayedRunnable>> queue = entity.level().isClientSide ? Queue.CLIENT_NAMED : Queue.SERVER_NAMED;

            if (!queue.containsKey(entity))
                queue.put(entity, new LinkedHashMap<>());

            Map<String, DelayedRunnable> subqueue = queue.get(entity);

            if (!subqueue.containsKey(name))
                subqueue.put(name, new DelayedRunnable(runnable, tickDelay));
        }

        public static void addToQueue(LivingEntity entity, String name, Runnable runnable) {
            addToQueue(entity, name, runnable, 0);
        }

        public static void runQueue(LivingEntity entity) {
            boolean isClient = entity.level().isClientSide;
            List<DelayedRunnable> runnables = (isClient ? Queue.CLIENT : Queue.SERVER).get(entity);

            if (runnables != null) {
                for (int i = 0; i < runnables.size();) {
                    DelayedRunnable runnable = runnables.get(i);

                    if (runnable.delay == 0) {
                        runnable.runnable.run();
                        runnables.remove(i);
                    } else {
                        runnable.delay--;
                        i++;
                    }
                }

                if (runnables.isEmpty())
                    (isClient ? Queue.CLIENT : Queue.SERVER).remove(entity);
            }

            Map<String, DelayedRunnable> namedRunnables = (isClient ? Queue.CLIENT_NAMED : Queue.SERVER_NAMED).get(entity);

            if (namedRunnables != null) {
                for (String name : namedRunnables.keySet()) {
                    DelayedRunnable runnable = namedRunnables.get(name);

                    if (runnable.delay == 0) {
                        runnable.runnable.run();
                        namedRunnables.remove(name);
                    }
                    else
                        runnable.delay--;
                }

                if (namedRunnables.isEmpty())
                    (isClient ? Queue.CLIENT_NAMED : Queue.SERVER_NAMED).remove(entity);
            }
        }

        public static class DelayedRunnable {
            public final Runnable runnable;
            public int delay;

            private DelayedRunnable(Runnable runnable, int tickDelay) {
                this.runnable = runnable;
                this.delay = tickDelay;
            }
        }
    }
}
