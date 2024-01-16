package doctor4t.astronomical.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

@Mixin(value = VFXBuilders.WorldVFXBuilder.class, remap = false)
public interface WorldVFXBuilderAccessor {
	@Accessor("supplier")
	VFXBuilders.WorldVFXBuilder.WorldVertexPlacementSupplier astronomical$getSupplier();

	@Accessor("r")
	float astronomical$getR();

	@Accessor("g")
	float astronomical$getG();

	@Accessor("b")
	float astronomical$getB();

	@Accessor("a")
	float astronomical$getA();

	@Accessor("xOffset")
	float astronomical$getXOffset();

	@Accessor("yOffset")
	float astronomical$getYOffset();

	@Accessor("zOffset")
	float astronomical$getZOffset();

	@Accessor("light")
	int astronomical$getLight();

	@Accessor("u0")
	float astronomical$getU0();

	@Accessor("v0")
	float astronomical$getV0();

	@Accessor("u1")
	float astronomical$getU1();

	@Accessor("u1")
	float astronomical$getV1();
}
