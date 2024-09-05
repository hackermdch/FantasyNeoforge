package net.hackermdch.fantasy;

import net.minecraft.world.level.dimension.LevelStem;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@SuppressWarnings("DataFlowIssue")
@ApiStatus.Internal
public interface FantasyLevelStem {
    Predicate<LevelStem> SAVE_PREDICATE = (e) -> ((FantasyLevelStem) (Object) e).fantasy$getSave();
    Predicate<LevelStem> SAVE_PROPERTIES_PREDICATE = (e) -> ((FantasyLevelStem) (Object) e).fantasy$getSaveProperties();

    void fantasy$setSave(boolean value);

    boolean fantasy$getSave();

    void fantasy$setSaveProperties(boolean value);

    boolean fantasy$getSaveProperties();
}
