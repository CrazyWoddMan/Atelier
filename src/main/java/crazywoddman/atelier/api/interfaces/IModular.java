package crazywoddman.atelier.api.interfaces;

import java.util.Map;

import crazywoddman.atelier.AtelierTags;
import crazywoddman.atelier.recipes.AtelierRecipes;
import net.minecraft.world.item.Item;

public interface IModular {
    static boolean isModular(Item item) {
        return item instanceof IModular || AtelierTags.Items.get(AtelierTags.Items.PLATE_CARRIERS).contains(item) || AtelierRecipes.hasConfigurableModules(item);
    }

    Map<String, Integer> getModules();

    static Map<String, Integer> getModules(Item item, String parent, byte index) {
        Map<String, Integer> modules = AtelierRecipes.getConfigurableModules(item, parent, index);

        if (item instanceof IModular modular)
            modules.putAll(modular.getModules());

        if (AtelierTags.Items.get(AtelierTags.Items.PLATE_CARRIERS).contains(item))
            modules.put("armor_plate", 1);

        return modules;
    }

    /**
    * Only for ArmorItem instances! Use {@link #getModules(Item, String, byte)} for accessories
    */
    static Map<String, Integer> getModules(Item item) {
        return getModules(item, "", (byte)-1);
    }
}