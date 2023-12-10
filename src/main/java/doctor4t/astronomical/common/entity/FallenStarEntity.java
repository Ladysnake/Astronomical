package doctor4t.astronomical.common.entity;

import com.sammy.lodestone.setup.LodestoneParticles;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModEntities;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.init.ModSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class FallenStarEntity extends Entity {
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

		if (world.isDay()) {
			this.damage(DamageSource.GENERIC, 1.0f);
		}

		ParticleBuilders.create(ModParticles.FALLEN_STAR)
			.setScale((.8f + this.world.random.nextFloat() / 3f))
			.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
			.setAlpha(0, 0.3f, 0)
			.enableNoClip()
			.setLifetime(20)
			.setSpinOffset(random.nextFloat() * 360f)
			.setSpin((float) (this.world.random.nextGaussian() / 100f))
			.spawn(this.world, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ());

		ParticleBuilders.create(ModParticles.STAR_IMPACT_FLARE)
			.setScale((6f + this.world.random.nextFloat() * 5f))
			.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
			.setAlpha(0, 0.2f, 0)
			.enableNoClip()
			.setLifetime(20)
			.setSpinOffset(random.nextFloat() * 360f)
			.setSpin((float) (this.world.random.nextGaussian() / 100f))
			.spawn(this.world, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ());

		ParticleBuilders.create(ModParticles.FALLEN_STAR)
			.randomOffset(random.nextGaussian() * 10f)
			.setScale((.2f + this.world.random.nextFloat() / 10f))
			.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
			.setAlpha(0, .5f, 0)
			.enableNoClip()
			.setLifetime(20 + random.nextInt(20))
			.setSpinOffset(random.nextFloat() * 360f)
			.setSpin((float) (this.world.random.nextGaussian() / 50f))
			.spawn(this.world, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ());

	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.world instanceof ServerWorld serverWorld) {
			serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(), ModSoundEvents.STAR_BREAK, SoundCategory.NEUTRAL, 2f, (float) (1f + world.random.nextGaussian() * .1f));
			for (int i = 0; i < 1 + random.nextInt(3); i++) {
				ItemEntity itemEntity = new ItemEntity(serverWorld, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ(), new ItemStack(ModItems.ASTRAL_FRAGMENT));
				Vec3d randomVel = new Vec3d(random.nextGaussian(), random.nextFloat(), random.nextGaussian()).normalize().multiply(.3f);
				itemEntity.setVelocity(randomVel);
				itemEntity.setPickupDelay(10);
				serverWorld.spawnEntity(itemEntity);
			}
			this.discard();
		} else {
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
					.setForcedMotion(ranMove,ranMove)
					.spawn(this.world, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ());
			}
		}

		return super.damage(source, amount);
	}

	@Override
	protected void initDataTracker() {

	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {

	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {

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
