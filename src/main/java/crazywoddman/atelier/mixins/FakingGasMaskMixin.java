package crazywoddman.atelier.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crazywoddman.atelier.AtelierTags;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
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
        
        if (!stack.is(AtelierTags.Items.WARIUM_GASMASKS))
            return AccessoriesCapability.getOptionally(entity).map(capability -> {
                ExpandedSimpleContainer filters = capability.getContainer(new SlotTypeReference("gas_filter")).getAccessories();

                for (int i = 0; i < filters.getContainerSize(); i++) {
                    ItemStack item = filters.getItem(i);

                    if (item.isEmpty())
                        continue;

                    CompoundTag tag = item.getOrCreateTag();

                    if (tag.getList("effects", ListTag.TAG_STRING).contains(StringTag.valueOf("minecraft:poison")) || tag.getBoolean("isCreative"))
                        return new ItemStack(CrustyChunksModItems.GAS_MASK_HELMET.get());
                }

                return stack;
            }).orElse(stack);
        
        return stack;
    }
}
