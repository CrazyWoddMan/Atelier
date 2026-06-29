package crazywoddman.atelier.data;

import crazywoddman.atelier.Atelier;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ServerUtils {
    public static void grantAdvancement(Player player, String name) {
        if (player instanceof ServerPlayer serverPlayer) {
            Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(Atelier.MODID, name));

            if (advancement != null)
                serverPlayer.getAdvancements().award(advancement, "by_code");
        }
    } 
}