package crazywoddman.atelier.mixins;

import crazywoddman.atelier.Atelier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(GrassBlock.class)
public class GrassBlockMixin {
    private static final ResourceKey<Biome> COTTON_FIELD = ResourceKey.create(
        Registries.BIOME,
        ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "cotton_field")
    );

    @Redirect(
        method = "performBonemeal",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/BiomeGenerationSettings;getFlowerFeatures()Ljava/util/List;"
        )
    )
    private List<ConfiguredFeature<?, ?>> filterWildCotton(BiomeGenerationSettings settings, ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        return level.getBiome(pos).is(COTTON_FIELD) ? List.of() : settings.getFlowerFeatures();
    }
}