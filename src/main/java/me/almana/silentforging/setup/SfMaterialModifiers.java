package me.almana.silentforging.setup;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.forge.ForgeQualityMaterialModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.setup.SgRegistries;

public final class SfMaterialModifiers {
    public static final DeferredRegister<IMaterialModifierType<?>> MATERIAL_MODIFIERS =
            DeferredRegister.create(SgRegistries.MATERIAL_MODIFIER_TYPE, Silentforging.MODID);

    public static final DeferredHolder<IMaterialModifierType<?>, ForgeQualityMaterialModifier.Type> FORGE_QUALITY =
            MATERIAL_MODIFIERS.register("forge_quality", ForgeQualityMaterialModifier.Type::new);

    private SfMaterialModifiers() {
    }
}
