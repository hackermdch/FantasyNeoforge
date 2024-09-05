package net.hackermdch.fantasy.util;

import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class VoidChunkProgressListener implements ChunkProgressListener {
    public static final VoidChunkProgressListener INSTANCE = new VoidChunkProgressListener();

    private VoidChunkProgressListener() {
    }

    @Override
    public void updateSpawnPos(@NotNull ChunkPos center) {
    }

    @Override
    public void onStatusChange(@NotNull ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
