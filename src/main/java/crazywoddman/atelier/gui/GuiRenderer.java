package crazywoddman.atelier.gui;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.AtelierConfig.Client.Align;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.client.AtelierKeyMappings;
import crazywoddman.atelier.client.ClientUtils;
import crazywoddman.atelier.client.LocalPlayerVars;
import crazywoddman.atelier.data.QuickAccessSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public class GuiRenderer {
    private static final ResourceLocation GOGGLES_OVERLAY = ClientUtils.makeTexturePath(Atelier.MODID, "gui/goggles_overlay");

    public static void render(GuiGraphics graphics, Minecraft mc) {
        boolean isFPP = mc.options.getCameraType().isFirstPerson();
        boolean renderFPO = isFPP && AtelierConfig.Client.FPP_OVERLAYS.get();

        if (LocalPlayerVars.wearables.nvdActive && (isFPP || AtelierConfig.Client.THIRD_PERSON_NVD.get())) {
            NvdClientHandler.render(graphics, renderFPO && CompatHelper.shouldRender(mc.player, SimpleSlot.of(IWearableAccessory.HAT, 0)));
        } else if (LocalPlayerVars.tintedVision && renderFPO && CompatHelper.shouldRender(mc.player, SimpleSlot.of(IWearableAccessory.FACE, LocalPlayerVars.faceItemSlot))) {
            ClientUtils.renderOverlay(graphics, GOGGLES_OVERLAY);
        }

        if (AtelierConfig.Client.QUICK_ACCESS_GUI.get() && mc.screen == null && !mc.options.hideGui && !QuickAccessSlot.CACHE.isEmpty())
           renderQuickAccess(graphics, mc);
    }

    private static void renderQuickAccess(GuiGraphics graphics, Minecraft mc) {
        Align align = AtelierConfig.Client.QUICK_ACCESS_ALIGN.get();
        int borderX = switch (align) {
            case BOTTOM_LEFT, TOP_LEFT -> 0;
            case BOTTOM_RIGHT, TOP_RIGHT -> graphics.guiWidth();
        } + AtelierConfig.Client.QUICK_ACCESS_X.get() * align.x;
        int borderY = switch (align) {
            case TOP_LEFT, TOP_RIGHT -> 0;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> graphics.guiHeight();
        } + AtelierConfig.Client.QUICK_ACCESS_Y.get() * align.y;
        int i = 0;
        
        for (QuickAccessSlot slot : QuickAccessSlot.CACHE) {
            int y = borderY + align.y * (align.y == 1 ? i : (QuickAccessSlot.CACHE.size() - i)) * 16;
            i++;

            if (AtelierConfig.Client.QUICK_ACCESS_KEYBINDS.get()) {
                Component text = Component.translatable(AtelierKeyMappings.QUICK_ACCESS.getKey().getName()).append(" + " + i);
                graphics.drawString(
                    mc.font,
                    text,
                    align.x == 1 ? borderX + 18 : (borderX - 18 - mc.font.width(text)),
                    y + 4,
                    0xFFFFFF,
                    true
                );
            }

            int x = align.x == 1 ? borderX : (borderX - 16);

            slot.path()[0].getAccess(mc.player).map(SlotAccess::get).ifPresent(stack -> {
                if (slot.path().length > 1) {
                    ItemStack module = IModular.getStackInSlot(stack, slot.path()[1]);

                    if (!module.isEmpty()) {
                        if (renderPreview(graphics, module, x, y))
                            return;
                        
                        stack = module;
                    } else {
                        TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "slot/" + slot.path()[1].name));
                        
                        if (sprite != null) {
                            graphics.blit(x, y, 0, 16, 16, sprite);
                            return;
                        }
                    }
                } else if (renderPreview(graphics, stack, x, y))
                    return;

                ClientUtils.renderItem(graphics, stack, x, y);
            });
        }
    }

    private static boolean renderPreview(GuiGraphics graphics, ItemStack stack, int x, int y) {
        if (stack.getItem() instanceof IQuickAccess access) {
            ItemStack preview = access.quickAccessPreview(stack);

            if (!preview.isEmpty()) {
                ClientUtils.renderItem(graphics, preview, x, y);
                return true;
            }
        }

        return false;
    }
}
