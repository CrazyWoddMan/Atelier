package crazywoddman.atelier.api.templates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.api.interfaces.IWearable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DyableArmor extends ArmorItem implements IWearable, IDyable {
    private final int defaultColor;
    private final Supplier<LayerDefinition> layerSupplier;
    private final ModelLayerLocation layerLocation;
    private final String textureLocation;
    private final String overlayLocation;

    public DyableArmor(ArmorMaterial material, Type type, Properties properties, int defaultColor, Supplier<LayerDefinition> layerSupplier, ModelLayerLocation layerLocation, String textureLocation) {
        super(material, type, properties);
        this.defaultColor = defaultColor;
        this.layerSupplier = layerSupplier;
        this.layerLocation = layerLocation;
        this.textureLocation = textureLocation;
        this.overlayLocation = Atelier.MODID + ":textures/empty.png";
    }

    public DyableArmor(ArmorMaterial material, Type type, Properties properties, int defaultColor, Supplier<LayerDefinition> layerSupplier, ModelLayerLocation layerLocation, String textureLocation, String overlayLocation) {
        super(material, type, properties);
        this.defaultColor = defaultColor;
        this.layerSupplier = layerSupplier;
        this.layerLocation = layerLocation;
        this.textureLocation = textureLocation;
        this.overlayLocation = overlayLocation;
    }

    @Override
    public Supplier<LayerDefinition> createLayer() {
        return layerSupplier;
    }

    @Override
    public ModelLayerLocation getLayerLocation() {
        return layerLocation;
    }

    @Override
    public int getDefaultColor() {
        return this.defaultColor;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return type == null ? textureLocation : overlayLocation;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                HumanoidModel<?> armorModel = new HumanoidModel<>(
                    Minecraft
                    .getInstance()
                    .getEntityModels()
                    .bakeLayer(getLayerLocation())
                );
                armorModel.crouching = living.isShiftKeyDown();
                armorModel.riding = original.riding;
                armorModel.young = living.isBaby();

                return armorModel;
            }
        });
    }
}