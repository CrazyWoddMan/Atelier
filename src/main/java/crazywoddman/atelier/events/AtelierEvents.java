package crazywoddman.atelier.events;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.compat.terrablender.CottonField;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.items.templates.FilterItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = Atelier.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AtelierEvents {

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == AtelierConfig.Client.SPEC)
            IQuickAccess.reload();
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(AtelierEvents::commonSetup);
    }

    private static void commonSetup() {
        ForgeRegistries.ITEMS.forEach(item -> {
            if (item instanceof IQuickAccess)
                AtelierItems.QUICK_ACCESS.add(item);

            if (item instanceof IWearableAccessory)
                CompatHelper.registerItem(item);
        });

        if (Atelier.TERRABLENDER_LOADED)
            CottonField.register();

        BrewingRecipeRegistry.addRecipe(new FilterBrewing(
            input -> input == null || (!input.getBoolean(FilterItem.PREPARED_TAG) && !input.contains(FilterItem.EFFECTS_TAG)),
            ingredient -> ingredient.is(Items.MILK_BUCKET),
            (input, ingredient) -> input.putBoolean(FilterItem.PREPARED_TAG, true)
        ));
        BrewingRecipeRegistry.addRecipe(new FilterBrewing(
            input -> input != null && input.getBoolean(FilterItem.PREPARED_TAG),
            ingredient -> ingredient.getItem() instanceof PotionItem && AtelierTags.Potions.get(AtelierTags.Potions.GAS_FILTER).contains(PotionUtils.getPotion(ingredient)),
            (input, ingredient) -> {
                input.remove(FilterItem.PREPARED_TAG);
                ListTag effects = new ListTag();

                for (MobEffectInstance effect : PotionUtils.getAllEffects(ingredient))
                    effects.add(StringTag.valueOf(ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect()).toString()));

                input.put(FilterItem.EFFECTS_TAG, effects);
            }
        ));
        BrewingRecipeRegistry.addRecipe(new FilterBrewing(
            input -> input != null && input.getBoolean(FilterItem.PREPARED_TAG),
            ingredient -> ingredient.is(Items.WITHER_ROSE),
            (input, ingredient) -> {
                input.remove(FilterItem.PREPARED_TAG);
                ListTag effects = new ListTag();
                effects.add(StringTag.valueOf("minecraft:wither"));
                input.put(FilterItem.EFFECTS_TAG, effects);
            }
        ));
        CompatHelper.onCommonSetup();
    }

    // TODO: improve
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
}
