package doctor4t.astronomical.common.block;

import doctor4t.astronomical.common.block.entity.MarshmallowCanBlockEntity;
import doctor4t.astronomical.common.init.ModBlocks;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.init.ModSoundEvents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MarshmallowCanBlock extends BlockWithEntity {
	public static final IntProperty LEVEL = IntProperty.of("level", 0, 8);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 10.0, 12.0);

	public MarshmallowCanBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 8));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MarshmallowCanBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		boolean isStarmallow = state.isOf(ModBlocks.STARMALLOW_CAN);
		if (blockEntity instanceof MarshmallowCanBlockEntity marshmallowCanBlockEntity) {
			if (player.getStackInHand(hand).isOf(Items.STICK)) {
				if (marshmallowCanBlockEntity.decrementMarshmallowCount()) {
					if (player.getStackInHand(hand).getCount() == 1) {
						player.setStackInHand(hand, new ItemStack(isStarmallow ? ModItems.STARMALLOW_STICK : ModItems.MARSHMALLOW_STICK));
					} else {
						player.getStackInHand(hand).decrement(1);
						player.giveItemStack(new ItemStack(isStarmallow ? ModItems.STARMALLOW_STICK : ModItems.MARSHMALLOW_STICK));
					}
					player.playSound(ModSoundEvents.MARSHMALLOW_CAN_TAKE, .5f, (float) (1.0f + world.random.nextGaussian() / 20f));
					return ActionResult.SUCCESS;
				}
			} else if (player.getStackInHand(hand).isOf(isStarmallow ? ModItems.STARMALLOW : ModItems.MARSHMALLOW)) {
				int i = marshmallowCanBlockEntity.incrementMarshmallowCount(player.getStackInHand(hand).getCount());
				if (i > 0) {
					player.getStackInHand(hand).decrement(i);
					player.playSound(ModSoundEvents.MARSHMALLOW_CAN_STORE, .5f, (float) (1.0f + world.random.nextGaussian() / 20f));
					return ActionResult.SUCCESS;
				}
			}
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
		return this.getDefaultState().with(LEVEL, (int) Math.ceil((double) ctx.getStack().getOrCreateNbt().getInt("marshmallowCount") / 8));
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof MarshmallowCanBlockEntity marshmallowCanBlockEntity) {
				ItemStack itemStack = new ItemStack(state.getBlock().asItem());
				if (!world.isClient) {
					itemStack.getOrCreateNbt().putInt("marshmallowCount", marshmallowCanBlockEntity.getMarshmallowCount());
				}
				ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, itemStack);
				itemEntity.setToDefaultPickupDelay();
				world.spawnEntity(itemEntity);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		int marshmallowCount = itemStack.getOrCreateNbt().getInt("marshmallowCount");
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof MarshmallowCanBlockEntity marshmallowCanBlockEntity) {
			marshmallowCanBlockEntity.setMarshmallowCount(marshmallowCount);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		int marshmallowCount = stack.getOrCreateNbt().getInt("marshmallowCount");
		boolean isStarmallow = stack.isOf(ModBlocks.STARMALLOW_CAN.asItem());
		String plural = (marshmallowCount == 1 ? "" : "s");

		tooltip.add(Text.literal(marshmallowCount + " ").append(
			Text.translatable(isStarmallow ? ("item.astronomical.starmallow" + plural) : ("item.astronomical.marshmallow" + plural))
		).setStyle(Style.EMPTY.withColor(isStarmallow ? 0xD570FF : 11184810)));

		super.appendTooltip(stack, world, tooltip, options);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction direction = Direction.DOWN;
		return Block.sideCoversSmallSquare(world, pos.offset(direction), direction.getOpposite());
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return Direction.DOWN == direction && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
