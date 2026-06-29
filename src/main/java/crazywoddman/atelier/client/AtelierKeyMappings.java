package crazywoddman.atelier.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import crazywoddman.atelier.Atelier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Atelier.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AtelierKeyMappings {
    
    public static final KeyMapping MODULAR_PREVIEW = new AtelierMapping("preview", GLFW.GLFW_KEY_LEFT_CONTROL) {
        @Override
        public boolean isDown() {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), getKey().getValue());
        }
    };
    public static final KeyMapping QUICK_ACCESS = new AtelierMapping("quickaccess", GLFW.GLFW_KEY_GRAVE_ACCENT);
    public static final KeyMapping NVD_TOGGLE = new AtelierMapping("nvd.toggle", GLFW.GLFW_KEY_N);

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(MODULAR_PREVIEW);
        event.register(QUICK_ACCESS);
        event.register(NVD_TOGGLE);
    }

    private static class AtelierMapping extends KeyMapping {
        AtelierMapping(String name, int defaultKey) {
            super(
                "key." + Atelier.MODID + "." + name,
                InputConstants.Type.KEYSYM,
                defaultKey,
                "key.categories." + Atelier.MODID
            );
        }
    }
}
