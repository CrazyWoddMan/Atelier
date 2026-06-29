package crazywoddman.atelier.compat.oculus;

import net.irisshaders.iris.api.v0.IrisApi;

class OculusHandler {
    static boolean shadersActive() {
        return IrisApi.getInstance().isShaderPackInUse();
    }
}