package crazywoddman.atelier.api.templates;

import net.minecraft.world.item.Item;

public class UnstackableItem extends Item {
    public UnstackableItem() {
        super(new Properties().stacksTo(1));
    }
}