package doctor4t.astronomical.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sammy.lodestone.systems.rendering.particle.SimpleParticleEffect;
import com.sammy.lodestone.systems.rendering.particle.world.FrameSetParticle;
import com.sammy.lodestone.systems.rendering.particle.world.WorldParticleEffect;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;

public class OrbParticle extends FrameSetParticle {
	public OrbParticle(ClientWorld world, WorldParticleEffect data, FabricSpriteProviderImpl spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, data, spriteSet, x, y, z, xd, yd, zd);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public SimpleParticleEffect.Animator getAnimator() {
		return SimpleParticleEffect.Animator.FIRST_INDEX;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		//RenderSystem.disableDepthTest();
		//RenderSystem.depthMask(false);
		super.buildGeometry(vertexConsumer, camera, tickDelta);
		//RenderSystem.enableDepthTest();
		//RenderSystem.depthMask(true);
	}
}
