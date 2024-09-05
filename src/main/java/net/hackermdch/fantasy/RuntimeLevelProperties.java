package net.hackermdch.fantasy;

import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.NotNull;

public final class RuntimeLevelProperties extends DerivedLevelData {
    final RuntimeLevelConfig config;
    private final GameRules rules;

    public RuntimeLevelProperties(WorldData saveProperties, RuntimeLevelConfig config) {
        super(saveProperties, saveProperties.overworldData());
        this.config = config;
        rules = new GameRules();
        config.getGameRules().applyTo(rules, null);
    }

    @Override
    @NotNull
    public GameRules getGameRules() {
        if (this.config.shouldMirrorOverworldGameRules()) {
            return super.getGameRules();
        }
        return this.rules;
    }

    @Override
    public void setDayTime(long timeOfDay) {
        config.setTimeOfDay(timeOfDay);
    }

    @Override
    public long getDayTime() {
        return config.getTimeOfDay();
    }

    @Override
    public void setClearWeatherTime(int time) {
        config.setSunny(time);
    }

    @Override
    public int getClearWeatherTime() {
        return config.getSunnyTime();
    }

    @Override
    public void setRaining(boolean raining) {
        config.setRaining(raining);
    }

    @Override
    public boolean isRaining() {
        return config.isRaining();
    }

    @Override
    public void setRainTime(int time) {
        config.setRaining(time);
    }

    @Override
    public int getRainTime() {
        return config.getRainTime();
    }

    @Override
    public void setThundering(boolean thundering) {
        config.setThundering(thundering);
    }

    @Override
    public boolean isThundering() {
        return config.isThundering();
    }

    @Override
    public void setThunderTime(int time) {
        config.setThundering(time);
    }

    @Override
    public int getThunderTime() {
        return config.getThunderTime();
    }

    @Override
    @NotNull
    public Difficulty getDifficulty() {
        if (config.shouldMirrorOverworldDifficulty()) {
            return super.getDifficulty();
        }
        return config.getDifficulty();
    }
}
