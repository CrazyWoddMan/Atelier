package crazywoddman.atelier.api.templates;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.interfaces.IDyeable;
import crazywoddman.atelier.api.interfaces.IWearable;
import crazywoddman.atelier.api.render.AtelierRenderHelper;

import java.util.List;
import java.util.function.Consumer;

public abstract class DyableArmor extends ArmorItem implements IWearable, IDyeable {
    private final int[] defaultColors;
    protected HumanoidModel<LivingEntity> model;

    public DyableArmor(ArmorMaterial material, Type type, Properties properties, int... defaultColors) {
        super(material, type, properties);
        this.defaultColors = defaultColors;
    }
    
    @Override
    public int[] getDefaultColors() {
        return this.defaultColors;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "overlay".equals(type)
        ? getTexture().getOverlay().map(ResourceLocation::toString).orElse(Atelier.MODID + ":textures/empty.png")
        : getTexture().get(type).toString();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        showColorTooltip(stack, tooltip, flag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return caludronClean(context);
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound().equip;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (model == null)
                    model = AtelierRenderHelper.bake(getLayerKey());

                model.crouching = living.isShiftKeyDown();
                model.riding = original.riding;
                model.young = living.isBaby();

                return model;
            }
        });
    }
}