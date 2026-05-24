package me.almana.silentforging.setup;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.forge.ForgeQuality;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SfDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Silentforging.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ForgeQuality>> FORGE_QUALITY =
            DATA_COMPONENTS.registerComponentType(
                    "forge_quality",
                    builder -> builder
                            .persistent(ForgeQuality.CODEC)
                            .networkSynchronized(ForgeQuality.STREAM_CODEC)
            );

    private SfDataComponents() {
    }
}
