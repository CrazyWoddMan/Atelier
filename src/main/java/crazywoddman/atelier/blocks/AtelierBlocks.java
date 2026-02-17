package crazywoddman.atelier.blocks;

import crazywoddman.atelier.Atelier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AtelierBlocks {
    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }

    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, Atelier.MODID);

    public static final RegistryObject<Block> SEWING_TABLE = REGISTRY.register("sewing_table", () -> new SewingTable(Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> WILD_COTTON = REGISTRY.register("wild_cotton", () -> new BushBlock(
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .replaceable()
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS)
            .offsetType(BlockBehaviour.OffsetType.XYZ)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
    ));
    public static final RegistryObject<Block> COTTON = REGISTRY.register("cotton", () -> new CropBlock(
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollission()
            .randomTicks()
            .instabreak()
            .sound(SoundType.CROP)
            .pushReaction(PushReaction.DESTROY)
    ));
}
