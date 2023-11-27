package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.util.BlockCastFinder;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialeemisc.util.MialeeText;

import java.util.concurrent.atomic.AtomicReference;

public class MarshmallowStickItem extends Item {
	public MarshmallowStickItem(Settings settings) {
		super(settings);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (entity instanceof PlayerEntity player) {
			if (selected && player.astronomical$isHoldingAttack()) {
				var blocks = BlockCastFinder.castRayForGridPoints(player.getPos(), player.getRotationVector(), 2, 6);
				var heated = false;
				for (var block : blocks) {
					var state = world.getBlockState(block);
					if (state.isIn(Astronomical.HEAT_SOURCES)) {
						heated = true;
						break;
					}
				}
				if (heated) {
					var nbt = stack.getOrCreateNbt();
					var ticks = nbt.getInt("RoastTicks");
					if (ticks < CookState.BURNT.cookTime + 3 * 20) nbt.putInt("RoastTicks", ticks + 1);
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
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 4 * 20, 0, true, false, true));
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 2 * 20, 0, true, false, true));
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_DRIPSTONE_BLOCK_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
		} else {
			if (state.givesEffect) {
				user.addStatusEffect(new StatusEffectInstance(Astronomical.STARGAZING_EFFECT, Integer.MAX_VALUE, state.effectAmplifier, true, false, true));
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
		if (CookState.getCookState(stack) == CookState.BURNT) {
			return 64;
		}
		return 24;
	}

	public SoundEvent getEatSound(ItemStack stack) {
		if (CookState.getCookState(stack) == CookState.BURNT) {
			return SoundEvents.BLOCK_DRIPSTONE_BLOCK_HIT;
		}
		return super.getEatSound();
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey(stack) + "." + CookState.getCookState(stack).name().toLowerCase();
	}

	@Override
	public Text getName(ItemStack stack) {
		return MialeeText.withColor(super.getName(stack), CookState.getCookState(stack).color);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.EAT;
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return false;
	}

	public enum CookState {
		RAW(0, -1, 0xFF2EFF27),
		SLIGHTLY_COOKED(2 * 20, 0, 0xFFFBFF2D),
		COOKED(10 * 20, 1, 0xFFFAB127),
		PERFECT(16 * 20, 2, 0xFFF92922),
		BURNT(17 * 20, -1, 0xFF5B8FE2);

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
			return getCookState(stack.getOrCreateNbt().getInt("RoastTicks"));
		}

		public static CookState getCookState(int roastTicks) {
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

		public CookState previous() {
			return switch (this) {
				case RAW, SLIGHTLY_COOKED -> RAW;
				case COOKED -> SLIGHTLY_COOKED;
				case PERFECT -> COOKED;
				case BURNT -> PERFECT;
			};
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
