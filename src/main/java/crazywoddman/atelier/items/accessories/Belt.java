package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import io.wispforest.accessories.api.SoundEventData;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.sounds.SoundEvents;

public class Belt extends DyableAccessory {

    public Belt() {
        super(new Properties().stacksTo(1), 8606770);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return HumanoidModelHelper.createLayer(32, 32, partdefinition -> 
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 10.0F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.02F))
            .texOffs(0, 0).addBox(3.75F, 10.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F, 0.1F, 0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F))
            .addOrReplaceChild("belt_r1", CubeListBuilder.create().texOffs(23, 0).addBox(2.5F, -2.0F, -1.0F, 0.0F, 2.0F, 3.0F), PartPose.offsetAndRotation(2.25F, 12.0F, 0.75F, 0.0F, 0.2094F, 0.0F))
        );
    }

    @Override
    public SoundEventData getEquipSound() {
        return new SoundEventData(SoundEvents.ARMOR_EQUIP_CHAIN, 1, 1);
    }
}