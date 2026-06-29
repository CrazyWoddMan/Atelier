package crazywoddman.atelier.items.accessories;

import java.util.List;
import java.util.function.Supplier;

import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.interfaces.ITooltipGenerator;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.client.renderers.HazmatRenderer;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class Hazmat extends DyableAccessory implements ITooltipGenerator {

    public Hazmat() {
        super(BIOPOLYMER);
    }

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return HazmatRenderer::new;
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition -> {
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.ZERO);
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.6F))
            .texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.ZERO);
            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(48, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.29F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.29F)), PartPose.offset(5.0F, 2.0F, 0.0F));
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 15).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.29F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(48, 15).addBox(-2.05F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.29F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        if (AtelierConfig.Server.HAZMAT_WARIUM_RAD.get())
            generateTooltip(tooltip, 4);
    }

    public static boolean isComplete(Player player) {
        return AtelierConfig.Server.HAZMAT_WARIUM_RAD.get()
            && CompatHelper.getSlotContainer(player, IWearableAccessory.SUIT).map(suit -> suit.getItem(0).is(AtelierItems.HAZMAT.get())).orElse(false)
            && player.getItemBySlot(EquipmentSlot.FEET).is(AtelierItems.TACTICAL_BOOTS.get())
            && CompatHelper.getSlotContainer(player, HAND).map(hands -> hands.countItem(AtelierItems.LEATHER_GLOVE.get()) > 1).orElse(false)
            && CompatHelper.getSlotContainer(player, IWearableAccessory.FACE).map(face -> {
                for (int i = 0; i < face.getContainerSize(); i++) {
                    ItemStack stack = face.getItem(i);
                    if (stack.is(AtelierTags.Items.GASMASKS) && IModular.getModule(stack, IModular.GAS_FILTER).isPresent())
                        return true;
                }
                return false;
            }).orElse(false);
    }
}