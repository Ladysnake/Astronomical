package doctor4t.astronomical.common.block;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.init.ModBlocks;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;

public class AstralDisplayBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.FACING;
	public static final BooleanProperty POWERED = Properties.POWERED;

	public AstralDisplayBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
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
		builder.add(FACING, POWERED);
	}

	@Override
	public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getAxis().isVertical() ? Direction.UP : ctx.getPlayerLookDirection()).with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean bl = state.get(POWERED);
			if (bl != world.isReceivingRedstonePower(pos)) {
				if (bl) {
					world.scheduleBlockTick(pos, this, 0);
				} else {
					world.setBlockState(pos, state.cycle(POWERED), Block.NOTIFY_LISTENERS);
				}
			}

		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		if (state.get(POWERED) && !world.isReceivingRedstonePower(pos)) {
			world.setBlockState(pos, state.cycle(POWERED), Block.NOTIFY_LISTENERS);
		}

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
				BlockEntity foundAstralDisplay = world.getBlockEntity(pos.offset(state.get(FACING), i));
				// if astral display found
				if (astralDisplay instanceof AstralDisplayBlockEntity astralDisplayBlockEntity
					&& foundAstralDisplay instanceof AstralDisplayBlockEntity foundAstralDisplayBE
					&& world.getBlockState(pos.offset(state.get(FACING), i)).isOf(ModBlocks.ASTRAL_DISPLAY)) {
					Direction direction = world.getBlockState(pos.offset(state.get(FACING), i)).get(FACING);

					// if same orientation, set to parent
					if (direction == state.get(FACING)) {
						astralDisplayBlockEntity.setParentPos(foundAstralDisplayBE.getParentPos());
					} else { // if different horizontal orientation set to display found
						astralDisplayBlockEntity.setParentPos(pos.offset(state.get(FACING), i));
					}
					return;
				}
			}
		}
	}

	@Override
	public void onStateReplaced(@NotNull BlockState state, World world, BlockPos pos, @NotNull BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
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
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AstralDisplayBlockEntity astralDisplayBlockEntity) {
				player.openHandledScreen(astralDisplayBlockEntity);
				PacketByteBuf buf = PacketByteBufs.create();
				buf.writeBlockPos(pos);
				buf.writeDouble(astralDisplayBlockEntity.yLevel.getValue());
				buf.writeDouble(astralDisplayBlockEntity.rotSpeed.getValue());
				buf.writeDouble(astralDisplayBlockEntity.spin.getValue());
				ServerPlayNetworking.send((ServerPlayerEntity) player, Astronomical.id("astral_display"), buf);
			}
			return ActionResult.CONSUME;
		}
	}

}
