package crazywoddman.atelier.gui;

import crazywoddman.atelier.Atelier;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AtelierMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, Atelier.MODID);

    public static final RegistryObject<MenuType<SewingTableMenu>> SEWING_TABLE =
        REGISTRY.register("sewing_table", () ->
            IForgeMenuType.create((windowId, inventory, buffer) ->
                new SewingTableMenu(windowId, inventory, buffer.readBlockPos())
            )
        );
}