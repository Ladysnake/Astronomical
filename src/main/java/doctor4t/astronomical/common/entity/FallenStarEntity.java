package doctor4t.astronomical.common.entity;

import com.sammy.lodestone.setup.LodestoneParticles;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModEntities;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.init.ModSoundEvents;
import doctor4t.astronomical.common.item.NanoAstralObjectItem;
import doctor4t.astronomical.common.item.NanoCosmosItem;
import doctor4t.astronomical.common.item.NanoPlanetItem;
import doctor4t.astronomical.common.item.NanoRingItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.awt.*;

public class FallenStarEntity extends Entity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(FallenStarEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

	public FallenStarEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	public FallenStarEntity(World world, double x, double y, double z) {
		this(ModEntities.FALLEN_STAR, world);
		this.setPosition(x, y, z);
	}

	@Override
	public void tick() {
		super.tick();

		if (!world.isClient && world.isDay()) {
			this.damage(DamageSource.GENERIC, 1.0f);
		}
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (player.getStackInHand(hand).isOf(ModItems.ASTRAL_CONTAINER)) {
			ItemStack retItemStack = this.getStack();
			if (retItemStack.isEmpty()) {
				int poolSize = NanoPlanetItem.PlanetTexture.SIZE + Astronomical.STAR_TEMPERATURE_COLORS.size() + NanoRingItem.RingTexture.SIZE + NanoCosmosItem.CosmosTexture.SIZE;
				int ran = 1 + random.nextInt(poolSize);

				int size = 1 + player.getRandom().nextInt(3);
				if (ran <= NanoPlanetItem.PlanetTexture.SIZE) {
					retItemStack = new ItemStack(ModItems.NANO_PLANET);
					retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color1", new Color(player.getRandom().nextFloat(), player.getRandom().nextFloat(), player.getRandom().nextFloat()).getRGB());
					retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color2", new Color(player.getRandom().nextFloat(), player.getRandom().nextFloat(), player.getRandom().nextFloat()).getRGB());
					String texture = NanoPlanetItem.PlanetTexture.getRandom().name();
					retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putString("texture", texture);
				} else if (ran <= NanoPlanetItem.PlanetTexture.SIZE + Astronomical.STAR_TEMPERATURE_COLORS.size()) {
					retItemStack = new ItemStack(ModItems.NANO_STAR);
					int temp = Astronomical.getRandomStarTemperature(player.getRandom());
					retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("temperature", temp);
				} else if (ran <= NanoPlanetItem.PlanetTexture.SIZE + Astronomical.STAR_TEMPERATURE_COLORS.size() + NanoRingItem.RingTexture.SIZE) {
					retItemStack = new ItemStack(ModItems.NANO_COSMOS);
					String texture = NanoCosmosItem.CosmosTexture.getRandom().name();
					retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putString("texture", texture);
				} else if (ran <= NanoPlanetItem.PlanetTexture.SIZE + Astronomical.STAR_TEMPERATURE_COLORS.size() + NanoRingItem.RingTexture.SIZE + NanoCosmosItem.CosmosTexture.SIZE) {
					retItemStack = new ItemStack(ModItems.NANO_RING);
					retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color", new Color(player.getRandom().nextFloat(), player.getRandom().nextFloat(), player.getRandom().nextFloat()).getRGB());
					String texture = NanoRingItem.RingTexture.getRandom().name();
					retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putString("texture", texture);
				}
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", size);
			}

			player.getStackInHand(hand).decrement(1);
			world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSoundEvents.STAR_COLLECT, SoundCategory.NEUTRAL, 1f, (float) (1f + world.random.nextGaussian() * .1f));
			player.giveItemStack(retItemStack);
			this.discard();
			return ActionResult.SUCCESS;
		} else if (player.getStackInHand(hand).getItem() instanceof NanoAstralObjectItem) {
			if (this.getStack().isEmpty()) {
				this.setStack(player.getStackInHand(hand));
				player.setStackInHand(hand, new ItemStack(ModItems.ASTRAL_CONTAINER));
				world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSoundEvents.STAR_CRAFT, SoundCategory.NEUTRAL, 1f, (float) (1f + world.random.nextGaussian() * .1f));
				return ActionResult.SUCCESS;
			} else return ActionResult.CONSUME;
		} else if (player.getStackInHand(hand).isOf(ModItems.ASTRAL_FRAGMENT) && !this.getStack().isEmpty()) {
			int size = this.getStack().getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size") + 1;
			this.getStack().getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", size);
			player.getStackInHand(hand).decrement(1);
			world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSoundEvents.STAR_CRAFT, SoundCategory.NEUTRAL, 1f, (float) (1f + world.random.nextGaussian() * .1f));
			player.sendMessage(Text.translatable("action.increase_star_size").append(Text.literal(" "+ size)).setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)), true);
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.world instanceof ServerWorld serverWorld) {
			serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(), ModSoundEvents.STAR_BREAK, SoundCategory.NEUTRAL, 2f, (float) (1f + world.random.nextGaussian() * .1f));
			int starsToReturn = 1;

			if (!this.getStack().isEmpty()) {
				world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSoundEvents.STAR_COLLECT, SoundCategory.NEUTRAL, 1f, (float) (1f + world.random.nextGaussian() * .1f));
				starsToReturn = this.getStack().getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size") - 1;

				if ((source.getAttacker() instanceof PlayerEntity player && player.getMainHandStack().isOf(ModItems.ASTRAL_CONTAINER))) {
					player.getMainHandStack().decrement(1);
					ItemStack retItemStack = this.getStack();
					retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1);
					player.giveItemStack(retItemStack);
					starsToReturn--;
				}
			}

			for (int i = 0; i < starsToReturn + random.nextInt(3); i++) {
				ItemEntity itemEntity = new ItemEntity(serverWorld, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ(), new ItemStack(ModItems.ASTRAL_FRAGMENT));
				Vec3d randomVel = new Vec3d(random.nextGaussian(), random.nextFloat(), random.nextGaussian()).normalize().multiply(.3f);
				itemEntity.setVelocity(randomVel);
				itemEntity.setPickupDelay(20);
				serverWorld.spawnEntity(itemEntity);
			}

			this.kill();
		}

		return super.damage(source, amount);
	}

	@Override
	public void onRemoved() {
		for (int i = 0; i < 3; i++) {
			ParticleBuilders.create(ModParticles.STAR_IMPACT_FLARE)
				.setScale((3f + this.world.random.nextFloat() * 5f))
				.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
				.setAlpha(0, 1f, 0)
				.setAlphaEasing(Easing.EXPO_OUT, Easing.SINE_OUT)
				.enableNoClip()
				.setLifetime(10)
				.spawn(this.world, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ());
		}

		for (int i = 0; i < 100; i++) {
			Vec3f ranMove = new Vec3f((float) random.nextGaussian(), (float) random.nextGaussian(), (float) random.nextGaussian());
			ranMove.normalize();
			ranMove.scale(.5f);
			ParticleBuilders.create(LodestoneParticles.TWINKLE_PARTICLE)
				.setScale((.2f + this.world.random.nextFloat() / 10f))
				.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
				.setAlpha(0, 1f, 0)
				.enableNoClip()
				.setLifetime(20)
				.setSpinOffset(random.nextFloat() * 360f)
				.setSpin((float) (this.world.random.nextGaussian() / 2f))
				.setForcedMotion(ranMove, ranMove)
				.spawn(this.world, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ());
		}

		super.onRemoved();
	}

	public void setStack(ItemStack item) {
		this.getDataTracker().set(ITEM, Util.make(item.copy(), stack -> stack.setCount(1)));
	}

	public ItemStack getStack() {
		return this.getDataTracker().get(ITEM);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		ItemStack itemStack = this.getStack();
		if (!itemStack.isEmpty()) {
			nbt.put("Item", itemStack.writeNbt(new NbtCompound()));
		}

	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("Item"));
		this.setStack(itemStack);
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

}
