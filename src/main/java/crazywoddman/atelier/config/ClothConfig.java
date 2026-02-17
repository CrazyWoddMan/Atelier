package crazywoddman.atelier.config;

import java.util.Arrays;

import crazywoddman.atelier.Atelier;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.DoubleFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

public class ClothConfig {

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
            client.entry(Config.CLIENT.halalMode);

            if (Minecraft.getInstance().getSingleplayerServer() != null) {
                Category server = new Category(builder, entryBuilder, "Server");
                server.entry(Config.SERVER.kneePadsProtection, 0);
                server.entry(Config.SERVER.bombVestMaxExplosionPower, 0);
                server.entry(Config.SERVER.bombVest);
            }

            return builder.build();
        }));
    }

    private static class Category {
        private final ConfigEntryBuilder builder;
        private final ConfigCategory category;

        private Category(ConfigBuilder builder, ConfigEntryBuilder entryBuilder, String name) {
            this.builder = entryBuilder;
            this.category = builder.getOrCreateCategory(Component.literal(name));
        }

        private static String getPath(ConfigValue<?> configValue) {
            String path = Atelier.MODID + ".config";

            for (String part : configValue.getPath())
                path += "." + part;

            return path;
        }

        private void entry(ConfigValue<?> configValue) {
            category.addEntry(simpleEntry(configValue).build());
        }

        private void entry(ConfigValue<?> configValue, Object minValue) {
            category.addEntry(putLimits(simpleEntry(configValue), minValue, null));
        }

        @SuppressWarnings("unused")
        private void entry(ConfigValue<?> configValue, Object minValue, Object maxValue) {
            category.addEntry(putLimits(simpleEntry(configValue), minValue, maxValue));
        }

        private static AbstractConfigListEntry<?> putLimits(AbstractFieldBuilder<?, ?, ?> builder, Object minValue, Object maxValue) {
            if (builder instanceof IntFieldBuilder build) {
                if (minValue instanceof Integer min)
                    build = build.setMin(min);
                if (maxValue instanceof Integer max)
                    build = build.setMax(max);
                builder = build;
            } else if (builder instanceof DoubleFieldBuilder fieldBuilder) {
                if (minValue instanceof Double min)
                    fieldBuilder = fieldBuilder.setMin(min);
                if (maxValue instanceof Double max)
                    fieldBuilder = fieldBuilder.setMax(max);
                builder = fieldBuilder;
            } else 
                throw new IllegalStateException("Unsupported builder or limits type");

            return builder.build();
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private AbstractFieldBuilder<?, ?, ?> simpleEntry(ConfigValue<?> configValue) {
            String path = getPath(configValue);
            Component name = Component.translatable(path);
            AbstractFieldBuilder<?, ?, ?> builder;

            if (configValue instanceof IntValue value)
                builder = this.builder
                    .startIntField(name, value.get())
                    .setDefaultValue(value.getDefault())
                    .setSaveConsumer(value::set);
            else if (configValue instanceof BooleanValue value)
                builder = this.builder
                    .startBooleanToggle(name, value.get())
                    .setDefaultValue(value.getDefault())
                    .setSaveConsumer(value::set);
            else if (configValue instanceof DoubleValue value)
                builder = this.builder
                    .startDoubleField(name, value.get())
                    .setDefaultValue(value.getDefault())
                    .setSaveConsumer(value::set);
            else if (configValue instanceof EnumValue<?> value) {
                builder = this.builder
                    .startEnumSelector(name, (Class)value.get().getClass(), value.get())
                    .setDefaultValue(value.getDefault())
                    .setSaveConsumer(((EnumValue)value)::set);

            }
            
            else
                throw new IllegalStateException("Method can't accept such ConfigValue type");

            return prepareTooltip(builder, path);
        }

        private static AbstractFieldBuilder<?, ?, ?> prepareTooltip(AbstractFieldBuilder<?, ?, ?> builder, String path) {
            Component[] tooltips = new Component[0];
            String description = path + ".desc";

            for (int i = 0; I18n.exists(description + i); i++) {
                tooltips = Arrays.copyOf(
                    tooltips,
                    tooltips.length + 1
                );
                tooltips[tooltips.length - 1] = Component.translatable(description + i);
            }

            if (tooltips.length > 0)
                builder = (AbstractFieldBuilder<?, ?, ?>) builder.setTooltip(tooltips);

            return builder;
        }
    }
}
