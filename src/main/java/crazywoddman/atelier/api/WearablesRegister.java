package crazywoddman.atelier.api;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IDyable;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class WearablesRegister {
    public static final Set<RegistryObject<Item>> ALL = new HashSet<>();
    public static final Set<RegistryObject<Item>> DYABLE = new HashSet<>();
    public static final Set<RegistryObject<Item>> MODULAR = new HashSet<>();

    private final DeferredRegister<Item> REGISTRY;

    /**
    * Should be used to register Atelier wearable items
    */
    public WearablesRegister(DeferredRegister<Item> registry) {
        REGISTRY = registry;
    }

    /**
    * Should be called during the mod initialization alongside DeferredRegister registration
    */
    public void register(IEventBus bus) {
        bus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS))
                for (RegistryObject<Item> item : ALL)
                    if (item.get() instanceof IDyable)
                        DYABLE.add(item);
        });
    }

    /**
    * Registers item in Forge DeferredRegister and Atelier wearables registry
    */
    public RegistryObject<Item> register(String name, Supplier<Item> supplier) {
        RegistryObject<Item> item = REGISTRY.register(name, supplier);
        ALL.add(item);

        return item;
    }
}