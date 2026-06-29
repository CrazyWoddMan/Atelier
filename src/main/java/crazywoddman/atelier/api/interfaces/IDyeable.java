package crazywoddman.atelier.api.interfaces;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface IDyeable extends DyeableLeatherItem {
    public static final String COLORS_TAG = "Colors";
    public static final int
    WHITE = 0xF9FFFE,
    GOLD = 0xFECB01,
    BIOPOLYMER = 0xF5E8C7,
    IRON = 0xBBBDB9,
    PHANTOM_CLOTH = 0x768282,
    DARK_GRAY = 0x373D3F,
    BLACK = 0x1D1D21;

    int[] getDefaultColors();

    static int[] getDefaultColors(ItemStack stack) {
        return ((IDyeable)stack.getItem()).getDefaultColors();
    }

    @Override
    default boolean hasCustomColor(ItemStack stack) {
        return stack.getTagElement(COLORS_TAG) != null;
    }

    @Override
    default void clearColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        
        if (tag != null) {
            tag.remove(COLORS_TAG);

            if (tag.isEmpty())
                stack.setTag(null);
        }
    }

    default InteractionResult caludronClean(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        CompoundTag colors = stack.getTagElement(COLORS_TAG);

        if (colors != null) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);

            if (state.is(Blocks.WATER_CAULDRON)) {
                clearColor(stack);
                context.getPlayer().awardStat(Stats.CLEAN_ARMOR);
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.CONSUME;
    }

    default void showColorTooltip(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        if (!flag.isAdvanced())
            return;

        CompoundTag tag = stack.getTagElement(COLORS_TAG);

        if (tag == null)
            return;

        int[] colors = getDefaultColors(stack);
        tooltip.add(Component.translatable(
            "item.color",
            IntStream.range(0, colors.length)
                  .map(i -> {
                      String index = Integer.toString(i);
                      return tag.contains(index) ? tag.getInt(index) : colors[i];
                  })
                  .mapToObj(i -> String.format(Locale.ROOT, "#%06X", i))
                  .collect(Collectors.joining(", "))
        ).withStyle(ChatFormatting.GRAY));
    }

    static Optional<Integer> getColorOptional(ItemStack stack, int index) {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains(COLORS_TAG)) {
            CompoundTag colors = tag.getCompound(COLORS_TAG);
            String i = Integer.toString(index);

            if (colors.contains(i))
                return Optional.of(colors.getInt(i));
        }

        return Optional.empty();
    }

    static int getColor(ItemStack stack, int index) {
        return getColorOptional(stack, index).orElse(((IDyeable)stack.getItem()).getDefaultColors()[index]);
    }

    /**
     * @deprecated Use {@link #getColor(ItemStack, int)} instead
     */
    @Deprecated
    @Override
    default int getColor(ItemStack stack) {
        return getColor(stack, 0);
    }

    static ItemStack setColor(ItemStack stack, int color, int index) {
        stack.getOrCreateTagElement(COLORS_TAG).putInt(Integer.toString(index), color);
        return stack;
    }

    @Override
    default void setColor(ItemStack stack, int color) {
        setColor(stack, color, 0);
    }

    static void copyColors(ItemStack from, ItemStack to) {
        CompoundTag tag = from.getTagElement(COLORS_TAG);
        if (tag != null) {
            to.getOrCreateTag().put(COLORS_TAG, tag);
        }
    }

    static float[] getFloatColor(ItemStack stack, int index) {
        return convert(getColor(stack, index));
    }
    
    static float[] convert(int color) {
        return new float[]{
            (color >> 16 & 0xFF) / 255F, // red
            (color >> 8 & 0xFF) / 255F,  // green
            (color & 0xFF) / 255F        // blue
        };
    }

    static int convert(float[] color) {
        return ((int)(color[0] * 255) << 16) 
             | ((int)(color[1] * 255) << 8)
             |  (int)(color[2] * 255);
    }

    static int blendDyeColors(ItemStack stack, int index, DyeColor... colors) {
        int color = blendDyeColors(getColorOptional(stack, index).orElse(0), colors);
        setColor(stack, color, index);
        return color;
    }

    static int blendDyeColors(int baseColor, DyeColor... colors) {
        int[] rgb = new int[3];
        int maxColorSum = 0;
        int totalColors = 0;

        if (baseColor != 0) {
            float r = (float)((baseColor >> 16) & 255) / 255.0F;
            float g = (float)((baseColor >> 8) & 255) / 255.0F;
            float b = (float)(baseColor & 255) / 255.0F;
            maxColorSum += (int)(Math.max(r, Math.max(g, b)) * 255.0F);
            rgb[0] += (int)(r * 255.0F);
            rgb[1] += (int)(g * 255.0F);
            rgb[2] += (int)(b * 255.0F);
            totalColors++;
        }
        
        for (DyeColor dye : colors) {
            float[] frgb = dye.getTextureDiffuseColors();
            int r = (int)(frgb[0] * 255);
            int g = (int)(frgb[1] * 255);
            int b = (int)(frgb[2] * 255);
            maxColorSum += Math.max(r, Math.max(g, b));
            rgb[0] += r;
            rgb[1] += g;
            rgb[2] += b;
            totalColors++;
        }
        
        int avarageRed = rgb[0] / totalColors;
        int avarageGreen = rgb[1] / totalColors;
        int avarageBlue = rgb[2] / totalColors;
        float avarageMaxColor = (float) maxColorSum / totalColors;
        float actualMaxColor = (float) Math.max(avarageRed, Math.max(avarageGreen, avarageBlue));
        
        if (actualMaxColor > 0) {
            avarageRed = (int) ((float) avarageRed * avarageMaxColor / actualMaxColor);
            avarageGreen = (int) ((float) avarageGreen * avarageMaxColor / actualMaxColor);
            avarageBlue = (int) ((float) avarageBlue * avarageMaxColor / actualMaxColor);
        }
        
        return (avarageRed << 16) | (avarageGreen << 8) | avarageBlue;
    }
}