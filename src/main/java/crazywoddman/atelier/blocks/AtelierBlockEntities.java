package crazywoddman.atelier.blocks;

import java.util.Arrays;

import crazywoddman.atelier.Atelier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    @SafeVarargs
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(
        String registryname,
        BlockEntitySupplier<T> supplier,
        RegistryObject<Block>... blocks
    ) {
        return REGISTRY.register(
            registryname,
            () -> Builder.of(supplier, Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new)).build(null)
        );
    }

    private static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Atelier.MODID);
    
    public static final RegistryObject<BlockEntityType<SewingTableBlockEntity>> SEWING_TABLE = register(
        AtelierBlocks.SEWING_TABLE.getId().getPath(),
        SewingTableBlockEntity::new,
        AtelierBlocks.SEWING_TABLE
    );
}
