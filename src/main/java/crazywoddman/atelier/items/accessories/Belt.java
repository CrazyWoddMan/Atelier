package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.EquipSound;
import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class Belt extends DyableAccessory {

    public Belt() {
        super(DEFAULT_LEATHER_COLOR, GOLD);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(32, partdefinition -> 
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 10.0F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.02F)), PartPose.ZERO)
        );
    }

    @Override
    public EquipSound equipSound() {
        return EquipSound.CHAIN;
    }
}