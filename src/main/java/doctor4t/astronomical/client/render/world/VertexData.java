package doctor4t.astronomical.client.render.world;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

import java.awt.*;

public record VertexData(Vec3f[] vertices, Color color, Vec2f[] uv) {
}
