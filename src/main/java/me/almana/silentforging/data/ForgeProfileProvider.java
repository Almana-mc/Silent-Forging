package me.almana.silentforging.data;

import me.almana.silentforging.recipe.DefaultForgeProfiles;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ForgeProfileProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public ForgeProfileProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<Identifier, Recipe<?>> profiles = new LinkedHashMap<>();
        for (DefaultForgeProfiles.Entry entry : DefaultForgeProfiles.ENTRIES) {
            profiles.put(entry.profileId(), entry.recipe());
        }
        return DataProvider.saveAll(cache, Recipe.CODEC, pathProvider, profiles);
    }

    @Override
    public String getName() {
        return "Silent Forging profiles";
    }
}