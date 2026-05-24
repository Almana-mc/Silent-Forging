package me.almana.silentforging.forge;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ForgeAction implements StringRepresentable {
    BEND(9, 1),
    HIT(18, -1),
    FOLD(-11, 2),
    EXTRUDE(-14, 0),
    DRAW(7, 2),
    TEMPER(-4, 3);

    public static final Codec<ForgeAction> CODEC = StringRepresentable.fromEnum(ForgeAction::values);
    public static final StreamCodec<ByteBuf, ForgeAction> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> values()[id],
            ForgeAction::ordinal
    );

    private final String serializedName;
    private final int power;
    private final int quality;

    ForgeAction(int power, int quality) {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.power = power;
        this.quality = quality;
    }

    public int power() {
        return power;
    }

    public boolean increasesProgress() {
        return power > 0;
    }

    public boolean reducesProgress() {
        return power < 0;
    }

    public int quality() {
        return quality;
    }

    public String key() {
        return serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public static ForgeAction byId(int id) {
        ForgeAction[] all = values();
        return id >= 0 && id < all.length ? all[id] : null;
    }
}
