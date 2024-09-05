package net.hackermdch.fantasy.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("NullableProblems")
public class VoidChunkGenerator extends ChunkGenerator {
    public static final MapCodec<VoidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Biome.CODEC.stable().fieldOf("biome").forGetter(VoidChunkGenerator::getBiome)).apply(instance, instance.stable(VoidChunkGenerator::new)));

    private static final NoiseColumn EMPTY_SAMPLE = new NoiseColumn(0, new BlockState[0]);

    private final Holder<Biome> biome;

    public static final DensityFunction ZERO_DENSITY_FUNCTION = new DensityFunction() {
        public double compute(@NotNull DensityFunction.FunctionContext pos) {
            return 0.0;
        }

        public void fillArray(double @NotNull [] ds, @NotNull DensityFunction.ContextProvider arg) {
        }

        @NotNull
        public DensityFunction mapAll(@NotNull DensityFunction.Visitor visitor) {
            return this;
        }

        public double minValue() {
            return 0.0;
        }

        public double maxValue() {
            return 0.0;
        }

        @NotNull
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return KeyDispatchDataCodec.of(MapCodec.unit(this));
        }
    };

    public static final Climate.Sampler EMPTY_SAMPLER = new Climate.Sampler(ZERO_DENSITY_FUNCTION, ZERO_DENSITY_FUNCTION, ZERO_DENSITY_FUNCTION, ZERO_DENSITY_FUNCTION, ZERO_DENSITY_FUNCTION, ZERO_DENSITY_FUNCTION, Collections.emptyList());

    public VoidChunkGenerator(Holder<Biome> biome) {
        super(new FixedBiomeSource(biome));
        this.biome = biome;
    }

    @Deprecated
    public VoidChunkGenerator(Supplier<Biome> biome) {
        this(Holder.direct(biome.get()));
    }

    public VoidChunkGenerator(Registry<Biome> biomeRegistry) {
        this(biomeRegistry, Biomes.THE_VOID);
    }

    public VoidChunkGenerator(Registry<Biome> biomeRegistry, ResourceKey<Biome> biome) {
        this(biomeRegistry.getHolder(biome).orElseThrow());
    }

    // Create an empty (void) world!
    public VoidChunkGenerator(MinecraftServer server) {
        this(server.registryAccess().registryOrThrow(Registries.BIOME), Biomes.THE_VOID);
    }

    // Create a world with a given Biome (as an ID)
    public VoidChunkGenerator(MinecraftServer server, ResourceLocation biome) {
        this(server, ResourceKey.create(Registries.BIOME, biome));
    }

    // Create a world with a given Biome (as a RegistryKey)
    public VoidChunkGenerator(MinecraftServer server, ResourceKey<Biome> biome) {
        this(server.registryAccess().registryOrThrow(Registries.BIOME), biome);
    }

    @Override
    @NotNull
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    protected Holder<Biome> getBiome() {
        return this.biome;
    }

    @Override
    public void applyCarvers(WorldGenRegion chunkRegion, long seed, RandomState noiseConfig, BiomeManager world, StructureManager structureAccessor, ChunkAccess chunk, GenerationStep.Carving carverStep) {
    }

    @Override
    public void createReferences(WorldGenLevel world, StructureManager accessor, ChunkAccess chunk) {
    }

    @Override
    @NotNull
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState noiseConfig, StructureManager structureAccessor, ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState noiseConfig) {
        return 0;
    }

    @Override
    @NotNull
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState noiseConfig) {
        return EMPTY_SAMPLE;
    }

    @Override
    public void addDebugScreenInfo(List<String> text, RandomState noiseConfig, BlockPos pos) {
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureAccessor) {
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState noiseConfig, ChunkAccess chunk) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
    }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public @Nullable Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel world, HolderSet<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures) {
        return null;
    }

    @Override
    @NotNull
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> biome, StructureManager accessor, MobCategory group, BlockPos pos) {
        return WeightedRandomList.create();
    }

    @Override
    public void createStructures(RegistryAccess registryManager, ChunkGeneratorStructureState placementCalculator, StructureManager structureAccessor, ChunkAccess chunk, StructureTemplateManager structureTemplateManager) {
    }

    @Override
    @NotNull
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> structureSetRegistry, RandomState noiseConfig, long seed) {
        return ChunkGeneratorStructureState.createForFlat(noiseConfig, seed, this.biomeSource, Stream.empty());
    }
}
