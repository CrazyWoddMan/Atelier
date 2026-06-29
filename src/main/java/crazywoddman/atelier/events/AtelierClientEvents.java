package crazywoddman.atelier.events;

import java.util.function.Supplier;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.api.interfaces.IWearable;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.client.ModulesRenderData;
import crazywoddman.atelier.client.renderers.AtelierRenderLayer;
import crazywoddman.atelier.compat.clothconfig.AtelierClothConfig;
import crazywoddman.atelier.gui.AtelierMenuTypes;
import crazywoddman.atelier.gui.ContainerItemTooltip;
import crazywoddman.atelier.gui.ClientContainerItemTooltip;
import crazywoddman.atelier.gui.ClientModuleTooltip;
import crazywoddman.atelier.gui.ModuleTooltip;
import crazywoddman.atelier.gui.SewingTableScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = Atelier.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AtelierClientEvents {
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(AtelierMenuTypes.SEWING_TABLE.get(), SewingTableScreen::new);

            ForgeRegistries.ITEMS.forEach(item -> {
                if (item instanceof IWearableAccessory wearable)
                    CompatHelper.registerRenderer(item, wearable.getRenderer());
            });
            
            if (Atelier.CLOTH_CONFIG_LOADED)
                AtelierClothConfig.registerConfigScreen();
        });
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        ModulesRenderData.register(event);
    }

    @SubscribeEvent
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ModuleTooltip.class, ClientModuleTooltip::new);
        event.register(ContainerItemTooltip.class, ClientContainerItemTooltip::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
            (stack, layer) -> {
                return layer < IDyeable.getDefaultColors(stack).length
                ? IDyeable.getColor(stack, layer)
                : 0xFFFFFF;
            },
            ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof IDyeable).toArray(Item[]::new)
        );
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(RegisterLayerDefinitions event) {
        ForgeRegistries.ITEMS.forEach(item -> {
            if (item instanceof IWearable wearable) {
                Supplier<LayerDefinition> layer = wearable.createLayer();
                ResourceLocation key = wearable.getLayerKey();

                if (layer != null && key != null)
                    event.registerLayerDefinition(new ModelLayerLocation(key, "main"), layer);
            }
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES) {
            try {
                var livingEntityRenderer = event.getRenderer((EntityType<LivingEntity>) entityType);

                if (livingEntityRenderer != null && livingEntityRenderer.getModel() instanceof HumanoidModel)
                    livingEntityRenderer.addLayer(new AtelierRenderLayer(livingEntityRenderer));

            } catch (ClassCastException ignore) {}
        }

        event.getSkins().forEach(model -> {
            var livingEntityRenderer = event.getSkin(model);

            if (livingEntityRenderer != null && livingEntityRenderer.getModel() instanceof HumanoidModel)
                livingEntityRenderer.addLayer(new AtelierRenderLayer(livingEntityRenderer));
        });
    }
}
