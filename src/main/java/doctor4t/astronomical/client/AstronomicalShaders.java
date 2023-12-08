package doctor4t.astronomical.client;

import com.mojang.blaze3d.vertex.VertexFormats;
import com.mojang.datafixers.util.Pair;
import com.sammy.lodestone.systems.rendering.ExtendedShader;
import com.sammy.lodestone.systems.rendering.ShaderHolder;
import doctor4t.astronomical.common.Astronomical;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.resource.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AstronomicalShaders {
	public static List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shaderList;
	public static ShaderHolder TRANSPARENT_NO_CULL_TEXTURE = new ShaderHolder();

	public static void init(ResourceManager manager) throws IOException {
		shaderList = new ArrayList<>();
		registerShader(ExtendedShader.createShaderInstance(TRANSPARENT_NO_CULL_TEXTURE, manager, Astronomical.id("transparent_no_cull_texture"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));
	}

	public static void registerShader(ExtendedShader extendedShaderInstance) {
		registerShader(extendedShaderInstance, (shader) -> ((ExtendedShader) shader).getHolder().setInstance((ExtendedShader) shader));
	}

	public static void registerShader(ShaderProgram shader, Consumer<ShaderProgram> onLoaded) {
		shaderList.add(Pair.of(shader, onLoaded));
	}
}
