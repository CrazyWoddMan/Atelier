package crazywoddman.atelier.items.armor;

import java.util.function.Supplier;

import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableArmor;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;

public class TacticalBoots extends DyableArmor {
    public TacticalBoots() {
        super(ArmorMaterials.LEATHER, Type.BOOTS, new Properties().defaultDurability(ArmorMaterials.LEATHER.getDurabilityForType(Type.BOOTS) * 2), DEFAULT_LEATHER_COLOR);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(16, partdefinition -> {
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 8.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.3F))
            .texOffs(0, 8).addBox(-2.0F, 10.0F, -3.2F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 8).mirror().addBox(-2.0F, 10.0F, -3.2F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.2F)).mirror(false)
            .texOffs(0, 0).mirror().addBox(-2.0F, 8.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offset(1.9F, 12.0F, 0.0F));
        });
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return true;
    }
}