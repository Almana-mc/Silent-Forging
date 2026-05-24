package me.almana.silentforging.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ToolForgeBlock extends BaseEntityBlock {
    public static final MapCodec<ToolForgeBlock> CODEC = simpleCodec(ToolForgeBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
            box(3.5, 0, 3.5, 12.5, 2, 12.5),
            box(5, 1, 5, 11, 6, 11),
            box(4.9, 5, 5.25, 11.1, 10, 11.5),
            box(6, 8, 5, 10.5, 12.5, 14),
            box(5, 8, 11, 11, 10, 16),
            box(7, 7.5, 0, 9, 9, 6)
    );

    public ToolForgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToolForgeBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()
                && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof ToolForgeBlockEntity forge) {
            serverPlayer.openMenu(forge, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
