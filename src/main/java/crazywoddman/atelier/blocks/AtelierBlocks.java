package crazywoddman.atelier.blocks;

import crazywoddman.atelier.Atelier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AtelierBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, Atelier.MODID);

    public static final RegistryObject<Block> SEWING_TABLE = REGISTRY.register("sewing_table", () -> new SewingTable(Properties.copy(Blocks.OAK_PLANKS)));
}
