package crazywoddman.atelier.api.interfaces;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.data.QuickAccessSlot;
import it.unimi.dsi.fastutil.Pair;
import crazywoddman.atelier.client.ClientUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public interface IQuickAccess extends InteractSound {
    static final Set<Item> BLACKLIST = new HashSet<>();

    boolean quickAccess(Player player, ItemStack stack);

    ItemStack quickAccessPreview(ItemStack stack);

    static Optional<IQuickAccess> get(Item item) {
        return item instanceof IQuickAccess access && !BLACKLIST.contains(item) ? Optional.of(access) : Optional.empty();
    }

    static void reload() {
        BLACKLIST.clear();
        AtelierConfig.Client.QUICK_ACCESS_BLACKLIST
            .get()
            .stream()
            .map(ResourceLocation::tryParse)
            .filter(Objects::nonNull)
            .map(ForgeRegistries.ITEMS::getValue)
            .filter(Objects::nonNull)
            .forEach(BLACKLIST::add);
        Player player = ClientUtils.getLocalPlayer();

        if (player == null)
            return;

        for (EquipmentSlot slot : EquipmentSlot.values())
            if (slot.isArmor())
                QuickAccessSlot.check(SimpleSlot.of(slot));

        for (Pair<SimpleSlot, ItemStack> pair : CompatHelper.findEquipped(player, s -> true))
            QuickAccessSlot.check(pair.first());
    }
}