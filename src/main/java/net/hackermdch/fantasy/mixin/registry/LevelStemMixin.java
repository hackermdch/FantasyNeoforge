package net.hackermdch.fantasy.mixin.registry;

import net.hackermdch.fantasy.FantasyLevelStem;
import net.minecraft.world.level.dimension.LevelStem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelStem.class)
public class LevelStemMixin implements FantasyLevelStem {
    @Unique
    private boolean fantasy$save = true;
    @Unique
    private boolean fantasy$saveProperties = true;

    @Override
    public void fantasy$setSave(boolean value) {
        this.fantasy$save = value;
    }

    @Override
    public boolean fantasy$getSave() {
        return this.fantasy$save;
    }

    @Override
    public void fantasy$setSaveProperties(boolean value) {
        this.fantasy$saveProperties = value;
    }

    @Override
    public boolean fantasy$getSaveProperties() {
        return this.fantasy$saveProperties;
    }
}
