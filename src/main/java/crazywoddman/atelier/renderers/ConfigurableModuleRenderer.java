package crazywoddman.atelier.renderers;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import crazywoddman.atelier.api.ModulesDataProvider;
import crazywoddman.atelier.api.SimpleWearableRenderer;
import crazywoddman.atelier.api.ModulesDataProvider.ModulesData.ParentReference;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.recipes.AtelierRecipes;
import crazywoddman.atelier.recipes.ConfigurableModule.Variant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;

public class ConfigurableModuleRenderer extends SimpleWearableRenderer implements SimpleAccessoryRenderer {
    protected boolean renderItem;
    protected int localIndex;
    protected String parentSlot;
    protected byte parentIndex;
    protected Item parent;

    public ConfigurableModuleRenderer(ResourceLocation modelID) {
        super(modelID, ref -> true);
        this.followBody = false;
    }

    public ConfigurableModuleRenderer() {}

    @Override
    public <M extends LivingEntity> void render(
        ItemStack stack,
        SlotReference reference,
        PoseStack matrices,
        EntityModel<M> model,
        MultiBufferSource multiBufferSource,
        int light,
        float limbSwing, float limbSwingAmount,
        float partialTicks, float ageInTicks,
        float netHeadYaw, float headPitch
    ) {
        LivingEntity entity = reference.entity();
        List<ParentReference> slots = ModulesDataProvider.get(entity).get(reference.slotName());
        int index = reference.slot();

        if (slots.size() <= index)
            return;

        ParentReference parentReference = slots.get(index);
        this.localIndex = index - slots.indexOf(parentReference);
        SlotReference parentSlot = parentReference.accessoriesSlot();
        this.parentSlot = parentSlot == null ? "" : parentSlot.slotName();
        this.parentIndex = (byte)(parentSlot == null ? -1 : parentSlot.slot());
        this.parent = (parentSlot == null ? entity.getItemBySlot(parentReference.equipmentSlot()) : reference.capability().getContainer(new SlotTypeReference(parentSlot.slotName())).getAccessories().getItem(parentSlot.slot())).getItem();

        if ((parentSlot != null && !parentSlot.slotContainer().shouldRender(this.parentIndex)) || (this.parentSlot.equals("hat") && parent instanceof IWearableAccessory accessory && accessory.hideUnderHelmet() && !entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()))
            return;

        if (this.followBody)
            SimpleAccessoryRenderer.super.render(
                stack,
                reference,
                matrices,
                model,
                multiBufferSource,
                light,
                limbSwing, limbSwingAmount,
                partialTicks, ageInTicks,
                netHeadYaw, headPitch
            );
        else
            super.render(
                stack,
                reference,
                matrices,
                model,
                multiBufferSource,
                light,
                limbSwing, limbSwingAmount,
                partialTicks, ageInTicks,
                netHeadYaw, headPitch
            );
    }

    @Override
    public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, PoseStack poseStack) {
        AtelierRecipes.getConfigurableModule(reference.slotName(), this.parent, this.parentSlot, this.parentIndex).ifPresent(recipe -> {
            Variant variant = recipe.variants[this.localIndex];
            HumanoidModel<?> humanoid = (HumanoidModel<?>)model;

            AccessoryRenderer.transformToModelPart(poseStack, switch (variant.bodyPart()) {
                case HEAD -> humanoid.head;
                case BODY -> humanoid.body;
                case LEFT_ARM -> humanoid.leftArm;
                case RIGHT_ARM -> humanoid.rightArm;
                case LEFT_LEG -> humanoid.leftLeg;
                case RIGHT_LEG -> humanoid.rightLeg;
            });
            
            if (this.renderItem && reference.entity() instanceof ArmorStand)
                switch (variant.bodyPart()) {
                    case HEAD -> poseStack.translate(0, 0.05, 0);
                    case BODY -> poseStack.translate(0, -0.56, 0);
                    case LEFT_ARM, RIGHT_ARM -> poseStack.translate(0, -0.56, 0);
                    case LEFT_LEG, RIGHT_LEG -> poseStack.translate(0, -0.56, 0);
            }

            variant.getTranslate().ifPresent(translate ->
                poseStack.translate(translate.x, translate.y, translate.z)
            );
            variant.getRotation().ifPresent(rotation -> {
                if (rotation.x != 0)
                    poseStack.mulPose(Axis.XP.rotationDegrees(rotation.x));
                if (rotation.y != 0)
                    poseStack.mulPose(Axis.YP.rotationDegrees(rotation.y));
                if (rotation.z != 0)
                    poseStack.mulPose(Axis.ZP.rotationDegrees(rotation.z));
            });
            variant.getScale().ifPresent(scale ->
                poseStack.scale(scale, scale, scale)
            );
        });
    }
}