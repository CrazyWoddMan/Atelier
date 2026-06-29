package crazywoddman.atelier.client;

import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.data.AtelierSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;

/** Utility class for safely calling methods from classes annotated with {@link Dist#CLIENT} */
public class ClientUtils {

    public static ResourceLocation makeTexturePath(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, "textures/" + path + ".png");
    }

    public static Minecraft getMC() {
        return Minecraft.getInstance();
    }

    public static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    public static boolean creativeInventoryOpen() {
        return Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen;
    }

    public static boolean isFirstPerson(LivingEntity entity) {
        return entity instanceof LocalPlayer && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    public static void renderOverlay(GuiGraphics graphics, ResourceLocation texture) {
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        graphics.blit(texture, 0, 0, 0, 0, width, height, width, height);
    }

    public static void renderItem(GuiGraphics graphics, ItemStack stack, int x, int y) {
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
    }

    public static void playDetonationSound(Player player) {
        boolean halal = AtelierConfig.Client.HALAL_MODE.get();
        Minecraft.getInstance().getSoundManager().play(
            player.isLocalPlayer()
            ? new SimpleSoundInstance(
                (halal ? AtelierSounds.HALAL : AtelierSounds.DETONATION).getId(),
                SoundSource.PLAYERS,
                1, 1,
                player.getRandom(),
                false,
                0,
                Attenuation.NONE,
                0, 0, 0,
                true
            ) : new EntityBoundSoundInstance(
                (halal ? AtelierSounds.HALAL : AtelierSounds.DETONATION).get(),
                SoundSource.PLAYERS,
                1,
                1,
                player,
                0
            )
        );
    }

    public static void playBreathingSound(int playerID, int slot) {
        if (Minecraft.getInstance().level.getEntity(playerID) instanceof Player player)
            Minecraft.getInstance().getSoundManager().play(new FilterBreathingSound(player, slot));
    }

    public static class FilterBreathingSound extends EntityBoundSoundInstance {
        private final LivingEntity entity;
        private final SimpleSlot slot;

        public FilterBreathingSound(LivingEntity entity, int gasmaskSlot) {
            super(
                AtelierSounds.GASMASK.get(),
                entity.getSoundSource(),
                1,
                0.85f + entity.getRandom().nextFloat() * 0.05f,
                entity,
                0
            );
            this.entity = entity;
            this.slot = SimpleSlot.of(IWearableAccessory.FACE, gasmaskSlot);
        }

        @Override
        public void tick() {
            super.tick();
            
            if (this.entity == null
            || !this.entity.isAlive()
            || CompatHelper.getSlotContainer(this.entity, this.slot.name).get().getItem(this.slot.index).isEmpty()
            || !CompatHelper.shouldRender(this.entity, this.slot)
            || entity.isUnderWater()
            ) stop();
        }
    }
}