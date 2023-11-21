package doctor4t.astronomical.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;

public class AstraClientUtil {
	public static long extractSeed() {
		return MinecraftClient.getInstance().getServer() == null ? 0L : MinecraftClient.getInstance().getServer().getSaveProperties().getGeneratorOptions().getSeed();
	}
}
