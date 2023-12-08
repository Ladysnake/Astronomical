package doctor4t.astronomical.common.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface BlockCastFinder {
	static @NotNull List<BlockPos> castRayForGridPoints(Vec3d startPosition, @NotNull Vec3d direction, double radius, double maxDistance) {
		Set<BlockPos> intersectedPoints = new HashSet<>();
		var currentPosition = startPosition;
		var normalizedDirection = direction.normalize();
		for (double d = 0; d <= maxDistance; d += 0.2) {
			currentPosition = startPosition.add(normalizedDirection.multiply(d));
			addGridPointsWithinRadius(currentPosition, radius, intersectedPoints);
		}
		return new ArrayList<>(intersectedPoints);
	}

	static void addGridPointsWithinRadius(@NotNull Vec3d position, double radius, Set<BlockPos> intersectedPoints) {
		var minX = (int) Math.floor(position.x - radius);
		var maxX = (int) Math.ceil(position.x + radius);
		var minY = (int) Math.floor(position.y - radius);
		var maxY = (int) Math.ceil(position.y + radius);
		var minZ = (int) Math.floor(position.z - radius);
		var maxZ = (int) Math.ceil(position.z + radius);
		for (var x = minX; x <= maxX; x++) {
			for (var y = minY; y <= maxY; y++) {
				for (var z = minZ; z <= maxZ; z++) {
					var gridPoint = new BlockPos(x, y, z);
					if (isWithinRadius(position, gridPoint, radius)) {
						intersectedPoints.add(gridPoint);
					}
				}
			}
		}
	}

	static boolean isWithinRadius(@NotNull Vec3d position, @NotNull BlockPos gridPoint, double radius) {
		var dx = position.x - (gridPoint.getX() + 0.5);
		var dy = position.y - (gridPoint.getY() + 0.5);
		var dz = position.z - (gridPoint.getZ() + 0.5);
		var distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		return distance <= radius;
	}
}
