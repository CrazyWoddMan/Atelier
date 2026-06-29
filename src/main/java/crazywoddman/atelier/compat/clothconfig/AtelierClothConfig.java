package crazywoddman.atelier.compat.clothconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.client.LocalPlayerVars;
import crazywoddman.atelier.data.WearablesCapability.WearableState;
import crazywoddman.atelier.network.packets.WearCapToServerPacket;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.DoubleFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.EnumSelectorBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.StringListBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

public class AtelierClothConfig {

    @SuppressWarnings("removal")
    public static void registerConfigScreen() {
        FMLJavaModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((mc, screen) -> {
            ConfigBuilder builder = ConfigBuilder
                .create()
                .setParentScreen(screen)
                .setTitle(Component.literal("Atelier Config"));
            builder.setGlobalized(false);
            builder.setTransparentBackground(true);
            ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

            Category client = new Category(builder, entryBuilder, "Client");
            client.addField(AtelierConfig.Client.FPP_OVERLAYS);
            client.addField(AtelierConfig.Client.NVD_MODE);
            client.addField(AtelierConfig.Client.THIRD_PERSON_NVD);
            client.addField(AtelierConfig.Client.EYES_LEVEL).setSaveConsumer(v -> {
                AtelierConfig.Client.EYES_LEVEL.set(v);
                LocalPlayerVars.wearables.eyesLevel = v.byteValue();
                WearCapToServerPacket.send(WearableState.EYES_LEVEL);
            });
            client.addField(AtelierConfig.Client.EYE_PATCH).setSaveConsumer(v -> {
                AtelierConfig.Client.EYE_PATCH.set(v);
                LocalPlayerVars.wearables.eyePatch = v;
                WearCapToServerPacket.send(WearableState.EYE_PATCH);
            });
            client.addField(AtelierConfig.Client.QUICK_ACCESS_GUI);
            client.addField(AtelierConfig.Client.QUICK_ACCESS_KEYBINDS);
            client.addField(AtelierConfig.Client.QUICK_ACCESS_ALIGN);
            client.addField(AtelierConfig.Client.QUICK_ACCESS_X).setMin(0);
            client.addField(AtelierConfig.Client.QUICK_ACCESS_Y).setMin(0);
            client.addField(AtelierConfig.Client.QUICK_ACCESS_MODULES)
                .setSaveConsumer(v -> {AtelierConfig.Client.QUICK_ACCESS_MODULES.set(v); IQuickAccess.reload();})
                .setErrorSupplier(list -> list
                    .stream()
                    .filter(s -> s.isEmpty() || !s.matches("^[a-z_]+$"))
                    .map(String::valueOf)
                    .reduce((x, y) -> x + ", " + y)
                    .map(s -> Component.translatable(Atelier.MODID + ".config.QuickAccess.modules.invalid", s))
                );
            client.addField(AtelierConfig.Client.QUICK_ACCESS_BLACKLIST)
                .setSaveConsumer(v -> {AtelierConfig.Client.QUICK_ACCESS_BLACKLIST.set(v); IQuickAccess.reload();})
                .setErrorSupplier(list -> list
                    .stream()
                    .filter(s -> {
                        ResourceLocation key = ResourceLocation.tryParse(s);
                        return key == null || !(ForgeRegistries.ITEMS.getValue(key) instanceof IQuickAccess);
                    })
                    .map(String::valueOf)
                    .reduce((x, y) -> x + ", " + y)
                    .map(s -> Component.translatable(Atelier.MODID + ".config.QuickAccess.itemsBlacklist.invalid", s))
                );
            client.addField(AtelierConfig.Client.HALAL_MODE);
            client.build();

            if (Minecraft.getInstance().getSingleplayerServer() != null) {
                Category server = new Category(builder, entryBuilder, "Server");
                server.addField(AtelierConfig.Server.KNEEPADS_PROTECT).setMin(0).setMax(1);
                server.addField(AtelierConfig.Server.NVD_CONSUME).setMin(0).setSaveConsumer(v -> {
                    AtelierConfig.Server.NVD_CONSUME.set(v);
                    IQuickAccess.reload();
                });
                server.addField(AtelierConfig.Server.POUCH_CAPACITY).setMin(1).setMax(9);
                server.addField(AtelierConfig.Server.WEBBING_CAPACITY).setMin(1).setMax(27);
                server.addField(AtelierConfig.Server.HAZMAT_WARIUM_RAD);
                server.addField(AtelierConfig.Server.BOMBVEST_MODE);
                server.addField(AtelierConfig.Server.BOMBVEST_POWER).setMin(0);
                server.build();
            }
            
            return builder.build();
        }));
    }

    private static class Category {
        final ConfigEntryBuilder builder;
        final ConfigCategory category;
        final List<AbstractFieldBuilder<?, ?, ?>> fields = new ArrayList<>();

        Category(ConfigBuilder builder, ConfigEntryBuilder entryBuilder, String name) {
            this.builder = entryBuilder;
            this.category = builder.getOrCreateCategory(Component.literal(name));
        }

        void build() {
            Map<String, SubCategoryBuilder> subs = new LinkedHashMap<>();

            for (AbstractFieldBuilder<?, ?, ?> field : this.fields) {
                String subname = Arrays.stream(((TranslatableContents)field.getFieldNameKey().getContents()).getKey().split("\\."), 0, 3).collect(Collectors.joining("."));
                SubCategoryBuilder sub = subs.get(subname);

                if (sub == null)
                    subs.put(subname, sub = this.builder.startSubCategory(Component.translatable(subname)).setExpanded(true));

                sub.add(field.build());
            }

            subs.forEach((name, sub) -> this.category.addEntry(sub.build()));
        }

        IntFieldBuilder addField(IntValue value) {
            return addField(this.builder.startIntField(getName(value), value.get()).setDefaultValue(value.getDefault()).setSaveConsumer(value::set));
        }

        DoubleFieldBuilder addField(DoubleValue value) {
            return addField(this.builder.startDoubleField(getName(value), value.get()).setDefaultValue(value.getDefault()).setSaveConsumer(value::set));
        }

        BooleanToggleBuilder addField(BooleanValue value) {
            return addField(this.builder.startBooleanToggle(getName(value), value.get()).setDefaultValue(value.getDefault()).setSaveConsumer(value::set));
        }

        <T extends Enum<T>> EnumSelectorBuilder<T> addField(EnumValue<T> value) {
            T v = value.get();
            return addField(this.builder.startEnumSelector(getName(value), v.getDeclaringClass(), v).setDefaultValue(value.getDefault()).setSaveConsumer(value::set));
        }

        @SuppressWarnings("unchecked")
        StringListBuilder addField(ConfigValue<List<? extends String>> value) {
            return addField(this.builder.startStrList(getName(value), (List<String>)value.get())
                .setDefaultValue((List<String>)value.getDefault())
                .setSaveConsumer(value::set)
            );
        }

        <T extends AbstractFieldBuilder<?, ?, ?>> T addField(T field) {
            this.fields.add(field);
            List<Component> tooltip = new ArrayList<>();
            String description = ((TranslatableContents)field.getFieldNameKey().getContents()).getKey() + ".desc";

            for (int i = 0; I18n.exists(description + i); i++)
                tooltip.add(Component.translatable(description + i));

            if (tooltip.size() > 0)
                field.setTooltip(tooltip.toArray(Component[]::new));

            return field;
        }

        static Component getName(ConfigValue<?> value) {
            return Component.translatable(Atelier.MODID + ".config." + value.getPath().stream().map(s -> s.replace(" ", "")).collect(Collectors.joining(".")));
        }
    }
}
