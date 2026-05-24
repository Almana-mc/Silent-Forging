package me.almana.silentforging.setup;

import me.almana.silentforging.Silentforging;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SfItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Silentforging.MODID);

    public static final DeferredItem<BlockItem> TOOL_FORGE = ITEMS.registerSimpleBlockItem("tool_forge", SfBlocks.TOOL_FORGE);

    private SfItems() {
    }
}
