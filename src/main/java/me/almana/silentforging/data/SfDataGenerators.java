package me.almana.silentforging.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class SfDataGenerators {
    private SfDataGenerators() {
    }

    public static void gatherData(GatherDataEvent.Server event) {
        event.createProvider(ForgeProfileProvider::new);
    }
}
