package doctor4t.astronomical.mixin;

import com.sammy.lodestone.systems.rendering.VFXBuilders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VFXBuilders.WorldVFXBuilder.class, remap = false)
public interface WorldVFXBuilderAccessor {
	@Accessor("supplier")
	VFXBuilders.WorldVFXBuilder.WorldVertexPlacementSupplier supplier();

	@Accessor("r")
	float r();

	@Accessor("g")
	float g();

	@Accessor("b")
	float b();

	@Accessor("a")
	float a();

	@Accessor("xOffset")
	float xOffset();

	@Accessor("yOffset")
	float yOffset();

	@Accessor("zOffset")
	float zOffset();

	@Accessor("light")
	int light();

	@Accessor("u0")
	float u0();

	@Accessor("v0")
	float v0();

	@Accessor("u1")
	float u1();

	@Accessor("v1")
	float v1();
}
