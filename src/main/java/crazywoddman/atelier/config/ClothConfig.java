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
    private enum LevelType {
        NULL, HOST, CLIENT;
    }
    private static ConfigEntryBuilder entryBuilder;
    private static ConfigCategory category;

    @SuppressWarnings("removal")
    public static void registerConfigScreen() {
        FMLJavaModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((mc, screen) -> {
            ConfigBuilder builder = ConfigBuilder
                .create()
                .setParentScreen(screen)
                .setTitle(Component.literal("Atelier Config"));
            builder.setGlobalized(false);
            builder.setTransparentBackground(true);
            entryBuilder = ConfigEntryBuilder.create();
            LevelType levelType = Minecraft.getInstance().level == null ? LevelType.NULL : (Minecraft.getInstance().getSingleplayerServer() == null ? LevelType.CLIENT : LevelType.HOST);
            entryBuilder.startTextDescription(
                Component.literal("§eChanges " + switch (levelType) {
                    case NULL -> "can't be made from the main menu. Enter a world";
                    case HOST -> "only apply for the current world. Re-enter the world for some changes to take effect";
                    case CLIENT -> "on server can only be made by editing config file in world/serverconfig/";
                })
            ).build();

            if (levelType != LevelType.HOST)
                return builder.build();

            category = builder.getOrCreateCategory(Component.literal("Atelier"));

            entry(Config.SERVER.kneePadsProtection, 0);
            entry(Config.SERVER.bombVestMaxExplosionPower, 0);
            entry(Config.SERVER.bombVest);
            entry(Config.SERVER.halalMode);

            return builder.build();
        }));
    }

    private static String getPath(ConfigValue<?> configValue) {
        String path = Atelier.MODID + ".config";

        for (String part : configValue.getPath())
            path += "." + part;

        return path;
    }

    // @SuppressWarnings("unused")
    private static void entry(ConfigValue<?> configValue) {
        category.addEntry(simpleEntry(configValue).build());
    }

    private static void entry(ConfigValue<?> configValue, Object minValue) {
        category.addEntry(putLimits(simpleEntry(configValue), minValue, null));
    }

    @SuppressWarnings("unused")
    private static void entry(ConfigValue<?> configValue, Object minValue, Object maxValue) {
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
    private static AbstractFieldBuilder<?, ?, ?> simpleEntry(ConfigValue<?> configValue) {
        String path = getPath(configValue);
        Component name = Component.translatable(path);
        AbstractFieldBuilder<?, ?, ?> builder;

        if (configValue instanceof IntValue value)
            builder = entryBuilder
                .startIntField(name, value.get())
                .setDefaultValue(value.getDefault())
                .setSaveConsumer(value::set);
        else if (configValue instanceof BooleanValue value)
            builder = entryBuilder
                .startBooleanToggle(name, value.get())
                .setDefaultValue(value.getDefault())
                .setSaveConsumer(value::set);
        else if (configValue instanceof DoubleValue value)
            builder = entryBuilder
                .startDoubleField(name, value.get())
                .setDefaultValue(value.getDefault())
                .setSaveConsumer(value::set);
        else if (configValue instanceof EnumValue<?> value) {
            builder = entryBuilder
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
