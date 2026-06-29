package crazywoddman.atelier;

import crazywoddman.atelier.client.ClientUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;

public class DebugUtil {
    
    public static void sendToChat(Object message, Object... args) {
        sendToChat(Component.literal(message.toString().formatted(args)));
    }
    
    public static void sendToChat(Component message) {
        if (EffectiveSide.get() == LogicalSide.CLIENT) {
            Player player = ClientUtils.getLocalPlayer();

            if (player != null)
                player.displayClientMessage(message, false);
        } else {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

            if (server != null)
                server.getPlayerList().broadcastSystemMessage(message, false);
        }
    }
}