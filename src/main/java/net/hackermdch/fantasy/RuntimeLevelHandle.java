package net.hackermdch.fantasy;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class RuntimeLevelHandle {
    private final Api fantasy;
    private final ServerLevel world;

    RuntimeLevelHandle(Api fantasy, ServerLevel world) {
        this.fantasy = fantasy;
        this.world = world;
    }

    public void setTickWhenEmpty(boolean tickWhenEmpty) {
        ((FantasyLevelAccess) this.world).fantasy$setTickWhenEmpty(tickWhenEmpty);
    }

    /**
     * Deletes the world, including all stored files
     */
    public void delete() {
        fantasy.enqueueWorldDeletion(world);
    }

    /**
     * Unloads the world. It only deletes the files if world is temporary.
     */
    public void unload() {
        if (world instanceof RuntimeLevel level && level.style == RuntimeLevel.Style.TEMPORARY) {
            fantasy.enqueueWorldDeletion(world);
        } else {
            fantasy.enqueueWorldUnloading(world);
        }
    }

    public ServerLevel asLevel() {
        return world;
    }

    public ResourceKey<Level> getResourceKey() {
        return this.world.dimension();
    }
}
