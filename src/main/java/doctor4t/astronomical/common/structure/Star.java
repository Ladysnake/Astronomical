package doctor4t.astronomical.common.structure;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Base class used to represent a star.
 **/
public abstract class Star {
	public final Color start, end;

	protected Star(Color start, Color end) {
		this.start = start;
		this.end = end;
	}

	public abstract Vec3d getPos();
	public abstract void emit(Vec3d position, World clientWorld, ClientPlayerEntity view);
}
