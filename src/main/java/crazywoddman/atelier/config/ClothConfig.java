package crazywoddman.atelier.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

public class ClothConfig {
    @SuppressWarnings("removal")
    public static void registerConfigScreen() {
        FMLJavaModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((mc, screen) -> {
            ConfigBuilder builder = ConfigBuilder
                .create()
                .setParentScreen(screen)
                .setTitle(Component.literal("Warium Additions Config"));
            builder.setGlobalized(false);
            builder.setTransparentBackground(true);
            ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
            String levelType = Minecraft.getInstance().level == null ? "null" : (Minecraft.getInstance().getSingleplayerServer() == null ? "client" : "host");
            entryBuilder.startTextDescription(
                Component.literal("§eChanges " + switch (levelType) {
                    case "null" -> "can't be made from the main menu. Enter a world";
                    case "host" -> "only apply for the current world. Re-enter the world for some changes to take effect";
                    case "client" -> "on server can only be made by editing config file in world/serverconfig/. Alternatively, you can use Create «Access Configs of other mods» feature. Server restart is required for some changes to apply";
                    default -> "can't be made due to unknown error";
                })
            ).build();

            return builder.build();
        }));
    }
}
