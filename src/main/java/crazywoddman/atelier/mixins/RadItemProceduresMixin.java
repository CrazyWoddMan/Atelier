package crazywoddman.atelier.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import crazywoddman.atelier.items.accessories.Hazmat;
import net.mcreator.crustychunks.procedures.Rad01ItemProcedure;
import net.mcreator.crustychunks.procedures.Rad05TickItemProcedure;
import net.mcreator.crustychunks.procedures.Rad10TickItemProcedure;
import net.mcreator.crustychunks.procedures.Rad1TickItemProcedure;
import net.mcreator.crustychunks.procedures.Rad5TickItemProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin({
    Rad01ItemProcedure.class,
    Rad05TickItemProcedure.class,
    Rad1TickItemProcedure.class,
    Rad5TickItemProcedure.class,
    Rad10TickItemProcedure.class
})
public class RadItemProceduresMixin {

    @Inject(
        method = "execute",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void cancelProcedure(Entity entity, ItemStack stack, CallbackInfo ci) {
        if (entity instanceof Player player && Hazmat.isComplete(player))
            ci.cancel();
    }
}
