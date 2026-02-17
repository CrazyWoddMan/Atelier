package crazywoddman.atelier.items.templates;

import java.util.Map;
import java.util.function.Supplier;

import crazywoddman.atelier.api.SimpleWearableRenderer;
import crazywoddman.atelier.api.interfaces.IModular;
import crazywoddman.atelier.api.templates.DyableAccessory;
import crazywoddman.atelier.effects.AtelierEffects;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class GasMaskItem extends DyableAccessory implements IModular {
    protected final int filters;

    public GasMaskItem(Properties properties, int filters) {
        super(properties.stacksTo(1), 8606770);
        this.filters = filters;
    }

    @Override
    public Map<String, Integer> getModules() {
        return Map.of("gas_filter", filters);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        super.onUnequip(stack, reference);
        LivingEntity entity = reference.entity();

        if (entity instanceof Player)
            entity.removeEffect(AtelierEffects.FILTER_PROTECTED.get());
    }

    @Override
    public Supplier<AccessoryRenderer> getRenderer() {
        return () -> new SimpleWearableRenderer(
            getTextureKey(),
            ref -> true,
            RenderType::armorCutoutNoCull,
            RenderType::entityTranslucent
        );
    }
}