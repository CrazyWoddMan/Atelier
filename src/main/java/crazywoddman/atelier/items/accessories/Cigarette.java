package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import org.joml.Vector3f;

import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.SimpleSlot;
import crazywoddman.atelier.api.interfaces.IAccessoryRenderer;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.SimpleItem;
import crazywoddman.atelier.client.ClientUtils;
import crazywoddman.atelier.client.renderers.CigaretteRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Cigarette extends SimpleItem implements IWearableAccessory {
    private static final DustParticleOptions SMOKE = new DustParticleOptions(new Vector3f(0.2f), 0.5f);

    @Override
    public Supplier<IAccessoryRenderer> getRenderer() {
        return CigaretteRenderer::new;
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(8, partdefinition -> 
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F))
		    .addOrReplaceChild("cigarette_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.25F, -0.25F, 0)), PartPose.offsetAndRotation(0.5F, -1.0F, -4.9F, 0.0F, -0.3927F, 0.0F))
        );
    }

    @Override
    public EquipSound equipSound() {
        return EquipSound.GENERIC;
    }

    @Override
    public void wearTick(ItemStack stack, LivingEntity entity, SimpleSlot slot) {
        Level level = entity.level();

        if (!level.isClientSide || entity.tickCount % 3 != 0 || entity.getPose() != Pose.STANDING || ClientUtils.isFirstPerson(entity))
            return;

        Vec3 lookAngle = entity.getLookAngle();

        Vec3 pos = entity.getEyePosition(1.0f)
            .add(lookAngle.scale(0.3 + entity.getXRot() / 900 + 0.1))
            .add(new Vec3(-lookAngle.z, 0, lookAngle.x).normalize().scale(-0.1))
            .add(0, -0.1, 0);
        
        level.addParticle(
            SMOKE,
            pos.x, pos.y, pos.z,
            0, 2, 0
        );
    }
}