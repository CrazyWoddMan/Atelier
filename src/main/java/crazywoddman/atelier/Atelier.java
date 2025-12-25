package crazywoddman.atelier;

import crazywoddman.atelier.accessories.AccessoriesEvents;
import crazywoddman.atelier.accessories.PatchRenderer;
import crazywoddman.atelier.api.WearablesRegister;
import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.api.interfaces.IWearable;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.blocks.AtelierBlockEntities;
import crazywoddman.atelier.blocks.AtelierBlocks;
import crazywoddman.atelier.config.ClothConfig;
import crazywoddman.atelier.config.Config;
import crazywoddman.atelier.gui.AtelierMenuTypes;
import crazywoddman.atelier.gui.SewingTableScreen;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.recipes.AtelierRecipes;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.slot.SlotBasedPredicate;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(Atelier.MODID)
public class Atelier {
    public static final String MODID = "atelier";

    protected static final ModList modlist = ModList.get();
    public static final boolean cloth_config = modlist.isLoaded("cloth_config");
    public static final boolean accessories = modlist.isLoaded("accessories");
    public static final boolean curios = modlist.isLoaded("curios");
    public static final boolean warium = modlist.isLoaded("crusty_chunks");

    public Atelier(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();

        bus.addListener(this::commonSetup);
        
        AtelierItems.REGISTRY.register(bus);
        AtelierItems.WEARABLES.register(bus);
        AtelierItems.CREATIVE_TABS.register(bus);
        AtelierBlocks.REGISTRY.register(bus);
        AtelierBlockEntities.REGISTRY.register(bus);
        AtelierMenuTypes.REGISTRY.register(bus);
        AtelierRecipes.RECIPE_SERIALIZERS.register(bus);
        AtelierRecipes.RECIPE_TYPES.register(bus);
        AccessoriesEvents.register();
        MinecraftForge.EVENT_BUS.register(AccessoriesEvents.class);

        if (cloth_config)
            context.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AccessoriesAPI.registerPredicate(
                ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "armor_plate"),
                SlotBasedPredicate.ofItem(item -> 
                    AtelierRecipes.isPlate(item)
                )
            );
        });
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetupEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(AtelierMenuTypes.SEWING_TABLE.get(), SewingTableScreen::new);

                for (DyeColor color : DyeColor.values())
                    AccessoriesRendererRegistry.registerRenderer(
                        ForgeRegistries.ITEMS.getValue(
                            ResourceLocation.fromNamespaceAndPath("minecraft", color.getName() + "_banner")
                        ),
                        PatchRenderer::new
                    );
                for (RegistryObject<Item> object : WearablesRegister.ALL) {
                    Item item = object.get();
                    
                    if (item instanceof IWearableAccessory wearable)
                        AccessoriesRendererRegistry.registerRenderer(
                            item,
                            wearable.getRenderer()
                        );
                }
                
                if (modlist.isLoaded("cloth_config"))
                    ClothConfig.registerConfigScreen();
            });
        }

        @SubscribeEvent
        public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
            if (event.getTab() == AtelierItems.CREATIVE_TAB.get())
                for (RegistryObject<Item> item : AtelierItems.REGISTRY.getEntries())
                    event.accept(item.get());
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
            for (RegistryObject<Item> object : WearablesRegister.ALL) {
                if (object.get() instanceof IWearable wearable)
                    event.registerLayerDefinition(wearable.getLayerLocation(), wearable.createLayer());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onServerStarted(ServerStartedEvent event) {
            AtelierRecipes.reload(event.getServer().getRecipeManager());
        }

        @SubscribeEvent
        public static void onCommandRegister(net.minecraftforge.event.RegisterCommandsEvent event) {
            AtelierCommands.register(event.getDispatcher());
        }
    }
}
