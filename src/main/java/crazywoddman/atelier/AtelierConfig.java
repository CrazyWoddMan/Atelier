package crazywoddman.atelier;

import java.util.List;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.gui.NvdClientHandler;
import crazywoddman.atelier.items.simple.Detonator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

public class AtelierConfig {

    public static void register(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, Server.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, Client.SPEC);
    }

    private static ArtifactVersion getCurrentVersion() {
        return ModList.get().getModContainerById(Atelier.MODID).get().getModInfo().getVersion();
    }

    public static class Server {
        public static final ForgeConfigSpec.ConfigValue<String> VERSION;
        public static final ForgeConfigSpec.DoubleValue KNEEPADS_PROTECT;
        public static final ForgeConfigSpec.IntValue NVD_CONSUME, BOMBVEST_POWER, POUCH_CAPACITY, WEBBING_CAPACITY;
        public static final ForgeConfigSpec.BooleanValue HAZMAT_WARIUM_RAD;
        public static final ForgeConfigSpec.EnumValue<Detonator.ExplosionType> BOMBVEST_MODE;
        static final ForgeConfigSpec SPEC;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            VERSION = builder.comment("DO NOT TOUCH").define("version", getCurrentVersion().toString());

            builder.push("general");
            KNEEPADS_PROTECT = builder.comment("Amount of fall damage that Knee Pads absorb (1 = 100%)")
                .defineInRange("kneePadsProtection", 0.3, 0, 1);
            builder.pop();

            builder.push("nightVisionDevices");
            NVD_CONSUME = builder.comment("Redstone consumption rate (redstone consumed every this ticks)")
                .comment("Set 0 to disable")
                .defineInRange("redstoneConsume", 200, 0, Integer.MAX_VALUE);
            builder.pop();

            builder.push("capacities");
            POUCH_CAPACITY = builder.comment("Pouch slots amount")
                .defineInRange("pouch", 1, 1, 9);
            WEBBING_CAPACITY = builder.comment("Webbing slots amount")
                .defineInRange("webbing", 5, 1, 27);
            builder.pop();

            builder.push("bombvest");
            BOMBVEST_MODE = builder.comment(
                Detonator.ExplosionType.DISABLE.name() + ": Pouches can't detonate",
                Detonator.ExplosionType.NO_DESTRUCTION.name() + ": explosion doesn't break blocks",
                Detonator.ExplosionType.ENABLE.name() + ": default explosion"
            ).defineEnum("mode", Detonator.ExplosionType.ENABLE);
            BOMBVEST_POWER = builder.comment("After what amount of explosive items in Pouches detonation won't get any more powerful")
                .defineInRange("maxExplosionPower", 32, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("warium");
            HAZMAT_WARIUM_RAD = builder.comment("Complete Hazmat suit protects from radiation")
                .define("hazmatRadProtect", true);
            builder.pop();

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ForgeConfigSpec.ConfigValue<String> VERSION;
        public static final ForgeConfigSpec.BooleanValue FPP_OVERLAYS, QUICK_ACCESS_GUI, QUICK_ACCESS_KEYBINDS, HALAL_MODE, THIRD_PERSON_NVD;
        public static final ForgeConfigSpec.IntValue QUICK_ACCESS_X, QUICK_ACCESS_Y, EYES_LEVEL;
        public static final ForgeConfigSpec.EnumValue<WearablesCapability.EyePatch> EYE_PATCH;
        public static final ForgeConfigSpec.EnumValue<Align> QUICK_ACCESS_ALIGN;
        public static final ForgeConfigSpec.EnumValue<NvdClientHandler.Mode> NVD_MODE;
        public static final ForgeConfigSpec.ConfigValue<List<? extends String>> QUICK_ACCESS_MODULES, QUICK_ACCESS_BLACKLIST;
        public static final ForgeConfigSpec SPEC;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            VERSION = builder.comment("DO NOT TOUCH").define("version", getCurrentVersion().toString());
            
            builder.push("general");
            FPP_OVERLAYS = builder.comment("First Person overlays for items like gasmasks").define("fppOverlays", true);
            builder.pop();

            builder.push("personalization");
            EYES_LEVEL = builder.comment("Affects face accessories position for your player")
                .defineInRange("eyesLevel", WearablesCapability.DEFAULT_EYES_LEVEL, 2, 5);
            EYE_PATCH = builder.defineEnum("eyePatch", WearablesCapability.EyePatch.RIGHT);
            builder.pop();

            builder.push("nightVisionDevices");
            NVD_MODE = builder.defineEnum("mode", NvdClientHandler.Mode.NOISE_AND_OVEREXPOSURE);
            THIRD_PERSON_NVD = builder.define("thirdPerson", false);
            builder.pop();

            builder.push("quickAccessGui");
            QUICK_ACCESS_GUI = builder.define("enabled", true);
            QUICK_ACCESS_KEYBINDS = builder.define("keybindsHint", true);
            QUICK_ACCESS_ALIGN = builder.defineEnum("align", Align.BOTTOM_RIGHT);
            QUICK_ACCESS_X = builder.defineInRange("offsetX", 5, 0, Integer.MAX_VALUE);
            QUICK_ACCESS_Y = builder.defineInRange("offsetY", 5, 0, Integer.MAX_VALUE);
            QUICK_ACCESS_MODULES = builder.comment("Modules that can be equipped/unequipped via Quick Access")
                .comment("Allowed Values: patch, pouch, armor_plate, gas_filter, cigarette_pack")
                .defineListAllowEmpty("modules", List.of(), o -> o instanceof String);
            QUICK_ACCESS_BLACKLIST = builder.defineListAllowEmpty(
                "itemsBlacklist",
                List.of("atelier:nvb", "atelier:nvbo"),
                o -> {
                    if (o instanceof String str) {
                        ResourceLocation key = ResourceLocation.tryParse(str);
                        if (key != null)
                            return ForgeRegistries.ITEMS.getValue(key) instanceof IQuickAccess;
                    }
                    return false;
                }
            );
            builder.pop();

            builder.push("bombvest");
            HALAL_MODE = builder.comment("Sing El Djihad before exploding").define("halalMode", false);
            builder.pop();

            SPEC = builder.build();
        }

        public enum Align {
            BOTTOM_RIGHT(-1, -1),
            BOTTOM_LEFT(1, -1),
            TOP_LEFT(1, 1),
            TOP_RIGHT(-1, 1);
            
            public final int x, y;
            
            Align(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }
    }
}
