package crazywoddman.atelier.items.templates;

import java.util.Optional;
import java.util.function.Supplier;

import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.api.templates.DyeableContainerItem;
import crazywoddman.atelier.client.renderers.NightVisionRenderer;
import crazywoddman.atelier.data.AtelierSounds;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.data.ServerUtils;
import crazywoddman.atelier.data.WearablesCapability;
import crazywoddman.atelier.data.WearablesCapability.WearableState;
import crazywoddman.atelier.network.packets.WearCapToClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class NightVisionDevice extends DyeableContainerItem implements IWearableAccessory {
    
    public NightVisionDevice() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return AtelierConfig.Server.NVD_CONSUME.get() > 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF0000;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) (IContainerItem.getFullness(stack) * 13);
    }

    @Override
    public int[] getDefaultColors() {
        return new int[]{BIOPOLYMER, DEFAULT_LEATHER_COLOR};
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return () -> new NightVisionRenderer(this);
    }
    
    @Override
    public boolean hideUnderHelmet() {
        return false;
    }

    @Override
    public void onEquip(ItemStack stack, LivingEntity entity, SimpleSlot slot) {}

    @Override
    public void onUnequip(ItemStack stack, LivingEntity entity, SimpleSlot slot) {}

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.empty();
    }

    @Override
    public boolean insertAtIndex(ItemStack stack, ItemStack input, SlotAccess output, int index) {
        return AtelierConfig.Server.NVD_CONSUME.get() > 0
            && input.is(Items.REDSTONE)
            && super.insertAtIndex(stack, input, output, index);
    }

    @Override
    public ItemStack quickAccessPreview(ItemStack stack) {
        return ItemStack.EMPTY;
    }

    @Override
    public void wearTick(ItemStack stack, LivingEntity entity, SimpleSlot slot) {
        if (!(entity instanceof ServerPlayer player) || player.isCreative() || player.isSpectator())
            return;

        int consumption = AtelierConfig.Server.NVD_CONSUME.get();

        if (consumption == 0 || player.tickCount % consumption != 0)
            return;
        
        WearablesCapability.get(player).ifPresent(cap -> {
            if (cap.nvdActive) {
                CompoundTag tag = stack.getTag();

                if (tag != null) {
                    ListTag list = tag.getList("Items", ListTag.TAG_COMPOUND);

                    if (list.size() > 0) {
                        CompoundTag redstone = list.getCompound(0);
                        byte count = redstone.getByte("Count");

                        if (count > 1) {
                            redstone.putByte("Count", --count);
                            return;
                        } else {
                            tag.remove("Items");

                            if (tag.isEmpty())
                                stack.setTag(null);
                        }
                    }
                }

                cap.nvdActive = false;
                WearCapToClientPacket.send(player, WearableState.NVD, true);
                AtelierSounds.play(AtelierSounds.NVD_OFF, player, null);
            }
        });
    }

    public static boolean setActive(WearablesCapability capability, boolean active) {
        if (capability.nvdActive != active && CompatHelper
            .getStackInSlot(capability.holder, SimpleSlot.of(IWearableAccessory.HAT, 0))
            .filter(s -> !active || (s.is(AtelierTags.Items.NVD) && (capability.holder.isCreative() || AtelierConfig.Server.NVD_CONSUME.get() == 0 || IContainerItem.getFullness(s) > 0)))
            .isPresent()
        ) {
            capability.nvdActive = active;

            if (capability.holder instanceof ServerPlayer) {
                AtelierSounds.play(active ? AtelierSounds.NVD_ON : AtelierSounds.NVD_OFF, capability.holder, null);
                ServerUtils.grantAdvancement(capability.holder, "nvd_first_use");
            }

            return true;
        }
        return false;
    }
}