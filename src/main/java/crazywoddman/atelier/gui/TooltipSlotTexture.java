package crazywoddman.atelier.gui;

import crazywoddman.atelier.client.ClientUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public enum TooltipSlotTexture {
    SLOT(0, 0, 18, 18),
    SLOT_WITH_BOTTOM(0, 0, 18, 20),
    BORDER_VERTICAL(0, 18, 1, 20),
    BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
    BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
    BORDER_CORNER_TOP(0, 20, 1, 1),
    BORDER_CORNER_BOTTOM(0, 60, 1, 1);

    public final int x;
    public final int y;
    public final int w;
    public final int h;

    private TooltipSlotTexture(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    private static final ResourceLocation TEXTURE = ClientUtils.makeTexturePath("minecraft", "gui/container/bundle");

    public static void blit(GuiGraphics graphics, int x, int y, TooltipSlotTexture texture) {
        graphics.blit(TEXTURE, x, y, 0, texture.x, texture.y, texture.w, texture.h, 128, 128);
    }
}