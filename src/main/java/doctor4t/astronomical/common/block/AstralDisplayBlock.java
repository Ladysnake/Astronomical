package doctor4t.astronomical.common.block;

import carpet.script.language.Sys;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AstralDisplayBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.FACING;

	public AstralDisplayBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getAxis().isVertical() ? Direction.UP : ctx.getPlayerLookDirection());
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new AstralDisplayBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return super.getTicker(world, state, type);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);

		if (state.get(FACING).getAxis().isHorizontal()) {
			BlockEntity astralDisplay = world.getBlockEntity(pos);
			for (int i = 1; i <= 20; i++) {
				BlockEntity parentAstralDisplay = world.getBlockEntity(pos.offset(state.get(FACING), i));
				if (astralDisplay instanceof AstralDisplayBlockEntity astralDisplayBlockEntity && parentAstralDisplay instanceof AstralDisplayBlockEntity parentAstralDisplayBlockEntity) {
					astralDisplayBlockEntity.setParentPos(parentAstralDisplayBlockEntity.getParentPos() != null ? parentAstralDisplayBlockEntity.getParentPos() : pos.offset(state.get(FACING), i));
					return;
				}
			}
		}
	}
}
