package me.almana.silentforging.data;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.setup.SfBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class SfBlockTagProvider extends IntrinsicHolderTagsProvider<Block> {
    public SfBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.BLOCK, lookupProvider, block -> block.builtInRegistryHolder().key(), Silentforging.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(SfBlocks.TOOL_FORGE.get());
        tag(BlockTags.NEEDS_IRON_TOOL).add(SfBlocks.TOOL_FORGE.get());
    }
}
