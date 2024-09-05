package net.hackermdch.fantasy.mixin;

import net.hackermdch.fantasy.FantasyLevelAccess;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
    @Shadow
    @Final
    public ServerLevel level;

    @Inject(method = "pollTask", at = @At("HEAD"), cancellable = true)
    private void pollTask(CallbackInfoReturnable<Boolean> ci) {
        if (!((FantasyLevelAccess) level).fantasy$shouldTick()) {
            ci.setReturnValue(false);
        }
    }
}
