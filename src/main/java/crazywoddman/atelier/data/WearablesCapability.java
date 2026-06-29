package crazywoddman.atelier.data;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.AtelierConfig;
import crazywoddman.atelier.client.LocalPlayerVars;
import crazywoddman.atelier.items.templates.NightVisionDevice;

@AutoRegisterCapability
public class WearablesCapability {
    public static final Capability<WearablesCapability> WEARABLES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "wearables");
    public static final WearablesCapability EMPTY = new WearablesCapability(null);
    public enum EyePatch {RIGHT, LEFT}
    public static final byte
    DEFAULT_EYES_LEVEL = 4,
    EYE_PATCH_RIGHT = 0,
    EYE_PATCH_LEFT = 1;

    public final Player holder;
    public boolean nvdActive;
    public byte eyesLevel;
    public EyePatch eyePatch;

    private WearablesCapability(Player player) {
        this.holder = player;
        if (player != null && player.isLocalPlayer()) {
            this.eyesLevel = AtelierConfig.Client.EYES_LEVEL.get().byteValue();
            this.eyePatch = AtelierConfig.Client.EYE_PATCH.get();
        } else {
            this.eyesLevel = DEFAULT_EYES_LEVEL;
            this.eyePatch = EyePatch.RIGHT;
        }
    }

    public static WearablesCapability fromNetwork(WearableState state, FriendlyByteBuf buffer) {
        WearablesCapability capability = new WearablesCapability(null);
        state.fromNetwork.accept(capability, buffer);
        return capability;
    }

    public static LazyOptional<WearablesCapability> get(Entity entity) {
        return entity instanceof Player player ? player.getCapability(WEARABLES_CAPABILITY) : LazyOptional.empty();
    }

    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            Provider provider = new Provider(player);
            event.addCapability(ID, provider);

            if (player.isLocalPlayer())
                LocalPlayerVars.wearables = provider.capability;
        }
    }

    public enum WearableState {
        ALL((cap, buf) -> {for (int i = 1; i < WearableState.values().length; i++) WearableState.values()[i].toNetwork.accept(cap, buf);},
            (cap, buf) -> {for (int i = 1; i < WearableState.values().length; i++) WearableState.values()[i].fromNetwork.accept(cap, buf);},
            (from, to) -> {for (int i = 1; i < WearableState.values().length; i++) WearableState.values()[i].copy.accept(from, to);}

        ),
        NVD((cap, buf) -> buf.writeBoolean(cap.nvdActive),
            (cap, buf) -> cap.nvdActive = buf.readBoolean(),
            (from, to) -> NightVisionDevice.setActive(to, from.nvdActive)
        ),
        EYES_LEVEL(
            (cap, buf) -> buf.writeByte(cap.eyesLevel),
            (cap, buf) -> cap.eyesLevel = buf.readByte(),
            (from, to) -> to.eyesLevel = from.eyesLevel
        ),
        EYE_PATCH(
            (cap, buf) -> buf.writeByte(cap.eyePatch.ordinal()),
            (cap, buf) -> cap.eyePatch = EyePatch.values()[buf.readByte()],
            (from, to) -> to.eyePatch = from.eyePatch
        );

        private final BiConsumer<WearablesCapability, FriendlyByteBuf> toNetwork, fromNetwork;
        private final BiConsumer<WearablesCapability, WearablesCapability> copy;

        private WearableState(
            BiConsumer<WearablesCapability, FriendlyByteBuf> toNetwork,
            BiConsumer<WearablesCapability, FriendlyByteBuf> fromNetwork,
            BiConsumer<WearablesCapability, WearablesCapability> copy
        ) {
            this.toNetwork = toNetwork;
            this.fromNetwork = fromNetwork;
            this.copy = copy;
        }

        public void toNetwork(WearablesCapability capability, FriendlyByteBuf buffer) {
            buffer.writeByte(ordinal());
            this.toNetwork.accept(capability, buffer);
        }

        public void copy(WearablesCapability from, WearablesCapability to) {
            this.copy.accept(from, to);
        }

        public static Optional<WearableState> of(ItemStack stack) {
            return stack.is(AtelierTags.Items.NVD) ? Optional.of(WearableState.NVD) : Optional.empty();
        }

        public static WearableState fromNetwork(FriendlyByteBuf buffer) {
            return values()[buffer.readByte()];
        }
    }

    private static class Provider implements ICapabilityProvider {
        final WearablesCapability capability;
        private final LazyOptional<WearablesCapability> lazyOptional;

        Provider(Player player) {
            this.capability = new WearablesCapability(player);
            this.lazyOptional = LazyOptional.of(() -> this.capability);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            if (cap == WEARABLES_CAPABILITY)
                return lazyOptional.cast();

            return LazyOptional.empty();
        }
    }
}