package doctor4t.astronomical.client;

import doctor4t.astronomical.client.render.entity.StarEntityRenderer;
import doctor4t.astronomical.common.init.ModEntities;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.structure.Star;
import doctor4t.astronomical.common.structure.TestStar;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientWorldTickEvents;

import java.awt.*;

public class AstronomicalClient implements ClientModInitializer {

	//https://www.youtube.com/watch?v=phWWx4NRhpE
	private static final Star testStar = new TestStar(new Vec3d(80, 0, 100), Color.WHITE, Color.RED);
	@Override
	public void onInitializeClient(ModContainer mod) {
		// init model layers
//		ModModelLayers.initialize();

		// entity renderers registration
		EntityRendererRegistry.register(ModEntities.STAR, StarEntityRenderer::new);

		// particle renderers registration
		ModParticles.registerFactories();

		// block special layers
//		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlocks.LOCKER, ModBlocks.CLOSED_LOCKER, ModBlocks.WALKWAY, ModBlocks.WALKWAY_STAIRS, ModBlocks.ABYSSTEEL_CHAIN, ModBlocks.ANGLERWEED, ModBlocks.ANGLERWEED_PLANT, ModBlocks.LURKING_LAMP, ModBlocks.OCTANT);
		ClientWorldTickEvents.START.register(AstronomicalClient::emitStars);
	}

	private static void emitStars(MinecraftClient cli, ClientWorld world) {
		//entity position to keep all stars centred around the player
		Vec3d origin = cli.cameraEntity != null ? cli.cameraEntity.getEyePos() : Vec3d.ZERO;
		//rotate according to how the sky rotates ingame
		Quaternion rotation = Vec3f.POSITIVE_Z.getDegreesQuaternion(world.getSkyAngle(1) * 360.0F);

		emitStar(testStar, origin, cli, world, rotation);
	}
	//because of how particles work, the star subtly trails as of now - an actor-like system that updates the position as relative to the player would allay this issue (that or switching to fully custom rendering.
	private static void emitStar(Star star, Vec3d origin, MinecraftClient cli, ClientWorld world, Quaternion rotato) {
		Vec3d emissionPos = origin.add(rotateViaQuat(star.getPos(), rotato));

		star.emit(emissionPos, world, cli.player);
	}

	public static Vec3d rotateViaQuat(Vec3d rot, Quaternion quat) {
		Quaternion q = quat.copy();
		Quaternion qPrime = new Quaternion(-q.getX(), -q.getY(), -q.getZ(), q.getW());
		q.hamiltonProduct(new Quaternion((float)rot.getX(), (float)rot.getY(), (float)rot.getZ(), 0));
		q.hamiltonProduct(qPrime);
		return new Vec3d(q.getX(), q.getY(), q.getZ());
	}

}
