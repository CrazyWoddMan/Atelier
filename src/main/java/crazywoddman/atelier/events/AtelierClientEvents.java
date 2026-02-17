package crazywoddman.atelier.events;

import crazywoddman.atelier.renderers.ConfigurableModuleRenderer;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.WearablesRegister;
import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.api.interfaces.IWearable;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.config.ClothConfig;
import crazywoddman.atelier.gui.AtelierMenuTypes;
import crazywoddman.atelier.gui.SewingTableScreen;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = Atelier.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AtelierClientEvents {
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(AtelierMenuTypes.SEWING_TABLE.get(), SewingTableScreen::new);

            for (DyeColor color : DyeColor.values())
                AccessoriesRendererRegistry.registerRenderer(
                    ForgeRegistries.ITEMS.getValue(
                        ResourceLocation.fromNamespaceAndPath("minecraft", color.getName() + "_banner")
                    ),
                    ConfigurableModuleRenderer::new
                );
            for (RegistryObject<Item> object : WearablesRegister.ALL) {
                Item item = object.get();
                
                if (item instanceof IWearableAccessory wearable) {
                    Supplier<AccessoryRenderer> renderer = wearable.getRenderer();

                    if (renderer != null)
                        AccessoriesRendererRegistry.registerRenderer(
                            item,
                            renderer
                        );
                    else
                        AccessoriesRendererRegistry.registerNoRenderer(item);
                }
            }
            
            if (Atelier.cloth_config)
                ClothConfig.registerConfigScreen();
        });
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for (RegistryObject<Item> item : WearablesRegister.DYABLE)
            event.register(
                (stack, layer) ->
                    layer == 0
                    ? ((IDyable)stack.getItem()).getColor(stack)
                    : 16777215,
                item.get()
            );
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(RegisterLayerDefinitions event) {
        for (RegistryObject<Item> object : WearablesRegister.ALL)
            if (object.get() instanceof IWearable wearable) {
                Supplier<LayerDefinition> layer = wearable.createLayer();

                if (layer != null)
                    event.registerLayerDefinition(new ModelLayerLocation(wearable.getModelKey(), "main"), layer);
            }
    }
}
