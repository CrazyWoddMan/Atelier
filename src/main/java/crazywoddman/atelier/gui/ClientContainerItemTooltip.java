package crazywoddman.atelier.gui;

import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ClientContainerItemTooltip implements ClientTooltipComponent {
    public static final ResourceLocation SLOT_TEXTURE = ClientUtils.makeTexturePath("minecraft", "gui/container/bundle");
    public static Slot lastChosen;
    public static int chosen;
    protected final int rows, cols, width, height;
    protected final ItemStack[] items;

    public static void setChosen(int value) {
        chosen = value;
        ClientModuleTooltip.preview = 0;
    }

    public ClientContainerItemTooltip(ItemStack stack) {
        this.items = items(stack);
        this.rows = rows();
        this.cols = cols();
        this.width = width();
        this.height = height();
    }

    public ClientContainerItemTooltip(ContainerItemTooltip tooltip) {
        this(tooltip.stack);
    }

    @Override
    public int getWidth(Font font) {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    protected int rows() {
        return this.items.length > 5 ? (int)Math.ceil(this.items.length / 5D) : 1;
    }

    protected int cols() {
        return this.items.length > 5 ? (int) Math.ceil(this.items.length / (double)this.rows) : this.items.length;
    }

    protected int width() {
        return this.cols * 18;
    }

    protected int height() {
        return this.rows * 18 + 4;
    }

    protected ItemStack[] items(ItemStack stack) {
        ItemStack[] contents = IContainerItem.getItems(stack);
        int entry = IContainerItem.getCapacity(stack) > contents.length
            && Minecraft.getInstance().screen instanceof AbstractContainerScreen screen
            && !screen.getMenu().getCarried().isEmpty()
            ? 1 : 0;
        ItemStack[] items = new ItemStack[Math.max(1, contents.length + entry)];

        for (int i = 0; i < contents.length; i++)
            items[i + entry] = contents[i];

        return items;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int i = 0;

        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.cols; c++) {
                int posX = x + c * 18;

                if (r == this.rows - 1)
                    posX += (this.rows * this.cols - this.items.length) * 9;

                int posY = y + r * 18;
                renderSlot(graphics, i, posX, posY, i == chosen);

                if (++i >= this.items.length)
                    break;
            }
        }
    }

    protected void renderSlot(GuiGraphics graphics, int index, int x, int y, boolean highlight) {
        renderSlotBG(graphics, index, x, y);

        if (items.length > index) {
            ItemStack stack = items[index];

            if (stack != null && !stack.isEmpty())
                ClientUtils.renderItem(graphics, items[index], x + 1, y + 1);
        }

        if (highlight)
            AbstractContainerScreen.renderSlotHighlight(graphics, x + 1, y + 1, 0);
    }

    protected void renderItem(GuiGraphics graphics, int index, int x, int y) {
        ClientUtils.renderItem(graphics, items[index], x, y);
    }

    protected void renderSlotBG(GuiGraphics graphics, int index, int x, int y) {
        graphics.blit(SLOT_TEXTURE, x, y, 0, 0, 0, 18, 18, 128, 128);
    } 
}