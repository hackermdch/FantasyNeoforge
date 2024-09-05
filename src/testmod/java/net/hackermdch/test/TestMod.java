package net.hackermdch.test;

import com.mojang.logging.LogUtils;
import net.hackermdch.fantasy.Api;
import net.hackermdch.fantasy.RuntimeLevelConfig;
import net.hackermdch.fantasy.RuntimeLevelHandle;
import net.hackermdch.fantasy.util.VoidChunkGenerator;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Mod(TestMod.ID)
public class TestMod {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID = "testmod";
    private static final HashMap<ResourceLocation, RuntimeLevelHandle> levels = new HashMap<>();

    @EventBusSubscriber(modid = ID)
    private static final class Events {
        @SubscribeEvent
        static void onServerStarted(ServerStartedEvent event) {
            var fantasy = Api.get(event.getServer());
            fantasy.openTemporaryWorld(ID, new RuntimeLevelConfig().setGenerator(new VoidChunkGenerator(event.getServer().registryAccess().registryOrThrow(Registries.BIOME).getHolder(0).orElseThrow())).setWorldConstructor(CustomWorld::new));
            fantasy.openTemporaryWorld(ID, new RuntimeLevelConfig().setGenerator(event.getServer().overworld().getChunkSource().getGenerator()).setWorldConstructor(CustomWorld::new));

            var biome = event.getServer().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS);
            var generator = new FlatLevelSource(new FlatLevelGeneratorSettings(Optional.empty(), biome, List.of()));
            fantasy.openTemporaryWorld(ID, new RuntimeLevelConfig().setDimensionType(BuiltinDimensionTypes.OVERWORLD).setGenerator(generator).setShouldTickTime(true));
        }

        @SubscribeEvent
        static void onRegisterCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(Commands.literal("fantasy_open").then(Commands.argument("name", ResourceLocationArgument.id()).executes(c -> {
                var source = c.getSource();
                try {
                    var time = new Object() {
                        long value = System.currentTimeMillis();
                    };
                    var id = ResourceLocationArgument.getId(c, "name");
                    var handle = Api.get(source.getServer()).getOrOpenPersistentWorld(id, new RuntimeLevelConfig().setGenerator(source.getServer().overworld().getChunkSource().getGenerator()).setDimensionType(source.getServer().overworld().dimensionTypeRegistration()).setSeed(id.hashCode()));
                    source.sendSuccess(() -> Component.literal("WorldCreate: " + (System.currentTimeMillis() - time.value)), false);
                    levels.put(id, handle);
                    time.value = System.currentTimeMillis();
                    if (source.getEntity() != null) {
                        source.getEntity().changeDimension(new DimensionTransition(handle.asLevel(), new Vec3(0, 100, 0), Vec3.ZERO, 0, 0, DimensionTransition.DO_NOTHING));
                        source.sendSuccess(() -> Component.literal("Teleport: " + (System.currentTimeMillis() - time.value)), false);
                    }
                } catch (Throwable e) {
                    LOGGER.error("Failed to open world", e);
                    source.sendFailure(Component.literal("Failed to open world"));
                    return 1;
                }
                return 0;
            })));
            event.getDispatcher().register(Commands.literal("fantasy_delete").then(Commands.argument("name", ResourceLocationArgument.id()).executes(c -> {
                var source = c.getSource();
                try {
                    var id = ResourceLocationArgument.getId(c, "name");
                    if (levels.get(id) == null) {
                        source.sendFailure(Component.literal("This world does not exist"));
                        return 1;
                    }
                    levels.get(id).delete();
                    levels.remove(id);
                    source.sendSuccess(() -> Component.literal("World \"" + id + "\" deleted"), true);
                } catch (Throwable e) {
                    LOGGER.error("Failed to delete world", e);
                    source.sendFailure(Component.literal("Failed to delete world"));
                    return 1;
                }
                return 0;
            })));
            event.getDispatcher().register(Commands.literal("fantasy_unload").then(Commands.argument("name", ResourceLocationArgument.id()).executes(c -> {
                var source = c.getSource();
                try {
                    var id = ResourceLocationArgument.getId(c, "name");
                    if (levels.get(id) == null) {
                        source.sendFailure(Component.literal("This world does not exist"));
                        return 1;
                    }
                    levels.get(id).unload();
                    levels.remove(id);
                    source.sendSuccess(() -> Component.literal("World \"" + id + "\" unloaded"), true);
                } catch (Throwable e) {
                    LOGGER.error("Failed to unload world", e);
                    source.sendFailure(Component.literal("Failed to unload world"));
                    return 1;
                }
                return 0;
            })));
        }
    }
}
