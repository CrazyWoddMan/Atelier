package crazywoddman.atelier.config;

import crazywoddman.atelier.items.simple.Detonator;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Config {
    public static void register(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
    }
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Server SERVER = new Server(BUILDER);
    private static final ForgeConfigSpec SERVER_SPEC = BUILDER.build();

    public static class Server {
        public final ForgeConfigSpec.DoubleValue kneePadsProtection;
        public final ForgeConfigSpec.EnumValue<Detonator.ExplosionType> bombVest;
        public final ForgeConfigSpec.IntValue bombVestMaxExplosionPower;

        public Server(ForgeConfigSpec.Builder builder) {
            this.kneePadsProtection = builder
                .comment("Percent of fall damage that Knee Pads block (1 = 100%)")
                .defineInRange("kneePadsProtection", 0.3, 0, 1);
            this.bombVest = builder
                .comment(
                    Detonator.ExplosionType.DISABLE.name() + ": Pouches can't detonate",
                    Detonator.ExplosionType.NO_DESTRUCTION.name() + ": explosion doesn't break blocks",
                    Detonator.ExplosionType.ENABLE.name() + ": default explosion"
                ).defineEnum("bombVest", Detonator.ExplosionType.ENABLE);
            this.bombVestMaxExplosionPower = builder
                .comment("After what amount of explosive items in Pouches detonation won't get any more powerful")
                .defineInRange("bombVestMaxExplosionPower", 32, 1, Integer.MAX_VALUE);
        }
    }

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final Client CLIENT = new Client(CLIENT_BUILDER);
    private static final ForgeConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    public static class Client {
        public final ForgeConfigSpec.BooleanValue halalMode;

        public Client(ForgeConfigSpec.Builder builder) {
            this.halalMode = builder.comment("Halal mode").define("halalMode", false);
        }
    }
}
