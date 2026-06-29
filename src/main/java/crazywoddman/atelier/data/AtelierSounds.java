package crazywoddman.atelier.data;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
    public static final RegistryObject<SoundEvent> NVD_ON = register("nvd_on");
    public static final RegistryObject<SoundEvent> NVD_OFF = register("nvd_off");

    public static void play(SoundEvent sound, Entity source, @Nullable Player localPlayer) {
        source.level().playSound(localPlayer,
            source.getX(), source.getY(), source.getZ(),
            sound, source.getSoundSource(),
            1, 1
        );
    }

    public static void play(Supplier<SoundEvent> sound, Entity source, @Nullable Player localPlayer) {
        play(sound.get(), source, localPlayer);
    }

    private static RegistryObject<SoundEvent> register(String path) {
        return REGISTRY.register(path, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Atelier.MODID, path)));
    }
}
