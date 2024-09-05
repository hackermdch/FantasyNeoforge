package net.hackermdch.fantasy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.hackermdch.fantasy.util.ChunkGeneratorSettingsProvider;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;dummy()Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;"))
    private NoiseGeneratorSettings fantasy$useProvidedChunkGeneratorSettings(Operation<NoiseGeneratorSettings> original, @Local(argsOnly = true) net.minecraft.world.level.chunk.ChunkGenerator chunkGenerator) {
        if (chunkGenerator instanceof ChunkGeneratorSettingsProvider provider) {
            var settings = provider.getSettings();
            if (settings != null) {
                return settings;
            }
        }
        return original.call();
    }
}
