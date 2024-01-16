package doctor4t.astronomical.client;

import com.mojang.blaze3d.vertex.VertexFormats;
import com.mojang.datafixers.util.Pair;
import doctor4t.astronomical.common.Astronomical;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.resource.ResourceFactory;
import team.lodestar.lodestone.systems.rendering.ExtendedShaderProgram;
import team.lodestar.lodestone.systems.rendering.ShaderHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AstronomicalShaders {
	public static List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shaderList;
	public static ShaderHolder TRANSPARENT_NO_CULL_TEXTURE = new ShaderHolder();

	public static void init(ResourceFactory factory) throws IOException {
		shaderList = new ArrayList<>();
		registerShader(ExtendedShaderProgram.createShaderProgram(TRANSPARENT_NO_CULL_TEXTURE, factory, Astronomical.id("transparent_no_cull_texture"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));
	}

	public static void registerShader(ExtendedShaderProgram extendedShaderInstance) {
		registerShader(extendedShaderInstance, (shader) -> ((ExtendedShaderProgram) shader).getHolder().setInstance((ExtendedShaderProgram) shader));
	}

	public static void registerShader(ShaderProgram shader, Consumer<ShaderProgram> onLoaded) {
		shaderList.add(Pair.of(shader, onLoaded));
	}
}
