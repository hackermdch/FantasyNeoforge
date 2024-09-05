package net.hackermdch.fantasy.mixin.registry;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.hackermdch.fantasy.Fantasy;
import net.hackermdch.fantasy.RemoveFromRegistry;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> implements RemoveFromRegistry<T>, WritableRegistry<T> {
    @Shadow
    @Final
    private Map<T, Holder.Reference<T>> byValue;
    @Shadow
    @Final
    private Map<ResourceLocation, Holder.Reference<T>> byLocation;
    @Shadow
    @Final
    private Map<ResourceKey<T>, Holder.Reference<T>> byKey;
    @Shadow
    @Final
    private Map<ResourceKey<T>, RegistrationInfo> registrationInfos;
    @Shadow
    @Final
    private ObjectList<Holder.Reference<T>> byId;
    @Shadow
    @Final
    private Reference2IntMap<T> toId;
    @Shadow
    private boolean frozen;
    @Shadow
    @Final
    ResourceKey<? extends Registry<T>> key;

    @Override
    public boolean fantasy$remove(T entry) {
        var registryEntry = byValue.get(entry);
        int rawId = toId.removeInt(entry);
        if (rawId == -1) {
            return false;
        }
        try {
            byKey.remove(registryEntry.key());
            byLocation.remove(registryEntry.key().location());
            byValue.remove(entry);
            byId.set(rawId, null);
            registrationInfos.remove(key);
            return true;
        } catch (Throwable e) {
            Fantasy.LOGGER.error("Could not remove entry", e);
            return false;
        }
    }

    @Override
    public boolean fantasy$remove(ResourceLocation key) {
        var entry = byLocation.get(key);
        return entry != null && entry.isBound() && this.fantasy$remove(entry.value());
    }

    @Override
    public void fantasy$setFrozen(boolean value) {
        this.frozen = value;
    }

    @Override
    public boolean fantasy$isFrozen() {
        return this.frozen;
    }

    @ModifyReturnValue(method = "holders", at = @At("RETURN"))
    public Stream<Holder.Reference<T>> filter(Stream<Holder.Reference<T>> original) {
        return original.filter(Objects::nonNull);
    }
}
