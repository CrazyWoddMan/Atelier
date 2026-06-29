package crazywoddman.atelier.compat.oculus;

import net.minecraftforge.fml.ModList;

public class OculusHelper {
    private static final boolean OCULUS_LOADED = ModList.get().isLoaded("oculus");

    public static boolean shadersActive() {
        return OCULUS_LOADED && OculusHandler.shadersActive();
    }
}