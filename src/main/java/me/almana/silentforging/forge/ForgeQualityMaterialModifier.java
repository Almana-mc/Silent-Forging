package me.almana.silentforging.forge;

import com.mojang.serialization.MapCodec;
import me.almana.silentforging.Silentforging;
import me.almana.silentforging.setup.SfDataComponents;
import me.almana.silentforging.setup.SfMaterialModifiers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record ForgeQualityMaterialModifier(ForgeQuality quality) implements IMaterialModifier {
    public static final MapCodec<ForgeQualityMaterialModifier> CODEC = ForgeQuality.CODEC
            .fieldOf("quality")
            .xmap(ForgeQualityMaterialModifier::new, ForgeQualityMaterialModifier::quality);
    public static final StreamCodec<RegistryFriendlyByteBuf, ForgeQualityMaterialModifier> STREAM_CODEC =
            StreamCodec.composite(
                    ForgeQuality.STREAM_CODEC,
                    ForgeQualityMaterialModifier::quality,
                    ForgeQualityMaterialModifier::new
            );

    @Override
    public IMaterialModifierType<?> getType() {
        return SfMaterialModifiers.FORGE_QUALITY.get();
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> modifyProperties(
            MaterialInstance material,
            PartType partType,
            PropertyKey<T, V> key,
            Collection<V> values
    ) {
        if (!key.property().isAffectedByGrades() || quality.statBonusPercent() == 0) {
            return values;
        }
        return IMaterialModifier.Helper.modifyNumberValuesWithBonusOrPenalty(
                key,
                values,
                quality.statBonusPercent() / 100F
        );
    }

    @Override
    public void appendTooltip(List<Component> tooltip) {
        tooltip.add(quality.displayName());
    }

    @Override
    public MutableComponent modifyMaterialName(MutableComponent name) {
        if (quality == ForgeQuality.NORMAL) {
            return name;
        }
        return name.append(Component.literal(" [")).append(quality.displayName()).append(Component.literal("]"));
    }

    public static final class Type implements IMaterialModifierType<ForgeQualityMaterialModifier> {
        private static final Identifier ID = Identifier.fromNamespaceAndPath(Silentforging.MODID, "forge_quality");

        @Override
        public Identifier getId() {
            return ID;
        }

        @Override
        public Optional<ForgeQualityMaterialModifier> readModifier(ItemInstance stack) {
            ForgeQuality quality = stack.get(SfDataComponents.FORGE_QUALITY);
            if (quality == null || quality == ForgeQuality.NORMAL) {
                return Optional.empty();
            }
            return Optional.of(new ForgeQualityMaterialModifier(quality));
        }

        @Override
        public void addModifier(ForgeQualityMaterialModifier modifier, ItemStack stack) {
            if (modifier.quality() == ForgeQuality.NORMAL) {
                removeModifier(stack);
                return;
            }
            stack.set(SfDataComponents.FORGE_QUALITY, modifier.quality());
        }

        @Override
        public void removeModifier(ItemStack stack) {
            stack.remove(SfDataComponents.FORGE_QUALITY);
        }

        @Override
        public MapCodec<ForgeQualityMaterialModifier> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ForgeQualityMaterialModifier> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
