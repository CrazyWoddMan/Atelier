package crazywoddman.atelier.api.templates;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.api.HumanoidModelHelper;
import crazywoddman.atelier.api.interfaces.IDyable;
import crazywoddman.atelier.api.interfaces.IWearable;

import java.util.function.Consumer;

public abstract class DyableArmor extends ArmorItem implements IWearable, IDyable {
    private final int defaultColor;
    private String textureLocation;
    private String overlayLocation;
    private HumanoidModel<LivingEntity> model;
    private boolean init;

    public DyableArmor(ArmorMaterial material, Type type, Properties properties, int defaultColor) {
        super(material, type, properties);
        this.defaultColor = defaultColor;
    }

    public DyableArmor(ArmorMaterial material, Type type, Properties properties, int defaultColor, ResourceLocation textureLocation) {
        this(material, type, properties, defaultColor);
        this.textureLocation = textureLocation.toString();
        this.overlayLocation = IWearable.getOverlayTexture(textureLocation).map(ResourceLocation::toString).orElse(Atelier.MODID + ":textures/empty.png");
        this.init = true;
    }

    @Override
    public int getDefaultColor() {
        return this.defaultColor;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (!init) {
            ResourceLocation key = getTextureKey();
            this.textureLocation = IWearable.getModelTexture(key).toString();
            this.overlayLocation = IWearable.getOverlayTexture(key).map(ResourceLocation::toString).orElse(Atelier.MODID + ":textures/empty.png");
        }
        return type == null ? textureLocation : overlayLocation;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (model == null)
                    model = HumanoidModelHelper.bake(getModelKey());

                model.crouching = living.isShiftKeyDown();
                model.riding = original.riding;
                model.young = living.isBaby();

                return model;
            }
        });
    }
}