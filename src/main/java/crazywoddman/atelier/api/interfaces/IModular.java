package crazywoddman.atelier.api.interfaces;

import java.util.Map;

import crazywoddman.atelier.items.AtelierItems;
import crazywoddman.atelier.recipes.AtelierRecipes;
import net.minecraft.world.item.Item;

public interface IModular {
    static boolean isModular(Item item) {
        return item instanceof IModular || AtelierItems.Tags.get(AtelierItems.Tags.PLATE_CARRIERS).contains(null) || AtelierRecipes.isPatch(item);
    }
    /**
     * @return a map of module IDs and slots amount for this item. <b>MUST NOT INCLUDE PLATE MODULE!</b>
     */
    Map<String, Integer> getModules();
}