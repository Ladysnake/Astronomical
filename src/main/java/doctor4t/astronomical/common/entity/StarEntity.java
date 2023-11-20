package doctor4t.astronomical.common.entity;

import doctor4t.astronomical.common.init.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class StarEntity extends Entity {
	public StarEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	public StarEntity(World world, double x, double y, double z) {
		this(ModEntities.STAR, world);
		this.setPosition(x, y, z);
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
	protected MoveEffect getMoveEffect() {
		return MoveEffect.NONE;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
