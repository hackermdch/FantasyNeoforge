package net.hackermdch.fantasy;

import com.google.common.collect.ImmutableList;
import net.hackermdch.fantasy.util.VoidChunkProgressListener;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executor;

public class RuntimeLevel extends ServerLevel {
    public final Style style;
    private boolean flat;

    protected RuntimeLevel(MinecraftServer server, ResourceKey<Level> resourceKey, RuntimeLevelConfig config, Style style) {
        super(server, Util.backgroundExecutor(), server.storageSource, new RuntimeLevelProperties(server.getWorldData(), config), resourceKey, config.createDimensionOptions(server), VoidChunkProgressListener.INSTANCE, false, BiomeManager.obfuscateSeed(config.getSeed()), ImmutableList.of(), config.shouldTickTime(), null);
        this.style = style;
        this.flat = config.isFlat() == TriState.DEFAULT ? super.isFlat() : config.isFlat().isTrue();
    }

    protected RuntimeLevel(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess session, ServerLevelData properties, ResourceKey<Level> worldKey, LevelStem dimensionOptions, ChunkProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<CustomSpawner> spawners, boolean shouldTickTime, @Nullable RandomSequences randomSequencesState, Style style) {
        super(server, workerExecutor, session, properties, worldKey, dimensionOptions, worldGenerationProgressListener, debugWorld, seed, spawners, shouldTickTime, randomSequencesState);
        this.style = style;
    }

    @Override
    public long getSeed() {
        return ((RuntimeLevelProperties) levelData).config.getSeed();
    }

    @Override
    public void save(@Nullable ProgressListener progressListener, boolean flush, boolean enabled) {
        if (style == Style.PERSISTENT || !flush) {
            super.save(progressListener, flush, enabled);
        }
    }

    /**
     * Only use the time update code from super as the immutable world proerties runtime dimensions breaks scheduled functions
     */
    @Override
    protected void tickTime() {
        if (tickTime && levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            setDayTime(levelData.getDayTime() + 1L);
        }
    }

    @Override
    public boolean isFlat() {
        return this.flat;
    }

    public enum Style {
        PERSISTENT, TEMPORARY
    }

    public interface Constructor {
        RuntimeLevel createWorld(MinecraftServer server, ResourceKey<Level> registryKey, RuntimeLevelConfig config, Style style);
    }
}
