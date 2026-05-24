package me.almana.silentforging.setup;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.block.ToolForgeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SfBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Silentforging.MODID);

    public static final DeferredBlock<ToolForgeBlock> TOOL_FORGE = BLOCKS.registerBlock(
            "tool_forge",
            ToolForgeBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .requiresCorrectToolForDrops()
    );

    private SfBlocks() {
    }
}
