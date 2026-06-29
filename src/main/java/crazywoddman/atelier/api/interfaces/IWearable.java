package crazywoddman.atelier.api.interfaces;

import java.util.function.Supplier;

import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.client.WearableTexture;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public interface IWearable {
    Supplier<LayerDefinition> createLayer();

    default WearableTexture getTexture() {
        return new WearableTexture((Item)this);
    }

    default ResourceLocation getLayerKey() {
        return ForgeRegistries.ITEMS.getKey((Item)this);
    }

    default EquipSound equipSound() {
        return EquipSound.LEATHER;
    }
}