package crazywoddman.atelier.events;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierTags;
import crazywoddman.atelier.recipes.AtelierRecipes;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Atelier.MODID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AtelierClientForgeEvents {
    
    private static final ResourceLocation GASMASK_OVERLAY = ResourceLocation.fromNamespaceAndPath(
        Atelier.MODID,
        "textures/gui/gasmask_overlay.png"
    );

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();

        if (AtelierRecipes.isPlate(item)) {
            List<Component> tooltip = event.getToolTip();
            tooltip.add(1, Component.empty());
            tooltip.add(2, Component.translatable(Atelier.MODID + ".tooltip.equipped").withStyle(ChatFormatting.GRAY));
            tooltip.add(3, Component.literal("+" + AtelierRecipes.getPlateRecipe(item).get().protection + " ").append(Component.translatable(Atelier.WARIUM_LOADED ? Atelier.MODID + ".tooltip.plate.protection" : "enchantment.minecraft.projectile_protection")).withStyle(ChatFormatting.BLUE));
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.player == null || !mc.options.getCameraType().isFirstPerson() || !event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id()))
            return;

        AccessoriesContainer face = AccessoriesCapability.get(mc.player).getContainer(new SlotTypeReference("face"));

        if (!face.getAccessories().getItem(0).is(AtelierTags.Items.GASMASKS) || !face.shouldRender(0))
            return;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GASMASK_OVERLAY);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
        tesselator.end();
        
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
