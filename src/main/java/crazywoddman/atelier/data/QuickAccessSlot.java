package crazywoddman.atelier.data;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.api.interfaces.InteractSound;
import crazywoddman.atelier.client.ClientUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record QuickAccessSlot(SimpleSlot... path) implements Comparable<QuickAccessSlot> {
    public static final Set<QuickAccessSlot> CACHE = new TreeSet<>();

    @Override
    public int compareTo(QuickAccessSlot other) {
        return Arrays.compare(this.path, other.path);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeByte(this.path.length);

        for (SimpleSlot slot : path)
            slot.toNetwork(buffer);
    }

    public static QuickAccessSlot fromNetwork(FriendlyByteBuf buf) {
        SimpleSlot[] path = new SimpleSlot[buf.readByte()];

        for (int i = 0; i < path.length; i++)
            path[i] = SimpleSlot.fromNetwork(buf);

        return new QuickAccessSlot(path);
    }

    public static void check(SimpleSlot slot) {
        CACHE.removeIf(access -> access.path()[0].equals(slot));
        ItemStack stack = slot.getAccess(ClientUtils.getLocalPlayer()).map(SlotAccess::get).orElse(ItemStack.EMPTY);

        if (stack.isEmpty())
            return;
        
        if (IQuickAccess.get(stack.getItem()).isPresent()) {
            CACHE.add(new QuickAccessSlot(slot));
            return;
        }
        
        IModular.getModules(stack.getItem()).ifPresent(modules -> {
            Optional<CompoundTag> equipped = IModular.getModules(stack);

            for (int m = 0; m < modules.size(); m++) {
                List<? extends String> accessible = AtelierConfig.Client.QUICK_ACCESS_MODULES.get();
                String module = modules.get(m);
                int index = m - modules.indexOf(module);

                if (accessible.contains(module)) {
                    CACHE.add(new QuickAccessSlot(slot, SimpleSlot.of(module, index)));
                } else {
                    equipped.ifPresent(tag -> {
                        CompoundTag items = tag.getCompound(module);
                        if (!items.isEmpty()) {
                            CompoundTag item = items.getCompound(Integer.toString(index));

                            if (!item.isEmpty() && IQuickAccess.get(ItemStack.of(item).getItem()).isPresent())
                                CACHE.add(new QuickAccessSlot(slot, SimpleSlot.of(module, index)));
                        }
                    });
                }
            }
        });
    }

    public void use(ServerPlayer player) {
        Optional<SlotAccess> holder = this.path[0].getAccess(player);
        boolean hasParent = this.path.length > 1;

        (hasParent ? holder.map(parent -> IModular.getAccess(parent.get(), this.path[1])) : holder).ifPresent(slot -> {
            ItemStack hand = player.getItemBySlot(EquipmentSlot.MAINHAND).copy();
            ItemStack stack = slot.get();
            Item item = stack.getItem();

            if (item instanceof IQuickAccess access) {
                if (access.quickAccess(player, stack)) {
                    if (hasParent)
                        slot.set(stack);

                    playSound(hand, item, player);
                }
            } else if (slot.set(hand)) {
                player.setItemSlot(EquipmentSlot.MAINHAND, stack);
                playSound(hand, item, player);
            }
        });
    }

    private static void playSound(ItemStack hand, Item item, Player player) {
        if (!Atelier.ACCESSORIES_LOADED && !(item instanceof ArmorItem))
            return;

        EquipSound sound = item instanceof InteractSound interact ? interact.interactSound() : EquipSound.BUNDLE;

        if (hand.isEmpty()) {
            sound.playUnequip(player, null);
        } else {
            sound.playEquip(player, null);
        }
    }
}