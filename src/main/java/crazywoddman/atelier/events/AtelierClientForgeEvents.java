package crazywoddman.atelier.events;

import java.util.List;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.client.InputHandler;
import crazywoddman.atelier.data.ArmorPlates;
import crazywoddman.atelier.data.AtelierData;
import crazywoddman.atelier.gui.ClientContainerItemTooltip;
import crazywoddman.atelier.gui.GuiRenderer;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Atelier.MODID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AtelierClientForgeEvents {
    
    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        handleDataReload(event.getRecipeManager());
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
            handleDataReload(Minecraft.getInstance().getConnection().getRecipeManager());
        }
    }

    private static void handleDataReload(RecipeManager manager) {
        if (AtelierData.isDirty) {
            AtelierData.reload(manager, true);
            AtelierData.isDirty = false;
        } else {
            AtelierData.isDirty = true;
        }
    }

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Pre event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            Slot hovered = screen.getSlotUnderMouse();
            
            if (hovered != null && hovered != ClientContainerItemTooltip.lastChosen) {
                ClientContainerItemTooltip.lastChosen = hovered;
                ClientContainerItemTooltip.setChosen(0);
            }
        }
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        ClientContainerItemTooltip.lastChosen = null;
        ClientContainerItemTooltip.setChosen(0);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        List<Component> tooltip = event.getToolTip();
        
        if (tooltip.isEmpty())
            return;
        
        if (item == AtelierItems.KNEEPADS.get()) {
            tooltip.add(Math.max(0, tooltip.size() - 2), Component.translatable("item.atelier.kneepads.protection", (int)(AtelierConfig.Server.KNEEPADS_PROTECT.get() * 100)).withStyle(ChatFormatting.BLUE));
        } else ArmorPlates.get(item).ifPresent(plate -> {
            tooltip.add(1, Component.empty());
            tooltip.add(2, Component.translatable(Atelier.MODID + ".tooltip.equipped").withStyle(ChatFormatting.GRAY));
            tooltip.add(3, Component.literal("+" + plate.protection + " ").append(Component.translatable(Atelier.WARIUM_LOADED ? Atelier.MODID + ".tooltip.plate.protection" : "enchantment.minecraft.projectile_protection")).withStyle(ChatFormatting.BLUE));
        });
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.player != null && event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id()))
            GuiRenderer.render(event.getGuiGraphics(), mc);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        InputHandler.keyInput(event);
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        InputHandler.keyInputScreen(event);
    }

    @SubscribeEvent
    public static void onMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        InputHandler.mouseScrollScreen(event);
    }
}
