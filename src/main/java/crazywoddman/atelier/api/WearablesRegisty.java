package crazywoddman.atelier.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class WearablesRegisty {
    protected static final List<RegistryObject<Item>> ALL_ITEMS = new ArrayList<>();
    protected static final List<RegistryObject<Item>> DYABLE_ITEMS = new ArrayList<>();
    protected static final List<RegistryObject<Item>> MODULAR_ITEMS = new ArrayList<>();

    public static List<RegistryObject<Item>> getWearables() {
        return ALL_ITEMS;
    }

    public static List<RegistryObject<Item>> getDyable() {
        return DYABLE_ITEMS;
    }

    public static List<RegistryObject<Item>> getModular() {
        return MODULAR_ITEMS;
    }
}