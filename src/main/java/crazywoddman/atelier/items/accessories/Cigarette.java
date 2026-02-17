package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.interfaces.IWearableAccessory;
import io.wispforest.accessories.api.SoundEventData;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;

public class Cigarette extends Item implements IWearableAccessory {
    public Cigarette() {
        super(new Properties());
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(8, 8, partdefinition -> 
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F))
		    .addOrReplaceChild("cigarette_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.25F, -0.25F, 0)), PartPose.offsetAndRotation(0.5F, -1.0F, -4.9F, 0.0F, -0.3927F, 0.0F))
        );
    }

    @Override
    public SoundEventData getEquipSound() {
        return new SoundEventData(SoundEvents.ARMOR_EQUIP_GENERIC, 1, 1);
    }
}