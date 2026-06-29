package crazywoddman.atelier.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crazywoddman.atelier.client.LocalPlayerVars;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {

    @Redirect(
        method = "updateLightTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"
        )
    )
    private void atelier$upload(DynamicTexture lightTexture) {
        LocalPlayerVars.lightTextureHook = lightTexture;
        lightTexture.upload();
    }
}
