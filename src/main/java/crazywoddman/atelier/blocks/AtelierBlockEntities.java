package crazywoddman.atelier.blocks;

import crazywoddman.atelier.Atelier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AtelierBlockEntities {
    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Atelier.MODID);
    
    public static final RegistryObject<BlockEntityType<?>> SEWING_TABLE = register(AtelierBlocks.SEWING_TABLE.getId().getPath(), AtelierBlocks.SEWING_TABLE, SewingTableBlockEntity::new);

    private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntitySupplier<?> supplier) {
        return REGISTRY.register(registryname, () -> Builder.of(supplier, new Block[]{block.get()}).build(null));
    }
}
