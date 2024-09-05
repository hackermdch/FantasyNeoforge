package net.hackermdch.fantasy;

import net.hackermdch.fantasy.util.TransientChunkGenerator;
import net.hackermdch.fantasy.util.VoidChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Fantasy.ID)
public class Fantasy {
    public static final Logger LOGGER = LogManager.getLogger(Fantasy.class);
    public static final String ID = "fantasy";
    public static final ResourceKey<DimensionType> DEFAULT_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(ID, "default"));

    public Fantasy(IEventBus modBus) {
        modBus.register(new Object() {
            @SubscribeEvent
            void handle(RegisterEvent event) {
                modBus.unregister(this);
                Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath(ID, "void"), VoidChunkGenerator.CODEC);
                Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath(ID, "transient"), TransientChunkGenerator.CODEC);
            }
        });
    }
}
