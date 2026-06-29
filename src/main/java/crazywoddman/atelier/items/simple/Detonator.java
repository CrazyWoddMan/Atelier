package crazywoddman.atelier.items.simple;

import java.util.List;

import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.network.packets.DetonationSoundPacket;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.Queue;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.api.interfaces.ITooltipGenerator;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class Detonator extends Item implements ITooltipGenerator {
    private static int EXPLOSION_TASK = Queue.makeID();
    public enum ExplosionType {DISABLE, NO_DESTRUCTION, ENABLE}

    public Detonator() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        generateTooltip(tooltip, 1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.PLAYERS, 1, 1);
        ExplosionType type = AtelierConfig.Server.BOMBVEST_MODE.get();

        if (type != ExplosionType.DISABLE && !level.isClientSide) {
            Queue queue = Queue.of(level);
            int id = EXPLOSION_TASK + player.getId();

            if (!queue.hasTask(id) && findExplosives(player, true).count > 0) {
                DetonationSoundPacket.send((ServerPlayer)player);
                queue.add(id, 45, () -> {
                    if (player.isRemoved() || !player.isAlive())
                        return;

                    SearchResult search = findExplosives(player, false);

                    if (search.count == 0)
                        return;

                    float power = 3 + (float)Math.pow(Math.min(search.count, AtelierConfig.Server.BOMBVEST_POWER.get()), 0.7);
                    player.level().explode(
                        null,
                        player.getX(),
                        player.getY() + 1,
                        player.getZ(),
                        power,
                        search.incendiary,
                        type == ExplosionType.NO_DESTRUCTION ? Level.ExplosionInteraction.NONE : Level.ExplosionInteraction.TNT
                    );
                });
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private static SearchResult findExplosives(Player player, boolean findFirst) {
        SearchResult result = new SearchResult(0, false);
        
        for (Pair<SimpleSlot, ItemStack> pair : CompatHelper.findEquipped(player, s -> IContainerItem.get(s.getItem()).isPresent()))
            if (findExplosives(pair.second(), player, findFirst, result) && findFirst)
                return SearchResult.FOUND;

        for (EquipmentSlot slot : EquipmentSlot.values())
            if (slot.isArmor() && findExplosives(player.getItemBySlot(slot), player, findFirst, result) && findFirst)
                return SearchResult.FOUND;

        return result;
    }

    private static boolean findExplosives(ItemStack stack, Player player, boolean findFirst, SearchResult result) {
        if (stack.isEmpty())
            return false;

        ItemStack[] containers = stack.getItem() instanceof IContainerItem
            ? new ItemStack[]{stack}
            : IContainerItem.getItems(stack);

        for (ItemStack container : containers) {
            ItemStack[] contents = IContainerItem.getItems(container);

            for (int i = 0; i < contents.length; i++) {
                ItemStack content = contents[i];

                if (!content.is(AtelierTags.Items.CAN_DETONATE))
                    continue;

                result.count += content.getCount();

                if (findFirst)
                    return result.count > 0;

                if(!result.incendiary && content.is(AtelierTags.Items.CAN_DETONATE_FIRE))
                    result.incendiary = true;

                if (!player.isCreative())
                    IContainerItem.insert(container, ItemStack.EMPTY, SlotAccess.NULL, i);
            }
        }

        return result.count > 0;
    }

    private static class SearchResult {
        static final SearchResult FOUND = new SearchResult(1, false);

        int count;
        boolean incendiary;

        SearchResult(int count, boolean incendiary) {
            this.count = count;
            this.incendiary = incendiary;
        }
    }
}