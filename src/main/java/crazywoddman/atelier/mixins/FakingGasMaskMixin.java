package crazywoddman.atelier.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.data.AtelierTags;
import crazywoddman.atelier.items.templates.FilterItem;
import net.mcreator.crustychunks.init.CrustyChunksModItems;
import net.mcreator.crustychunks.procedures.ToxicCloudEntityProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin({ToxicCloudEntityProcedure.class})
public class FakingGasMaskMixin {
    
    @Redirect(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private static ItemStack injectReturn(LivingEntity entity, EquipmentSlot slot) {
        ItemStack stack = entity.getItemBySlot(slot);
        
        if (!stack.is(AtelierTags.Items.WARIUM_GASMASKS)) {
            return CompatHelper.getSlotContainer(entity, IWearableAccessory.FACE).map(container -> {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    ItemStack face = container.getItem(i);

                    if (face.is(AtelierTags.Items.GASMASKS)) {
                        return IModular.getModule(face, IModular.GAS_FILTER).map(filters -> {
                            for (String index : filters.getAllKeys()) {
                                CompoundTag filter = ItemStack.of(filters.getCompound(index)).getOrCreateTag();
                                if (filter.getList(FilterItem.EFFECTS_TAG, ListTag.TAG_STRING).contains(StringTag.valueOf("minecraft:poison"))
                                || filter.getBoolean(FilterItem.CREATIVE_TAG)
                                ) return new ItemStack(CrustyChunksModItems.GAS_MASK_HELMET.get());
                            }
                            return stack;
                        }).orElse(stack);
                    }
                }
                return stack;
            }).orElse(stack);
        }
        
        return stack;
    }
}
