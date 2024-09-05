package net.hackermdch.test;

import net.hackermdch.fantasy.RuntimeLevel;
import net.hackermdch.fantasy.RuntimeLevelConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class CustomWorld extends RuntimeLevel {
    private long dynSeed;
    private final RecipeManager recipeManager;

    protected CustomWorld(MinecraftServer server, ResourceKey<Level> registryKey, RuntimeLevelConfig config, Style style) {
        super(server, registryKey, config, style);
        recipeManager = new RecipeManager(server.registryAccess());
    }

    @Override
    public void tick(@NotNull BooleanSupplier shouldKeepTicking) {
        dynSeed = random.nextLong();
        super.tick(shouldKeepTicking);
    }

    @Override
    @NotNull
    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    @Override
    public long getSeed() {
        return dynSeed;
    }
}
