package doctor4t.astronomical.common.block;

import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AstralDisplayBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.FACING;

	public AstralDisplayBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public BlockState rotate(@NotNull BlockState state, @NotNull BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(@NotNull BlockState state, @NotNull BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
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
		if (itemStack.hasCustomName()) {
			var blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AstralDisplayBlockEntity astralDisplayBlockEntity) {
				astralDisplayBlockEntity.setCustomName(itemStack.getName());
			}
		}
		if (state.get(FACING).getAxis().isHorizontal()) {
			var astralDisplay = world.getBlockEntity(pos);
			for (var i = 1; i <= 20; i++) {
				var parentAstralDisplay = world.getBlockEntity(pos.offset(state.get(FACING), i));
				if (astralDisplay instanceof AstralDisplayBlockEntity astralDisplayBlockEntity && parentAstralDisplay instanceof AstralDisplayBlockEntity parentAstralDisplayBlockEntity) {
					astralDisplayBlockEntity.setParentPos(parentAstralDisplayBlockEntity.getParentPos() != null ? parentAstralDisplayBlockEntity.getParentPos() : pos.offset(state.get(FACING), i));
					return;
				}
			}
		}
	}

	@Override
	public void onStateReplaced(@NotNull BlockState state, World world, BlockPos pos, @NotNull BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			var blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
				world.updateComparators(pos, this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			var blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AstralDisplayBlockEntity) {
				player.openHandledScreen((AstralDisplayBlockEntity)blockEntity);
			}

			return ActionResult.CONSUME;
		}
	}

}
