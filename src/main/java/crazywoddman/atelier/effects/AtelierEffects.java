package crazywoddman.atelier.effects;

import crazywoddman.atelier.Atelier;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AtelierEffects {
    public static void register(IEventBus bus) {
        MOB_EFFECTS.register(bus);
    }

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = 
        DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Atelier.MODID);

    public static final RegistryObject<MobEffect> FILTER_PROTECTED = MOB_EFFECTS.register(
        "filter_protected",
        FilterEffect::new
    );
}