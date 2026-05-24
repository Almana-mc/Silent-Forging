package me.almana.silentforging.setup;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.block.ToolForgeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SfBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Silentforging.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ToolForgeBlockEntity>> TOOL_FORGE =
            BLOCK_ENTITIES.register(
                    "tool_forge",
                    () -> new BlockEntityType<>(ToolForgeBlockEntity::new, SfBlocks.TOOL_FORGE.get())
            );

    private SfBlockEntities() {
    }
}
