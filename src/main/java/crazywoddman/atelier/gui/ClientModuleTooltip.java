package crazywoddman.atelier.gui;

import java.util.List;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.client.AtelierKeyMappings;
import crazywoddman.atelier.client.ClientUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class ClientModuleTooltip extends ClientContainerItemTooltip {
    public static int preview;
    private List<String> modules;

    public ClientModuleTooltip(ModuleTooltip tooltip) {
        super(tooltip.stack);
    }

    @Override
    protected ItemStack[] items(ItemStack stack) {
        this.modules = IModular.CACHE.get(stack.getItem());
        ItemStack[] items = new ItemStack[this.modules.size()];
        IModular.forEachEquipped(stack, (module, item) ->
            items[this.modules.indexOf(module.name) + module.index] =
                AtelierKeyMappings.MODULAR_PREVIEW.isDown()
                ? IContainerItem.get(item.getItem())
                    .map(c -> c.getContainerItems(item))
                    .filter(c -> c.length > 0)
                    .map(c -> c[c.length > preview ? preview : 0])
                    .orElse(item)
                : item
        );
        return items;
    }

    @Override
    protected void renderItem(GuiGraphics graphics, int index, int x, int y) {
        ItemStack stack = this.items[0];
        ClientUtils.renderItem(
            graphics,
            AtelierKeyMappings.MODULAR_PREVIEW.isDown()
                ? IContainerItem.get(stack.getItem())
                    .map(c -> c.getContainerItems(stack))
                    .filter(c -> c.length > 0)
                    .map(c -> c[c.length > preview ? preview : 0])
                    .orElse(stack)
                : stack,
            x, y
        );
        
    }

    @Override
    protected void renderSlotBG(GuiGraphics graphics, int i, int x, int y) {
        super.renderSlotBG(graphics, i, x, y);
        ItemStack stack = this.items[i];

        if (stack == null || stack.isEmpty()) {
            graphics.blit(
                ClientUtils.makeTexturePath(Atelier.MODID, "slot/" + this.modules.get(i)),
                x + 1, y + 1,
                0, 0,
                16, 16,
                16, 16
            );
        }
    }
}