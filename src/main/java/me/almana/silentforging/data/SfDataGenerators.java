package me.almana.silentforging.data;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

public final class SfDataGenerators {
    private SfDataGenerators() {
    }

    public static void gatherData(GatherDataEvent.Server event) {
        event.createProvider(ForgeProfileProvider::new);
        event.createProvider(SfBlockTagProvider::new);
        event.createProvider(SfRecipeProvider.Runner::new);
        event.createProvider((output, lookupProvider) -> new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(SfBlockLootProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider
        ));
    }
}
