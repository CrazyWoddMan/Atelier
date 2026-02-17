package crazywoddman.atelier.items.accessories;

import java.util.List;
import java.util.function.Supplier;

import crazywoddman.atelier.renderers.KneePadsRenderer;
import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class KneePads extends DyableAccessory {

    public KneePads() {
        super(new Properties().stacksTo(1), 8618876);
    }

    public static byte protection;

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("atelier.tooltip.equipped").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.atelier.kneepads.protection", protection).withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(16, 16, partdefinition -> {
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(3, 11).addBox(-2.0F, 2.0F, -2.9F, 4.0F, 4.0F, 1.0F)
            .texOffs(0, 0).addBox(-2.0F, 2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(3, 11).addBox(-2.0F, 2.0F, -2.9F, 4.0F, 4.0F, 1.0F)
            .texOffs(0, 0).addBox(-2.0F, 2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        });
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return KneePadsRenderer::new;
    }

    @Override
    public SoundEventData getEquipSound() {
        return new SoundEventData(SoundEvents.ARMOR_EQUIP_IRON, 1, 1);
    }
}