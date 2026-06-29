package crazywoddman.atelier.client;

import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.data.QuickAccessSlot;
import crazywoddman.atelier.data.WearablesCapability.WearableState;
import crazywoddman.atelier.gui.ClientContainerItemTooltip;
import crazywoddman.atelier.gui.ClientModuleTooltip;
import crazywoddman.atelier.items.templates.NightVisionDevice;
import crazywoddman.atelier.network.packets.QuickAccessPacket;
import crazywoddman.atelier.network.packets.WearCapToServerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;

public class InputHandler {

    public static void mouseScrollScreen(ScreenEvent.MouseScrolled.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen))
            return;

        Slot hovered = screen.getSlotUnderMouse();
        
        if (hovered == null)
            return;

        ItemStack stack = hovered.getItem();

        if (stack.isEmpty())
            return;

        Item item = stack.getItem();
        IContainerItem.get(item).ifPresent(container -> {
            int slots = container.getVisibleSlots(stack, screen.getMenu().getCarried());
            int chosen = ClientContainerItemTooltip.chosen;

            if (slots <= chosen)
                return;

            event.setCanceled(true);

            if (AtelierKeyMappings.MODULAR_PREVIEW.isDown() && IModular.isModular(item)) {
                ItemStack previewStack = container.getContainerItems(stack)[chosen];

                if (IContainerItem.get(previewStack.getItem()).map(p -> {
                    int visible = p.getVisibleSlots(previewStack, ItemStack.EMPTY);

                    if (visible < 2)
                        return false;

                    int preview = ClientModuleTooltip.preview;

                    if (event.getScrollDelta() > 0) {
                        if (++preview >= visible)
                            preview -= visible;
                    } else if (--preview < 0)
                        preview += visible;

                    ClientModuleTooltip.preview = preview;
                    return true;
                }).orElse(false))
                    return;
            }

            if (event.getScrollDelta() > 0) {
                if (++chosen >= slots)
                    chosen -= slots;
            } else if (--chosen < 0)
                chosen += slots;

            ClientContainerItemTooltip.setChosen(chosen);
        });
    }

    public static void keyInputScreen(ScreenEvent.KeyPressed.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen))
            return;

        Slot hovered = screen.getSlotUnderMouse();
        
        if (hovered == null)
            return;

        ItemStack stack = hovered.getItem();

        if (stack.isEmpty())
            return;

        IContainerItem.get(stack.getItem()).ifPresent(container -> {
            int slots = container.getVisibleSlots(stack, screen.getMenu().getCarried());

            if (slots == 0)
                return;

            byte key;

            switch (event.getKeyCode()) {
                case GLFW.GLFW_KEY_1 -> key = 0;
                case GLFW.GLFW_KEY_2 -> key = 1;
                case GLFW.GLFW_KEY_3 -> key = 2;
                case GLFW.GLFW_KEY_4 -> key = 3;
                case GLFW.GLFW_KEY_5 -> key = 4;
                case GLFW.GLFW_KEY_6 -> key = 5;
                case GLFW.GLFW_KEY_7 -> key = 6;
                case GLFW.GLFW_KEY_8 -> key = 7;
                case GLFW.GLFW_KEY_9 -> key = 8;
                default -> {return;}
            }

            if (key < slots) {
                if (key != ClientContainerItemTooltip.chosen)
                    ClientContainerItemTooltip.setChosen(key);

                event.setCanceled(true);
            }
        });
    }

    public static void keyInput(InputEvent.Key event) {
        if (event.getAction() != InputConstants.PRESS)
            return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.screen != null)
            return;

        int key = event.getKey();

        if (key == AtelierKeyMappings.NVD_TOGGLE.getKey().getValue()) {
            if (NightVisionDevice.setActive(LocalPlayerVars.wearables, !LocalPlayerVars.wearables.nvdActive))
                WearCapToServerPacket.send(WearableState.NVD);
        } else if (AtelierKeyMappings.QUICK_ACCESS.isDown())
            for (int i = 0; i < mc.options.keyHotbarSlots.length; i++)
                if (event.getKey() == mc.options.keyHotbarSlots[i].getKey().getValue())
                    if (i < QuickAccessSlot.CACHE.size())
                        QuickAccessPacket.send(i);
    }
}
