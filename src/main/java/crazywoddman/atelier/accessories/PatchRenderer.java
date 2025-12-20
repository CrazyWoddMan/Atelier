package crazywoddman.atelier.accessories;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import crazywoddman.atelier.recipes.AtelierRecipes;
import crazywoddman.atelier.recipes.PatchRecipe;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;

public class PatchRenderer implements SimpleAccessoryRenderer {
    private static Map<String, EquipmentSlot> ARMOR_SLOTS = Map.of(
        "helmet", EquipmentSlot.HEAD,
        "chestplate", EquipmentSlot.CHEST,
        "leggings", EquipmentSlot.LEGS,
        "boots", EquipmentSlot.FEET
    );

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
        if (reference.slotName().endsWith("patch"))
            SimpleAccessoryRenderer.super.render(stack, reference, matrices, model, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, PoseStack poseStack) {
        if (!(model instanceof HumanoidModel<?> humanoid))
            return;

        AccessoriesCapability accessoriesCap = reference.capability();

        for (PatchRecipe recipe :reference.entity().level().getRecipeManager().getAllRecipesFor(AtelierRecipes.PATCH_RECIPE_TYPE.get())) {
            if (!recipe.getSlot().equals(reference.slotName()))
                continue;

            Ingredient wearable = recipe.getWearable();
            String parent = recipe.getParent();

            if (!(ARMOR_SLOTS.containsKey(parent) && wearable.test(reference.entity().getItemBySlot(ARMOR_SLOTS.get(parent))))) {
                AccessoriesContainer container = accessoriesCap.getContainer(new SlotTypeReference(parent));

                if (container == null)
                    continue;

                ExpandedSimpleContainer accessories = container.getAccessories();
                boolean satisfied = false;

                for (int i = 0; i < accessories.getContainerSize(); i++)
                    if (recipe.getWearable().test(accessories.getItem(i))) {
                        satisfied = true;
                        break;
                    }

                if (!satisfied)
                    continue;
            }

            AccessoryRenderer.transformToModelPart(poseStack, switch (recipe.getBodyPart()) {
                case HEAD -> humanoid.head;
                case BODY -> humanoid.body;
                case LEFT_ARM -> humanoid.leftArm;
                case RIGHT_ARM -> humanoid.rightArm;
                case LEFT_LEG -> humanoid.leftLeg;
                case RIGHT_LEG -> humanoid.rightLeg;
            });
            recipe.getScale().ifPresent(scale ->
                poseStack.scale(scale, scale, scale)
            );
            recipe.getTranslate().ifPresent(translate ->
                poseStack.translate(translate.x, translate.y, translate.z)
            );
            recipe.getRotation().ifPresent(rotation -> {
                if (rotation.x != 0)
                    poseStack.mulPose(Axis.XP.rotationDegrees(rotation.x));
                if (rotation.y != 0)
                    poseStack.mulPose(Axis.YP.rotationDegrees(rotation.y));
                if (rotation.z != 0)
                    poseStack.mulPose(Axis.ZP.rotationDegrees(rotation.z));
            });
        }
    }
}