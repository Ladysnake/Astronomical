package doctor4t.astronomical.client.render.world;

import net.minecraft.util.math.Vec2f;
import org.joml.Vector3f;

import java.awt.*;

public record VertexData(Vector3f[] vertices, Color[] color, Vec2f[] uv) {
}
