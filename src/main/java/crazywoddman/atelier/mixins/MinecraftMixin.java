package crazywoddman.atelier.mixins;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crazywoddman.atelier.client.AtelierKeyMappings;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    
    @Redirect(
        method = "handleKeybinds",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z",
            ordinal = 2
        )
    )
    private boolean redirectHotbarSlotSwitch(KeyMapping key) {
        return key.consumeClick() && !AtelierKeyMappings.QUICK_ACCESS.isDown();
    }
}