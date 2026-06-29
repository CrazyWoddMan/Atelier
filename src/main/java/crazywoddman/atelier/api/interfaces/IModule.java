package crazywoddman.atelier.api.interfaces;

import crazywoddman.atelier.api.render.SimpleModuleRenderer;

public interface IModule extends IWearable {

    default IModuleRenderer getRenderer() {
        return new SimpleModuleRenderer(getTexture(), getLayerKey(), true);
    }
}