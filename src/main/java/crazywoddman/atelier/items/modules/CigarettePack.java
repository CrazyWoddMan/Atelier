package crazywoddman.atelier.items.modules;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.interfaces.IModule;
import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.api.interfaces.ITooltipGenerator;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.items.AtelierItems;

public class CigarettePack extends Item implements IModule, ITooltipGenerator, IQuickAccess {
    private static final int CAPACITY = 9;

    public CigarettePack() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        generateTooltip(tooltip, 1);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("marlboro_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -1.4F, -5.8F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, -8.2F, 0.85F, -0.1309F, 0.0F, 0.0F));
            head.addOrReplaceChild("marlboro_r2", CubeListBuilder.create().texOffs(37, 1).addBox(-0.5F, -0.5F, -1.5F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(53, 2).addBox(-0.5F, -1.5659F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(5.5F, -7.1341F, 2.3681F, -0.0873F, 0.0F, 0.0F));
            head.addOrReplaceChild("marlboro_r3", CubeListBuilder.create().texOffs(53, 2).addBox(-0.5F, -1.5659F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(5.5F, -7.1341F, 1.3681F, -0.0873F, 0.0F, 0.0F));
            head.addOrReplaceChild("marlboro_r4", CubeListBuilder.create().texOffs(53, 2).addBox(-0.5F, -1.4659F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(5.5F, -7.1341F, 3.3681F, -0.0873F, 0.0F, 0.0F));
            head.addOrReplaceChild("marlboro_r5", CubeListBuilder.create().texOffs(45, 2).addBox(-0.5F, -0.5F, -1.4181F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -8.1341F, 2.3681F, -0.0756F, -0.0436F, -0.5219F));
        });
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
    public ItemStack quickAccessPreview(ItemStack stack) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean quickAccess(Player player, ItemStack stack) {
        ItemStack hand = player.getItemBySlot(EquipmentSlot.MAINHAND);

        if (hand.isEmpty()) {
            removeCigarettes(player, stack, false);
            player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AtelierItems.CIGARETTE.get()));
            return true;
        }

        return addCigarettes(player, stack, hand);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack pack, Slot slot, ClickAction click, Player player) {
        if (click == ClickAction.SECONDARY && slot.allowModification(player) && pack.getCount() == 1) {
            ItemStack slotItem = slot.getItem();
            
            if (slotItem.isEmpty()) {
                slot.safeInsert(new ItemStack(AtelierItems.CIGARETTE.get(), removeCigarettes(player, pack, true)));
                return true;
            }
            return addCigarettes(player, pack, slotItem);
        }
        
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pack, ItemStack carriedItem, Slot slot, ClickAction click, Player player, SlotAccess carriedAccess) {
        if (click == ClickAction.SECONDARY && slot.allowModification(player) && pack.getCount() == 1) {
            if (carriedItem.isEmpty()) {
                carriedAccess.set(new ItemStack(AtelierItems.CIGARETTE.get(), removeCigarettes(player, pack, false)));
                return true;
            }
            return addCigarettes(player, pack, carriedItem);
        }
        
        return false;
    }

    public static int getCigaretteCount(ItemStack pack) {
        return CAPACITY - pack.getDamageValue();
    }

    public static boolean addCigarettes(Player player, ItemStack pack, ItemStack cigarettes) {
        if (cigarettes.is(AtelierItems.CIGARETTE.get())) {
            int canAdd = Math.min(cigarettes.getCount(), CAPACITY - getCigaretteCount(pack));
            
            if (canAdd > 0) {
                EquipSound.BUNDLE.playEquip(player, player);
                pack.setDamageValue(pack.getDamageValue() - canAdd);
                cigarettes.shrink(canAdd);
                return true;
            }
        }

        return false;
    }

    public static int removeCigarettes(Player player, ItemStack pack, boolean all) {
        EquipSound.BUNDLE.playUnequip(player, player);
        
        if (!all)
            pack.setDamageValue(pack.getDamageValue() + 1);
        
        int cigarettes = getCigaretteCount(pack);
        
        if (all || cigarettes <= 0)
            pack.shrink(1);

        return all ? cigarettes : 1;
    }
}