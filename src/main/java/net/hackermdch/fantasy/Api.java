package net.hackermdch.fantasy;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Api {
    private static Api instance;
    private final MinecraftServer server;
    private final RuntimeLevelManager worldManager;
    private final Set<ServerLevel> deletionQueue = new ReferenceOpenHashSet<>();
    private final Set<ServerLevel> unloadingQueue = new ReferenceOpenHashSet<>();

    private Api(MinecraftServer server) {
        this.server = server;
        worldManager = new RuntimeLevelManager(server);
    }

    /**
     * Gets the {@link Api} instance for the given server instance.
     *
     * @param server the server to work with
     * @return the {@link Api} instance to work with runtime dimensions
     */
    public static Api get(MinecraftServer server) {
        Preconditions.checkState(server.isSameThread(), "cannot create worlds from off-thread!");
        if (instance == null || instance.server != server) {
            instance = new Api(server);
        }
        return instance;
    }

    private void tick() {
        if (!deletionQueue.isEmpty()) deletionQueue.removeIf(this::tickDeleteWorld);
        if (!unloadingQueue.isEmpty()) unloadingQueue.removeIf(this::tickUnloadWorld);
    }

    public boolean tickDeleteWorld(ServerLevel world) {
        if (isWorldUnloaded(world)) {
            worldManager.delete(world);
            return true;
        } else {
            kickPlayers(world);
            return false;
        }
    }

    public boolean tickUnloadWorld(ServerLevel world) {
        if (isWorldUnloaded(world)) {
            worldManager.unload(world);
            return true;
        } else {
            kickPlayers(world);
            return false;
        }
    }

    private void kickPlayers(ServerLevel world) {
        if (world.players().isEmpty()) return;
        var overworld = server.overworld();
        var spawnPos = overworld.getSharedSpawnPos();
        var spawnAngle = overworld.getSharedSpawnAngle();
        for (var player : ImmutableList.copyOf(world.players())) {
            player.teleportTo(overworld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, spawnAngle, 0.0F);
        }
    }

    private boolean isWorldUnloaded(ServerLevel world) {
        return world.players().isEmpty() && world.getChunkSource().getLoadedChunksCount() <= 0;
    }

    private List<RuntimeLevel> collectTemporaryWorlds() {
        var tmp = new ArrayList<RuntimeLevel>();
        for (var world : server.getAllLevels()) {
            if (world instanceof RuntimeLevel level) {
                if (level.style == RuntimeLevel.Style.TEMPORARY) {
                    tmp.add(level);
                }
            }
        }
        return tmp;
    }

    private void onServerStopping() {
        for (var level : collectTemporaryWorlds()) {
            kickPlayers(level);
            worldManager.delete(level);
        }
    }

    private RuntimeLevel addPersistentWorld(ResourceLocation key, RuntimeLevelConfig config) {
        return worldManager.add(ResourceKey.create(Registries.DIMENSION, key), config, RuntimeLevel.Style.PERSISTENT);
    }

    private RuntimeLevel addTemporaryWorld(ResourceLocation key, RuntimeLevelConfig config) {
        var levelKey = ResourceKey.create(Registries.DIMENSION, key);
        try {
            FileUtils.forceDeleteOnExit(server.storageSource.getDimensionPath(levelKey).toFile());
        } catch (IOException ignored) {
        }
        return this.worldManager.add(levelKey, config, RuntimeLevel.Style.TEMPORARY);
    }

    void enqueueWorldDeletion(ServerLevel world) {
        var ignore = server.submit(() -> {
            deletionQueue.add(world);
        });
    }

    void enqueueWorldUnloading(ServerLevel world) {
        var ignore = server.submit(() -> {
            unloadingQueue.add(world);
        });
    }

    public RuntimeLevelHandle openTemporaryWorld(ResourceLocation key, RuntimeLevelConfig config) {
        return new RuntimeLevelHandle(this, addTemporaryWorld(key, config));
    }

    public RuntimeLevelHandle openTemporaryWorld(String namespace, RuntimeLevelConfig config) {
        return this.openTemporaryWorld(generateTemporaryWorldKey(namespace), config);
    }

    public RuntimeLevelHandle getOrOpenPersistentWorld(ResourceLocation key, RuntimeLevelConfig config) {
        var worldKey = ResourceKey.create(Registries.DIMENSION, key);
        var level = server.getLevel(worldKey);
        if (level == null) level = addPersistentWorld(key, config);
        else deletionQueue.remove(level);
        return new RuntimeLevelHandle(this, level);
    }

    private static ResourceLocation generateTemporaryWorldKey(String namespace) {
        return ResourceLocation.fromNamespaceAndPath(namespace, RandomStringUtils.random(16, "abcdefghijklmnopqrstuvwxyz0123456789"));
    }

    @EventBusSubscriber(modid = Fantasy.ID)
    private static final class EventHandler {
        @SubscribeEvent
        static void onServerTick(ServerTickEvent.Pre event) {
            Api.get(event.getServer()).tick();
        }

        @SubscribeEvent
        static void onServerStopping(ServerStoppingEvent event) {
            Api.get(event.getServer()).onServerStopping();
        }
    }
}
