package doctor4t.astronomical.common.structure;

import doctor4t.astronomical.common.init.ModSoundEvents;
import doctor4t.astronomical.common.util.Vec2d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Starfall {
	public final Vec3d startDirection, endPos;
	public int progress = 0;
	public int color;
	public int ticksUntilLanded;

	public Starfall(NbtCompound n) {
		this.startDirection = new Vec3d(n.getDouble("x"), n.getDouble("y"), n.getDouble("z"));
		this.endPos = new Vec3d(n.getDouble("tgtX"), n.getDouble("tgtY"), n.getDouble("tgtZ"));
		this.progress = n.getInt("prog");
		this.color = n.getInt("color");
		this.ticksUntilLanded = n.getInt("ticksUntilLanded");
	}

	public Starfall(int ticksUntilLanded, int color, Vec3d startDirection, Vec3d end) {
		this.ticksUntilLanded = ticksUntilLanded;
		this.color = color;
		this.startDirection = startDirection;
		this.endPos = end;
	}

	private static Vec2d compressDirectionalVector(Vec3d directionalVector) {
		return new Vec2d(Math.asin(-directionalVector.y), Math.atan2(directionalVector.x, directionalVector.z));
	}

	public void writeNbt(NbtCompound nbt) {
		nbt.putInt("prog", progress);
		nbt.putInt("color", color);
		nbt.putInt("ticksUntilLanded", ticksUntilLanded);
		nbt.putDouble("tgtX", endPos.x);
		nbt.putDouble("tgtY", endPos.y);
		nbt.putDouble("tgtZ", endPos.z);
		nbt.putDouble("x", startDirection.x);
		nbt.putDouble("y", startDirection.y);
		nbt.putDouble("z", startDirection.z);
	}

	public void tick(World world) {
		progress++;
		if (progress == ticksUntilLanded && world instanceof ServerWorld serverWorld) {
			//TODO spawn stuff here
			serverWorld.playSound(null, endPos.getX(), endPos.getY(), endPos.getZ(), ModSoundEvents.STAR_IMPACT, SoundCategory.AMBIENT, 20f, (float) (1f + world.random.nextGaussian() * .1f));

//			LightningEntity l = new LightningEntity(EntityType.LIGHTNING_BOLT, serverWorld);
//			l.setPosition(endPos);
//			l.setCosmetic(true);
//			world.spawnEntity(l);
		}
	}
}
