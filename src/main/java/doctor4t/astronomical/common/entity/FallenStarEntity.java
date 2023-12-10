package doctor4t.astronomical.common.entity;

import com.sammy.lodestone.setup.LodestoneParticles;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModEntities;
import doctor4t.astronomical.common.init.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
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

		ParticleBuilders.create(ModParticles.FALLEN_STAR)
			.setScale((.8f + this.world.random.nextFloat() / 3f))
			.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
			.setAlpha(0, 0.3f, 0)
			.enableNoClip()
			.setLifetime(20)
			.setSpinOffset(random.nextFloat()*360f)
			.setSpin((float) (this.world.random.nextGaussian() / 100f))
			.spawn(this.world, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ());

		ParticleBuilders.create(ModParticles.STAR_IMPACT_FLARE)
			.setScale((6f + this.world.random.nextFloat() *5f))
			.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
			.setAlpha(0, 0.2f, 0)
			.enableNoClip()
			.setLifetime(20)
			.setSpinOffset(random.nextFloat()*360f)
			.setSpin((float) (this.world.random.nextGaussian() / 100f))
			.spawn(this.world, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ());

		ParticleBuilders.create(ModParticles.FALLEN_STAR)
			.randomOffset(random.nextGaussian()*10f)
			.setScale((.2f + this.world.random.nextFloat() / 10f))
			.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
			.setAlpha(0, .5f, 0)
			.enableNoClip()
			.setLifetime(20 + random.nextInt(20))
			.setSpinOffset(random.nextFloat()*360f)
			.setSpin((float) (this.world.random.nextGaussian() / 50f))
			.spawn(this.world, this.getX(), this.getY() + this.getHeight() / 2f, this.getZ());

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
