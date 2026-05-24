package me.almana.silentforging.forge;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Locale;

public enum ForgeRule implements StringRepresentable {
    BEND_LAST(Order.LAST, ForgeAction.BEND),
    BEND_SECOND_LAST(Order.SECOND_LAST, ForgeAction.BEND),
    BEND_THIRD_LAST(Order.THIRD_LAST, ForgeAction.BEND),
    HIT_LAST(Order.LAST, ForgeAction.HIT),
    HIT_SECOND_LAST(Order.SECOND_LAST, ForgeAction.HIT),
    HIT_THIRD_LAST(Order.THIRD_LAST, ForgeAction.HIT),
    FOLD_LAST(Order.LAST, ForgeAction.FOLD),
    FOLD_SECOND_LAST(Order.SECOND_LAST, ForgeAction.FOLD),
    FOLD_THIRD_LAST(Order.THIRD_LAST, ForgeAction.FOLD),
    EXTRUDE_LAST(Order.LAST, ForgeAction.EXTRUDE),
    EXTRUDE_SECOND_LAST(Order.SECOND_LAST, ForgeAction.EXTRUDE),
    EXTRUDE_THIRD_LAST(Order.THIRD_LAST, ForgeAction.EXTRUDE),
    DRAW_LAST(Order.LAST, ForgeAction.DRAW),
    DRAW_SECOND_LAST(Order.SECOND_LAST, ForgeAction.DRAW),
    DRAW_THIRD_LAST(Order.THIRD_LAST, ForgeAction.DRAW),
    TEMPER_LAST(Order.LAST, ForgeAction.TEMPER),
    TEMPER_SECOND_LAST(Order.SECOND_LAST, ForgeAction.TEMPER),
    TEMPER_THIRD_LAST(Order.THIRD_LAST, ForgeAction.TEMPER);

    public static final Codec<ForgeRule> CODEC = StringRepresentable.fromEnum(ForgeRule::values);
    public static final StreamCodec<ByteBuf, ForgeRule> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> values()[id],
            ForgeRule::ordinal
    );

    private final String serializedName;
    private final Order order;
    private final ForgeAction action;

    ForgeRule(Order order, ForgeAction action) {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.order = order;
        this.action = action;
    }

    public static boolean isConsistent(List<ForgeRule> rules) {
        if (rules.isEmpty() || rules.size() > 3) {
            return false;
        }
        boolean last = false;
        boolean secondLast = false;
        boolean thirdLast = false;
        for (ForgeRule rule : rules) {
            switch (rule.order) {
                case LAST -> {
                    if (last) return false;
                    last = true;
                }
                case SECOND_LAST -> {
                    if (secondLast) return false;
                    secondLast = true;
                }
                case THIRD_LAST -> {
                    if (thirdLast) return false;
                    thirdLast = true;
                }
            }
        }
        return true;
    }

    public boolean matches(ForgeWork work) {
        ForgeAction actual = switch (order) {
            case LAST -> work.last();
            case SECOND_LAST -> work.secondLast();
            case THIRD_LAST -> work.thirdLast();
        };
        return actual == action;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    private enum Order {
        LAST,
        SECOND_LAST,
        THIRD_LAST
    }
}
