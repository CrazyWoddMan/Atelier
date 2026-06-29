package crazywoddman.atelier.client;

import java.util.Optional;

import crazywoddman.atelier.api.interfaces.IDyeable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

/**Path to file or directory with wearable UV texture(s)*/
public final class WearableTexture {
    public static final String WEARABLES_PATH = "textures/wearable/";
    private static final byte OVERLAY_UNKNOWN = -1;
    private static final byte NO_OVERLAY = 0;
    private static final byte HAS_OVERLAY = 1;
    private final ResourceLocation path;
    private byte overlay = OVERLAY_UNKNOWN;
    
    /**
     * @param id must be relative to {@code textures/wearable/}, e.g. {@code atelier:some_wearable}
     */
    public WearableTexture(ResourceLocation id) {
        this.path = id.withPrefix(WEARABLES_PATH);
    }

    /**
     * @param namespace modid
     * @param path relative to {@code textures/wearable/}
     */
    public WearableTexture(String namespace, String path) {
        this(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
    
    public WearableTexture(Item item) {
        this(item instanceof IDyeable ? ForgeRegistries.ITEMS.getKey(item) : ForgeRegistries.ITEMS.getKey(item).withSuffix(".png"));
    }

    public ResourceLocation get(String suffix) {
        return this.path.withSuffix('/' + suffix + ".png");
    }

    public ResourceLocation get(int colorIndex) {
        return get(Integer.toString(colorIndex));
    }

    /** Only for non-dyeable items */
    public ResourceLocation get() {
        return this.path;
    }

    private ResourceLocation overlay() {
        return get("overlay");
    }

    // TODO: switch to world-enter initialization
    public Optional<ResourceLocation> getOverlay() {
        if (this.overlay == OVERLAY_UNKNOWN)
            this.overlay = Minecraft.getInstance().getResourceManager().getResource(overlay()).isPresent() ? HAS_OVERLAY : NO_OVERLAY;

        return this.overlay == HAS_OVERLAY ? Optional.of(overlay()) : Optional.empty();
    }
}