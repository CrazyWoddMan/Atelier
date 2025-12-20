package crazywoddman.atelier.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static final Server SERVER = new Server(builder);
    public static final ForgeConfigSpec SERVER_SPEC = builder.build();

    public static class Server {
        public Server(ForgeConfigSpec.Builder builder) {}
    }
}
