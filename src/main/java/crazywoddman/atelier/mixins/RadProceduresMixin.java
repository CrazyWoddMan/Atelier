package crazywoddman.atelier.mixins;

import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import crazywoddman.atelier.items.accessories.Hazmat;
import net.mcreator.crustychunks.procedures.Rad10TickProcedure;
import net.mcreator.crustychunks.procedures.Rad1TickProcedure;
import net.mcreator.crustychunks.procedures.Rad5TickProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin({
    Rad1TickProcedure.class,
    Rad5TickProcedure.class,
    Rad10TickProcedure.class
})
public class RadProceduresMixin {

    @Redirect(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"
        ),
        remap = false
    )
    private static List<Entity> redirectEntitiesList(Stream<Entity> stream) {
        return stream.filter(e -> !(e instanceof Player player) || !Hazmat.isComplete(player)).toList();
    }
}
