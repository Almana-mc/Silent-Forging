package me.almana.silentforging.data;

import me.almana.silentforging.setup.SfBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;

public class SfBlockLootProvider extends BlockLootSubProvider {
    public SfBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(SfBlocks.TOOL_FORGE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(SfBlocks.TOOL_FORGE.get());
    }
}
