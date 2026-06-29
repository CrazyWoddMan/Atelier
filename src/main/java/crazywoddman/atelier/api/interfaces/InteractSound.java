package crazywoddman.atelier.api.interfaces;

import crazywoddman.atelier.api.EquipSound;

public interface InteractSound {

    default EquipSound interactSound() {
        return EquipSound.BUNDLE;
    }
}
