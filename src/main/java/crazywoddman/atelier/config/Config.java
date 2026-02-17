package crazywoddman.atelier.config;

import crazywoddman.atelier.items.simple.Detonator;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static final Server SERVER = new Server(builder);
    public static final ForgeConfigSpec SERVER_SPEC = builder.build();

    public static class Server {
        public final ForgeConfigSpec.DoubleValue kneePadsProtection;
        public final ForgeConfigSpec.EnumValue<Detonator.ExplosionType> bombVest;
        public final ForgeConfigSpec.IntValue bombVestMaxExplosionPower;
        public final ForgeConfigSpec.BooleanValue halalMode;

        public Server(ForgeConfigSpec.Builder builder) {
            kneePadsProtection = builder
                .comment("Percent of fall damage that Knee Pads block (1 = 100%)")
                .defineInRange("kneePadsProtection", 0.3, 0, 1);
            bombVest = builder
                .comment(
                    Detonator.ExplosionType.DISABLE.name() + ": Pouches can't detonate",
                    Detonator.ExplosionType.NO_DESTRUCTION.name() + ": explosion doesn't break blocks",
                    Detonator.ExplosionType.ENABLE.name() + ": default explosion"
                ).defineEnum("bombVest", Detonator.ExplosionType.ENABLE);
            bombVestMaxExplosionPower = builder
                .comment("After what amount of explosive items in Pouches detonation won't get any more powerful")
                .defineInRange("bombVestMaxExplosionPower", 32, 1, Integer.MAX_VALUE);
            halalMode = builder
                .comment("Halal mode")
                .define("halalMode", false);
        }
    }
}
