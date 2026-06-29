package crazywoddman.atelier.items.armor;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.interfaces.IContainerItem;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.ContainerItem;
import crazywoddman.atelier.api.templates.DyableArmor;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.gui.ContainerItemTooltip;
import crazywoddman.atelier.items.AtelierArmorMaterials;

import java.util.Optional;
import java.util.function.Supplier;

public class Webbing extends DyableArmor implements IContainerItem, IQuickAccess {
    
    public Webbing() {
        super(
            AtelierArmorMaterials.LEATHER,
            ArmorItem.Type.CHESTPLATE,
            new Properties().defaultDurability(0),
            DEFAULT_LEATHER_COLOR,
            DEFAULT_LEATHER_COLOR,
            GOLD
        );
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        IDyeable.setColor(stack, color, 0);
        IDyeable.setColor(stack, color, 1);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new ContainerItemTooltip(stack));
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition -> {
            PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24).addBox(-4.1F, 0.9F, -4.0F, 3.0F, 4.0F, 2.0F)
            .texOffs(22, 9).addBox(-1.5F, 5.9F, -4.0F, 3.0F, 4.0F, 2.0F)
            .texOffs(0, 24).addBox(-5.1F, 5.9F, -4.0F, 3.0F, 4.0F, 2.0F)
            .texOffs(0, 0).addBox(-5.0F, 7.0F, -3.0F, 10.0F, 3.0F, 6.0F)
            .texOffs(10, 24).addBox(1.1F, 0.9F, -4.0F, 3.0F, 4.0F, 2.0F)
            .texOffs(10, 24).addBox(2.1F, 5.9F, -4.0F, 3.0F, 4.0F, 2.0F), PartPose.ZERO);
            body.addOrReplaceChild("webbing_r1", CubeListBuilder.create().texOffs(20, 27).addBox(-0.9F, -4.0F, -0.9F, 3.0F, 2.0F, 1.0F)
            .texOffs(24, 19).addBox(-4.5F, -4.0F, -0.9F, 3.0F, 2.0F, 1.0F)
            .texOffs(20, 24).addBox(-8.1F, -4.0F, -0.9F, 3.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(3.0F, 10.0F, -4.0F, -0.2182F, 0.0F, 0.0F));
            body.addOrReplaceChild("webbing_r2", CubeListBuilder.create().texOffs(20, 27).addBox(-0.9F, -4.0F, -0.9F, 3.0F, 2.0F, 1.0F)
            .texOffs(20, 24).addBox(-6.1F, -4.0F, -0.9F, 3.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(2.0F, 5.0F, -4.0F, -0.2182F, 0.0F, 0.0F));
            body.addOrReplaceChild("webbing_r3", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, -9.45F, 1.0F, 1.0F, 9.0F, 5.0F), PartPose.offsetAndRotation(1.0F, 8.75F, -3.5F, 0.0F, 0.0F, 0.2182F));
            body.addOrReplaceChild("webbing_r4", CubeListBuilder.create().texOffs(12, 10).addBox(-1.0F, -9.45F, 1.0F, 1.0F, 9.0F, 5.0F), PartPose.offsetAndRotation(-1.0F, 8.75F, -3.5F, 0.0F, 0.0F, -0.2182F));
        });
    };

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack carried, Slot slot, ClickAction click, Player player, SlotAccess carriedAccess) {
        return ContainerItem.clickedOn(stack, carried, slot, click, player, carriedAccess)
            || super.overrideOtherStackedOnMe(stack, carried, slot, click, player, carriedAccess);
    }

    @Override
    public int getContainerCapacity() {
        return AtelierConfig.Server.WEBBING_CAPACITY.get();
    }

    @Override
    public ItemStack[] getContainerItems(ItemStack stack) {
        return ContainerItem.getItems(stack);
    }

    @Override
    public int getVisibleSlots(ItemStack stack, ItemStack carried) {
        return ContainerItem.getVisible(stack, carried);
    }

    @Override
    public boolean insertAtIndex(ItemStack stack, ItemStack input, SlotAccess output, int index) {
        return !input.is(AtelierTags.Items.POUCH_BLACKLIST) && ContainerItem.insert(stack, input, output, index);
    }

    @Override
    public boolean quickAccess(Player player, ItemStack stack) {
        return insertAtIndex(stack, player.getItemBySlot(EquipmentSlot.MAINHAND), SlotAccess.forEquipmentSlot(player, EquipmentSlot.MAINHAND), 0);
    }

    @Override
    public ItemStack quickAccessPreview(ItemStack stack) {
        return ContainerItem.preview(stack);
    }
}