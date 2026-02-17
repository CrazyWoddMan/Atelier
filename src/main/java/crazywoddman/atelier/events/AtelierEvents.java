package crazywoddman.atelier.events;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.ModulesDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Atelier.MODID)
public class AtelierEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity entity && !entity.getCapability(ModulesDataProvider.MODULES_DATA).isPresent())
            event.addCapability(
                ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "modules_data"),
                new ModulesDataProvider(entity)
            );
    }
}