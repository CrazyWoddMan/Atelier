package crazywoddman.atelier.compat.terrablender;

import com.mojang.datafixers.util.Pair;
import crazywoddman.atelier.Atelier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;

import java.util.function.Consumer;

public class CottonField extends Region {
    private static final ResourceKey<Biome> COTTON_FIELD = ResourceKey.create(
        Registries.BIOME,
        ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "cotton_field")
    );

    public static void register() {
        Regions.register(new CottonFieldChanceNerf(
            ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "cotton_field_nerf"),
            10
        ));
        
        Regions.register(new CottonField(
            ResourceLocation.fromNamespaceAndPath(Atelier.MODID, "cotton_field"),
            1
        ));
    }

    private CottonField(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        addModifiedVanillaOverworldBiomes(mapper, builder ->     
            builder.replaceBiome(Biomes.SAVANNA, COTTON_FIELD)
        );
    }
    
    private static class CottonFieldChanceNerf extends Region {
        private CottonFieldChanceNerf(ResourceLocation name, int weight) {
            super(name, RegionType.OVERWORLD, weight);
        }

        @Override
        public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
            addModifiedVanillaOverworldBiomes(mapper, builder ->     
                builder.replaceBiome(COTTON_FIELD, Biomes.SAVANNA)
            );
        }
    }
}