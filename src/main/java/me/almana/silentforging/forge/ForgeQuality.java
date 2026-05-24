package me.almana.silentforging.forge;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ForgeQuality implements StringRepresentable {
    CRUDE(-10),
    NORMAL(0),
    PRISTINE(10);

    public static final Codec<ForgeQuality> CODEC = StringRepresentable.fromEnum(ForgeQuality::values);
    public static final StreamCodec<ByteBuf, ForgeQuality> STREAM_CODEC = ByteBufCodecs.idMapper(
            ForgeQuality::byId,
            ForgeQuality::ordinal
    );

    private final String serializedName;
    private final int statBonusPercent;

    ForgeQuality(int statBonusPercent) {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.statBonusPercent = statBonusPercent;
    }

    public int statBonusPercent() {
        return statBonusPercent;
    }

    public Component displayName() {
        return Component.translatable("quality.silentforging." + serializedName);
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public static ForgeQuality fromExtraStrikes(int extraStrikes) {
        if (extraStrikes <= 1) {
            return PRISTINE;
        }
        if (extraStrikes <= 3) {
            return NORMAL;
        }
        return CRUDE;
    }

    public static ForgeQuality byId(int id) {
        ForgeQuality[] all = values();
        return id >= 0 && id < all.length ? all[id] : NORMAL;
    }

    public static ForgeQuality byName(String name) {
        for (ForgeQuality quality : values()) {
            if (quality.serializedName.equals(name)) {
                return quality;
            }
        }
        return NORMAL;
    }
}
