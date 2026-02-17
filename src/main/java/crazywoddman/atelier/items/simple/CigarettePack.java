package crazywoddman.atelier.items.simple;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import crazywoddman.atelier.api.templates.SimpleItem;
import crazywoddman.atelier.items.AtelierItems;

public class CigarettePack extends SimpleItem {
    private static final int CAPACITY = 9;

    public CigarettePack() {
        super(new Properties().stacksTo(16), 1);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFFFFFF;
    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Damage");
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack pack, Slot slot, ClickAction click, Player player) {
        if (click == ClickAction.SECONDARY && slot.allowModification(player) && pack.getCount() == 1) {
            ItemStack slotItem = slot.getItem();
            
            if (slotItem.isEmpty()) {
                slot.safeInsert(new ItemStack(AtelierItems.CIGARETTE.get(), removeCigarettes(player, pack, true)));
                return true;
            } else if (slotItem.is(AtelierItems.CIGARETTE.get()))
                return addCigarettes(player, pack, slotItem);
        }
        
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pack, ItemStack cursorItem, Slot slot, ClickAction click, Player player, SlotAccess slotAccess) {
        if (click == ClickAction.SECONDARY && slot.allowModification(player) && pack.getCount() == 1) {
            if (cursorItem.isEmpty()) {
                slotAccess.set(new ItemStack(AtelierItems.CIGARETTE.get(), removeCigarettes(player, pack, false)));
                return true;
            } else if (cursorItem.is(AtelierItems.CIGARETTE.get()))
                return addCigarettes(player, pack, cursorItem);
        }
        
        return false;
    }

    public static int getCigaretteCount(ItemStack pack) {
        return CAPACITY - pack.getDamageValue();
    }

    public static boolean addCigarettes(Player player, ItemStack pack, ItemStack cigarettes) {
        int canAdd = Math.min(cigarettes.getCount(), CAPACITY - getCigaretteCount(pack));
        
        if (canAdd <= 0)
            return false;

        player.level().playSound(
            player,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.BUNDLE_INSERT,
            SoundSource.PLAYERS,
            2F,
            0.8F + player.getRandom().nextFloat() * 0.4F
        );
        pack.setDamageValue(pack.getDamageValue() - canAdd);
        cigarettes.shrink(canAdd);

        return true;
    }

    public static int removeCigarettes(Player player, ItemStack pack, boolean all) {
        player.level().playSound(
            player,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.BUNDLE_REMOVE_ONE,
            SoundSource.PLAYERS,
            2F,
            0.8F + player.getRandom().nextFloat() * 0.4F
        );
        
        if (!all)
            pack.setDamageValue(pack.getDamageValue() + 1);
        
        int cigarettes = getCigaretteCount(pack);
        
        if (all || cigarettes <= 0)
            pack.shrink(1);

        return all ? cigarettes : 1;
    }
}