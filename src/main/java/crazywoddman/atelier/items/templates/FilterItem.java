package crazywoddman.atelier.items.templates;

import java.util.List;
import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.templates.DyableModule;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class FilterItem extends DyableModule {
    public static final String
    EFFECTS_TAG = "Protects",
    CREATIVE_TAG = "Creative",
    PREPARED_TAG = "Prepared";
    public static boolean equipped;
    
    public FilterItem(Properties properties) {
        super(properties.setNoRepair(), IRON);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(CREATIVE_TAG);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        String desc = super.getDescriptionId(stack);
        return stack.getOrCreateTag().getBoolean(CREATIVE_TAG) ? desc + ".creative" : desc;
    }

    @Override
    public EquipSound equipSound() {
        return EquipSound.GOLD;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("atelier.tooltip.equipped").withStyle(ChatFormatting.GRAY));
        CompoundTag tag = stack.getOrCreateTag();
        boolean isCreative = tag.getBoolean(CREATIVE_TAG);
        boolean hasEffects = tag.contains(EFFECTS_TAG);

        if (isCreative || hasEffects) {
            MutableComponent desc = Component.translatable(Atelier.MODID + ".tooltip.filter").withStyle(ChatFormatting.BLUE).append(" ");

            if (hasEffects) {
                ListTag effects = tag.getList(EFFECTS_TAG, ListTag.TAG_STRING);

                for (int i = 0; i < effects.size(); i++) {
                    ResourceLocation key = ResourceLocation.tryParse(effects.getString(i));

                    if (key != null && ForgeRegistries.MOB_EFFECTS.containsKey(key)) {
                        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(key);
                        desc.append(Component.translatable("effect." + key.getNamespace() + "." + key.getPath()).withStyle(effect.getCategory().getTooltipFormatting()));

                        if (effects.size() > 1 && i < effects.size() - 1)
                            desc.append(",");

                        desc.append(" ");
                    }
                }

                if (!isCreative) {
                    int timeLeft = stack.getMaxDamage() - stack.getDamageValue();
                    desc.append(String.format("(%d:%02d)", timeLeft / 60, timeLeft % 60));
                }
            } else {
                if (isCreative)
                    desc.append(Component.translatable(Atelier.MODID + ".tooltip.filter.creative"));
            }

            tooltip.add(desc);
        } else
            tooltip.add(Component.translatable(tag.getBoolean(PREPARED_TAG) ? "atelier.tooltip.filter.prepared" : "effect.none").withStyle(ChatFormatting.GRAY));
            
        super.appendHoverText(stack, level, tooltip, flag);
    }
}