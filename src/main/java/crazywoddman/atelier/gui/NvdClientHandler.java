package crazywoddman.atelier.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.client.ClientUtils;
import crazywoddman.atelier.compat.oculus.OculusHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class NvdClientHandler {
    private static final DynamicTexture NOISE = new DynamicTexture(256, 256, false);

    private static final ResourceLocation
    NVD_OVERLAY = ClientUtils.makeTexturePath(Atelier.MODID, "gui/nvd_overlay"),
    WHITE_OVERLAY = ClientUtils.makeTexturePath(Atelier.MODID, "gui/white_overlay"),
    NOISE_KEY = Minecraft.getInstance().getTextureManager().register("nvd_noise", NOISE);

    public enum Mode {
        SIMPLE, NOISE, NOISE_AND_OVEREXPOSURE;
    }

    public static void render(GuiGraphics graphics, boolean renderFPO) {
        RenderSystem.enableBlend();
        Mode mode = AtelierConfig.Client.NVD_MODE.get();

        if (mode != Mode.SIMPLE) {
            if (mode == Mode.NOISE_AND_OVEREXPOSURE) {
                RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.DST_COLOR,
                    GlStateManager.DestFactor.ONE
                );
                ClientUtils.renderOverlay(graphics, WHITE_OVERLAY);

                if (!OculusHelper.shadersActive())
                    ClientUtils.renderOverlay(graphics, WHITE_OVERLAY);
            }

            RenderSystem.defaultBlendFunc();
            NativeImage image = NOISE.getPixels();
            RandomSource random = Minecraft.getInstance().level.random;
            
            for (int px = 0; px < 128; px++) {
                for (int py = 0; py < 128; py++) {
                    int noise = random.nextInt(40);
                    int color = ((80 + random.nextInt(40)) << 24) | (noise << 16) | (noise << 8) | noise;
                    
                    for (int dx = 0; dx < 2; dx++) {
                        for (int dy = 0; dy < 2; dy++) {
                            int x = px * 2 + dx;
                            int y = py * 2 + dy;

                            if (x < 256 && y < 256)
                                image.setPixelRGBA(x, y, color);
                        }
                    }
                }
            }

            NOISE.upload();
            ClientUtils.renderOverlay(graphics, NOISE_KEY);
        }

        if (renderFPO)
            ClientUtils.renderOverlay(graphics, NVD_OVERLAY);

        RenderSystem.disableBlend();
    }
}