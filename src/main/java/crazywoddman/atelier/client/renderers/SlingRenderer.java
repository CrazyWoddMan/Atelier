package crazywoddman.atelier.client.renderers;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.CompatHelper;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IQuickAccess;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.api.render.SimpleAccessoryRenderer;
import crazywoddman.atelier.client.WearableTexture;
import crazywoddman.atelier.items.AtelierItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class SlingRenderer extends SimpleAccessoryRenderer {
    private static final SimpleSlot
    BODY = SimpleSlot.of(IWearableAccessory.BODY, 0),
    SUIT = SimpleSlot.of(IWearableAccessory.SUIT, 0);
    private static final Map<Item, float[]> OFFSETS;
    private static final float[] DEFAULT_OFFSET = {-0.3f, 0.05f, 0.05f};

    public SlingRenderer() {
        super(new WearableTexture(AtelierItems.SLING.get()), AtelierItems.SLING.getId());
    }

    @Override
    public <M extends LivingEntity> void render(
        ItemStack stack,
        LivingEntity entity,
        SimpleSlot slot,
        PoseStack pose,
        EntityModel<M> model,
        MultiBufferSource buffer,
        int light
    ) {
        followBodyRotations(entity, this.model);
        boolean inflate = !entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
            || (!CompatHelper.getStackInSlot(entity, BODY).map(ItemStack::isEmpty).orElse(true) && CompatHelper.shouldRender(entity, BODY))
            || (!CompatHelper.getStackInSlot(entity, SUIT).map(ItemStack::isEmpty).orElse(true) && CompatHelper.shouldRender(entity, SUIT));
        pose.pushPose();

        if (inflate)
            this.model.body.zScale = 1.31f;

        for (int i = 0; i < 2; i++) {
            render(
                pose,
                light,
                buffer,
                RenderType.armorCutoutNoCull(texture.get(i)),
                stack,
                i
            );
        }

        pose.popPose();
        this.model.body.zScale = 1;
        
        if (!(model instanceof HumanoidModel<?> humanoid) || !(stack.getItem() instanceof IQuickAccess accessible))
            return;

        ItemStack rifle = accessible.quickAccessPreview(stack);

        if (rifle.isEmpty())
            return;

        if (inflate)
            pose.translate(0, 0, 0.05f);

        humanoid.body.translateAndRotate(pose);
        float[] offset = OFFSETS.getOrDefault(rifle.getItem(), DEFAULT_OFFSET);
        pose.translate(offset[0], offset[1], offset[2]);
        pose.mulPose(Axis.YP.rotationDegrees(90));
        pose.mulPose(Axis.XP.rotationDegrees(125));
        Minecraft.getInstance().getItemRenderer().renderStatic(
            rifle,
            ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
            light,
            OverlayTexture.NO_OVERLAY,
            pose,
            buffer,
            entity.level(),
            0
        );
    }

    static {
        if (Atelier.WARIUM_LOADED) {
            float[] peeler = new float[]{-0.1f, 0.25f, 0.15f};
            OFFSETS = Map.of(
                itemOf("armor_peeler_animated"), peeler,
                itemOf("armor_peeler_unloaded"), peeler,
                itemOf("lmg_animated"), new float[]{-0.35f, -0.15f, 0.12f}
            );
        } else {
            OFFSETS = Map.of();
        }
    }

    private static Item itemOf(String id) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("crusty_chunks", id));
    }
}