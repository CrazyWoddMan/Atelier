package crazywoddman.atelier.mixins;

import com.mojang.blaze3d.platform.NativeImage;

import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.client.LocalPlayerVars;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;

@Mixin(DynamicTexture.class)
public class DynamicTextureMixin {
    private static final int MIN_BRIGHTNESS = 30;

    @Redirect(
        method = "upload",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/NativeImage;upload(IIIZ)V"
        )
    )
    private void atelier$upload(NativeImage lightPixels, int level, int xOffset, int yOffset, boolean blur) {
        if ((DynamicTexture)(Object)this == LocalPlayerVars.lightTextureHook
        && LocalPlayerVars.wearables != null
        && LocalPlayerVars.wearables.nvdActive
        && (AtelierConfig.Client.THIRD_PERSON_NVD.get() || Minecraft.getInstance().options.getCameraType().isFirstPerson())
        ) {
            float multiplier = switch (AtelierConfig.Client.NVD_MODE.get()) {
                case SIMPLE -> 1.5F;
                case NOISE -> 2;
                case NOISE_AND_OVEREXPOSURE -> 1.1F;
            };
            lightPixels.applyToAllPixels(pixel ->
                (pixel & 0xFF000000)
                | (Mth.clamp((int)((pixel & 0xFF) * multiplier), MIN_BRIGHTNESS, 255) << 16)
                | (Mth.clamp((int)(((pixel >> 8) & 0xFF) * multiplier), MIN_BRIGHTNESS, 255) << 8)
                | Mth.clamp((int)((pixel & 0xFF) * multiplier), MIN_BRIGHTNESS, 255)
            );
        }

        LocalPlayerVars.lightTextureHook = null;
        lightPixels.upload(level, xOffset, yOffset, blur);
    }
}