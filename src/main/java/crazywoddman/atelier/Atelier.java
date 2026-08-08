package crazywoddman.atelier;

import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.blocks.AtelierBlockEntities;
import crazywoddman.atelier.blocks.AtelierBlocks;
import crazywoddman.atelier.data.AtelierData;
import crazywoddman.atelier.data.AtelierSounds;
import crazywoddman.atelier.effects.AtelierEffects;
import crazywoddman.atelier.gui.AtelierMenuTypes;
import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.network.NetworkHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

@Mod(Atelier.MODID)
public class Atelier {
    public static final String MODID = "atelier";
    public static final boolean
    CLOTH_CONFIG_LOADED,
    WARIUM_LOADED,
    JEI_LOADED,
    TERRABLENDER_LOADED,
    ACCESSORIES_LOADED,
    CURIOS_LOADED;

    static {
        ModList modlist = ModList.get();
        CLOTH_CONFIG_LOADED = modlist.isLoaded("cloth_config");
        WARIUM_LOADED = modlist.isLoaded("crusty_chunks");
        JEI_LOADED = modlist.isLoaded("jei");
        TERRABLENDER_LOADED = modlist.isLoaded("terrablender");
        ACCESSORIES_LOADED = modlist.isLoaded("accessories");
        CURIOS_LOADED = modlist.isLoaded("curios");
    }

    public Atelier(FMLJavaModLoadingContext context) throws InvalidVersionSpecificationException {
        if (!ACCESSORIES_LOADED && !CURIOS_LOADED) {
            throw new ModLoadingException(
                ModList.get().getModContainerById(MODID).get().getModInfo(),
                ModLoadingStage.CONSTRUCT,
                "fml.modloading.missingdependency",
                null,
                "accessories or curios",
                MODID,
                VersionRange.createFromVersionSpec("*"),
                new DefaultArtifactVersion("null")
            );
        }
        IEventBus bus = context.getModEventBus();
        NetworkHandler.register();
        AtelierItems.register(bus);
        AtelierBlocks.register(bus);
        AtelierBlockEntities.register(bus);
        AtelierMenuTypes.register(bus);
        AtelierData.register(bus);
        AtelierEffects.register(bus);
        AtelierSounds.register(bus);
        AtelierConfig.register(context);
        CompatHelper.registerEvents();
    }
}
