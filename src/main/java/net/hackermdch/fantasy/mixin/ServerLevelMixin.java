package net.hackermdch.fantasy.mixin;

import net.hackermdch.fantasy.FantasyLevelAccess;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements FantasyLevelAccess {
    @Unique
    private static final int TICK_TIMEOUT = 20 * 15;
    @Unique
    private boolean fantasy$tickWhenEmpty = true;
    @Unique
    private int fantasy$tickTimeout;

    @Override
    public void fantasy$setTickWhenEmpty(boolean tickWhenEmpty) {
        this.fantasy$tickWhenEmpty = tickWhenEmpty;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        boolean shouldTick = fantasy$tickWhenEmpty || fantasy$noEmpty();
        if (shouldTick) {
            this.fantasy$tickTimeout = TICK_TIMEOUT;
        } else if (this.fantasy$tickTimeout-- <= 0) {
            ci.cancel();
        }
    }

    @Override
    public boolean fantasy$shouldTick() {
        boolean shouldTick = fantasy$tickWhenEmpty || fantasy$noEmpty();
        return shouldTick || fantasy$tickTimeout > 0;
    }

    @Unique
    private boolean fantasy$noEmpty() {
        return !players.isEmpty() || chunkSource.getLoadedChunksCount() > 0;
    }

    @Shadow
    @Final
    List<ServerPlayer> players;

    @Shadow
    @Final
    private ServerChunkCache chunkSource;
}
