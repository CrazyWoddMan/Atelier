package crazywoddman.atelier.items.simple;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierClientUtils;
import crazywoddman.atelier.AtelierTags;
import crazywoddman.atelier.api.templates.SimpleItem;
import crazywoddman.atelier.config.Config;
import crazywoddman.atelier.items.AtelierItems;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Detonator extends SimpleItem {
    public enum ExplosionType {
        DISABLE, NO_DESTRUCTION, ENABLE
    }

    public Detonator() {
        super(new Properties().stacksTo(1), 1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1, 1);
        ExplosionType type = Config.SERVER.bombVest.get();

        if (type != ExplosionType.DISABLE) {
            int count = 0;
            boolean fireItems = false;
            
            for (SlotEntryReference slot : AccessoriesCapability.get(player).getEquipped(AtelierItems.POUCH.get())) {
                ItemStack pouch = slot.stack();
                CompoundTag tag = pouch.getTag();

                if (tag != null && tag.contains("Items")) {
                    ListTag items = tag.getList("Items", ListTag.TAG_COMPOUND);
                    
                    for (int i = 0; i < items.size(); i++) {
                        ItemStack stack = ItemStack.of(items.getCompound(i));

                        if(stack.is(AtelierTags.Items.CAN_DETONATE))
                            count += stack.getCount();

                        if(stack.is(AtelierTags.Items.CAN_DETONATE_FIRE))
                            fireItems = true;
                    }
                }

                if (!level.isClientSide && count > 0 && !player.isCreative())
                    slot.reference().setStack(ItemStack.EMPTY);
            }

            boolean fire = fireItems;

            if (count > 0) {
                if (level.isClientSide && !Atelier.Queue.hasTask(player, "boom")) {
                    Atelier.Queue.addToQueue(player, "boom", () -> {}, 45);
                    AtelierClientUtils.playeDetonationSound(player);
                } else {
                    float power = 3 + (float)Math.pow(Math.min(count, Config.SERVER.bombVestMaxExplosionPower.get()), 0.7);
                    Atelier.Queue.addToQueue(player, "boom", () ->
                        level.explode(
                            null,
                            player.getX(),
                            player.getY() + 1,
                            player.getZ(),
                            power,
                            fire,
                            type == ExplosionType.NO_DESTRUCTION ? Level.ExplosionInteraction.NONE : Level.ExplosionInteraction.TNT
                        ),
                        45
                    );
                }
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static double getProgressionElement(int index, double startValue, double targetValue, int n) {
        
        double steepness = 6.0 / n;
        double x = steepness * (index - 1);
        double shift = steepness * (n - 1) - Math.log(19);
        double progress = 1.0 / (1.0 + Math.exp(-(x - shift)));

        return startValue + (targetValue - startValue) * progress;
    }
}