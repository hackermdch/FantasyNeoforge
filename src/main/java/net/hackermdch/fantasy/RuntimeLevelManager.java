package net.hackermdch.fantasy;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

final class RuntimeLevelManager {
    private final MinecraftServer server;

    RuntimeLevelManager(MinecraftServer server) {
        this.server = server;
    }

    @SuppressWarnings("DataFlowIssue")
    RuntimeLevel add(ResourceKey<Level> resourceKey, RuntimeLevelConfig config, RuntimeLevel.Style style) {
        var options = config.createDimensionOptions(server);
        var ext = (FantasyLevelStem) (Object) options;
        if (style == RuntimeLevel.Style.TEMPORARY) {
            ext.fantasy$setSave(false);
        }
        ext.fantasy$setSaveProperties(false);
        var dimensionsRegistry = getDimensionsRegistry(server);
        var ext2 = (RemoveFromRegistry<?>) dimensionsRegistry;
        var isFrozen = ext2.fantasy$isFrozen();
        ext2.fantasy$setFrozen(false);
        var stem = ResourceKey.create(Registries.LEVEL_STEM, resourceKey.location());
        if (!dimensionsRegistry.containsKey(stem)) {
            dimensionsRegistry.register(stem, options, RegistrationInfo.BUILT_IN);
        }
        ext2.fantasy$setFrozen(isFrozen);
        var level = config.getWorldConstructor().createWorld(server, resourceKey, config, style);
        server.levels.put(level.dimension(), level);
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));
        // tick the level to ensure it is ready for use right away
        level.tick(() -> true);
        update();
        return level;
    }

    void delete(ServerLevel level) {
        var dimension = level.dimension();
        if (server.levels.remove(dimension, level)) {
            NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));
            var dimensionsRegistry = getDimensionsRegistry(server);
            RemoveFromRegistry.remove(dimensionsRegistry, dimension.location());
            var dir = server.storageSource.getDimensionPath(dimension).toFile();
            if (dir.exists()) {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException e) {
                    Fantasy.LOGGER.warn("Failed to delete level directory", e);
                    try {
                        FileUtils.forceDeleteOnExit(dir);
                    } catch (IOException ignored) {
                    }
                }
            }
            update();
        }
    }

    void unload(ServerLevel level) {
        var dimension = level.dimension();
        if (server.levels.remove(dimension, level)) {
            level.save(new ProgressListener() {
                @Override
                public void progressStartNoAbort(@NotNull Component title) {
                }

                @Override
                public void progressStart(@NotNull Component title) {
                }

                @Override
                public void progressStage(@NotNull Component task) {
                }

                @Override
                public void progressStagePercentage(int percentage) {
                }

                @Override
                public void stop() {
                }
            }, true, false);
            NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));
            RemoveFromRegistry.remove(getDimensionsRegistry(server), dimension.location());
            update();
        }
    }

    @SuppressWarnings("deprecation")
    private void update() {
        server.markWorldsDirty();
    }

    private static MappedRegistry<LevelStem> getDimensionsRegistry(MinecraftServer server) {
        return (MappedRegistry<LevelStem>) server.registries().compositeAccess().registryOrThrow(Registries.LEVEL_STEM);
    }
}
