package crazywoddman.atelier.items.accessories;

import java.util.function.Supplier;

import crazywoddman.atelier.api.render.AtelierRenderHelper;
import crazywoddman.atelier.api.templates.DyableAccessory;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CowboyHat extends DyableAccessory {
    
    public CowboyHat() {
        super(DEFAULT_LEATHER_COLOR, DARK_GRAY);
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return AtelierRenderHelper.createLayer(64, partdefinition -> {
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
            head.addOrReplaceChild("cowboy_hat_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -11.0F, -5.0F, 12.0F, 5.0F, 10.0F, new CubeDeformation(0.7F)), PartPose.offsetAndRotation(0.0F, 0.25F, -0.4F, -0.0436F, 0.0F, 0.0F));
            head.addOrReplaceChild("cowboy_hat_r2", CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.7F)), PartPose.offsetAndRotation(0.0F, 0.25F, -0.3F, -0.0436F, 0.0F, 0.0F));
        });
    }
}