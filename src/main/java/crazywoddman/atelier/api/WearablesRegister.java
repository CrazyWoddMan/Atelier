package crazywoddman.atelier.api;

import java.util.function.Supplier;

import crazywoddman.atelier.api.interfaces.IDyable;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class WearablesRegister {
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
                for (RegistryObject<Item> item : WearablesRegisty.ALL_ITEMS) {
                    if (item.get() instanceof IDyable)
                        WearablesRegisty.DYABLE_ITEMS.add(item);
                }
        });
    }

    /**
    * Registers item in Forge DeferredRegister and Atelier wearables registry
    */
    public RegistryObject<Item> register(String name, Supplier<? extends Item> supplier) {
        RegistryObject<Item> item = REGISTRY.register(name, supplier);
        WearablesRegisty.ALL_ITEMS.add(item);

        return item;
    }
}