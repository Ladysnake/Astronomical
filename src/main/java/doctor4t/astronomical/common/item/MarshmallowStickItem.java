package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.init.ModStatusEffects;
import doctor4t.astronomical.common.util.BlockCastFinder;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MarshmallowStickItem extends Item {
	public MarshmallowStickItem(Settings settings) {
		super(settings);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (entity instanceof PlayerEntity player) {
			var main = player.getMainHandStack().getItem() == this;
			var off = player.getOffHandStack().getItem() == this;
			if (!main && !off) return;
			if (player.astronomical$isHoldingAttack()) {
				var blocks = BlockCastFinder.castRayForGridPoints(player.getPos(), player.getRotationVector(), 2, 2);
				var headDistance = -1f;
				for (var block : blocks) {
					var state = world.getBlockState(block);
					if (state.isIn(Astronomical.HEAT_SOURCES)) {
						if (state.getProperties().contains(Properties.LIT) && !state.get(Properties.LIT)) {
							continue;
						}
						headDistance = (float) player.getPos().distanceTo(Vec3d.ofCenter(block));
						break;
					}
				}
				if (headDistance >= 0) {
					var stacks = new ItemStack[2];
					if (main) stacks[0] = player.getMainHandStack();
					if (off) stacks[1] = player.getOffHandStack();
					for (var heldStack : stacks) {
						if (heldStack == null) continue;
						var nbt = heldStack.getOrCreateNbt();
						var ticks = nbt.getFloat("RoastTicks");
						if (ticks < CookState.BURNT.cookTime + 3 * 20) {
//							var growth = Math.max(0, (4f - headDistance) / 10f);
							var growth = 1.5f;
							nbt.putFloat("RoastTicks", ticks + growth);
						}
					}
				}
			}
		}
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, @NotNull World world, @NotNull LivingEntity user) {
		var itemStack = new AtomicReference<>(stack);
		var state = CookState.getCookState(stack);
		if (user instanceof ServerPlayerEntity player) {
			var foodComponent = ModItems.MARSHMALLOW.getFoodComponent();
			if (foodComponent != null) {
				player.getHungerManager().add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
			}
			player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
			Criteria.CONSUME_ITEM.trigger(player, stack);
		}
		if (state == CookState.BURNT) {
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 40, 0, true, false, true));
//			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_DRIPSTONE_BLOCK_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
		} else {
			if (state.givesEffect) {
				user.addStatusEffect(new StatusEffectInstance(stack.isOf(ModItems.STARMALLOW_STICK) ? ModStatusEffects.STARFALL : ModStatusEffects.STARGAZING, Integer.MAX_VALUE, state.effectAmplifier, true, false, true));
			}
			world.playSound(null, user.getX(), user.getY(), user.getZ(), user.getEatSound(stack), SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
		}
		if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
			stack.damage(1, user, (entity) -> itemStack.set(new ItemStack(Items.STICK)));
		}
		user.emitGameEvent(GameEvent.EAT);
		return itemStack.get();
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
		var itemStack = user.getStackInHand(hand);
		user.setCurrentHand(hand);
		return TypedActionResult.consume(itemStack);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 24;
	}

	public SoundEvent getEatSound(ItemStack stack) {
//		if (CookState.getCookState(stack) == CookState.BURNT) {
//			return SoundEvents.BLOCK_DRIPSTONE_BLOCK_HIT;
//		}
		return super.getEatSound();
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, @NotNull List<Text> tooltip, TooltipContext context) {
		tooltip.add(withColor(Text.translatable("tooltip.astronomical.cook_level." + CookState.getCookState(stack).name().toLowerCase()), CookState.getCookState(stack).color));
		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.EAT;
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return false;
	}

	static Text withColor(@NotNull Text text, int color) {
		List<Text> textList = text.setStyle(text.getStyle().withColor(color));
		if (textList.isEmpty()) {
			return Text.literal("");
		} else {
			var first = textList.get(0);
			for (var i = 1; i < textList.size(); i++) {
				first = first.copy().append(textList.get(i));
			}
			return first;
		}
	}

	public enum CookState {
		RAW(0, -1, 0xA2A6A1),
		SLIGHTLY_COOKED(2 * 20, 0, 0xC9B188),
		COOKED(10 * 20, 1, 0xE8BC66),
		PERFECT(16 * 20 + 10, 2, 0xFFC724),
		BURNT(17 * 20, -1, 0x473D24);

		public final int cookTime;
		public final boolean givesEffect;
		public final int effectAmplifier;
		public final int color;

		CookState(int cookTime, int effectAmplifier, int color) {
			this.cookTime = cookTime;
			this.givesEffect = effectAmplifier >= 0;
			this.effectAmplifier = effectAmplifier;
			this.color = color;
		}

		public static CookState getCookState(@NotNull ItemStack stack) {
			return getCookState(stack.getOrCreateNbt().getFloat("RoastTicks"));
		}

		public static CookState getCookState(float roastTicks) {
			if (roastTicks < SLIGHTLY_COOKED.cookTime) {
				return RAW;
			} else if (roastTicks < COOKED.cookTime) {
				return SLIGHTLY_COOKED;
			} else if (roastTicks < PERFECT.cookTime) {
				return COOKED;
			} else if (roastTicks < BURNT.cookTime) {
				return PERFECT;
			} else {
				return BURNT;
			}
		}

		public CookState next() {
			return switch (this) {
				case RAW -> SLIGHTLY_COOKED;
				case SLIGHTLY_COOKED -> COOKED;
				case COOKED -> PERFECT;
				case PERFECT, BURNT -> BURNT;
			};
		}
	}
}
