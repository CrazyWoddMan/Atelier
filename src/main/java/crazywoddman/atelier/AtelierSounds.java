package crazywoddman.atelier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AtelierSounds {
    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }

    public final static DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Atelier.MODID);

    public static final RegistryObject<SoundEvent> DETONATION = register("detonation");
    public static final RegistryObject<SoundEvent> HALAL = register("halal");
    public static final RegistryObject<SoundEvent> GASMASK = register("gasmask");
    public static final RegistryObject<SoundEvent> GOTTA_MOVE = register("gotta_move");
    public static final RegistryObject<SoundEvent> SEWING_MACHINE = register("sewing_machine");

    private static RegistryObject<SoundEvent> register(String path) {
        return REGISTRY.register(path, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Atelier.MODID, path)));
    }
}
