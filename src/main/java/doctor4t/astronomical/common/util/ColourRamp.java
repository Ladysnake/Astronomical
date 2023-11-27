package doctor4t.astronomical.common.util;

import com.sammy.lodestone.systems.rendering.particle.Easing;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;

public class ColourRamp {
	private final Color start, end;
	private final Easing ease;

	public ColourRamp(Color start, Color end, Easing ease) {
		this.start = start;
		this.end = end;
		this.ease = ease;
	}
	public Color ease(float delta) {
		delta = ease.ease(delta, 0, 1, 1);
		float iDelta = 1-delta;
		int r1 = start.getRed();
		int g1 = start.getGreen();
		int b1 = start.getBlue();
		r1 *= r1; g1 *= g1; b1 *= b1;

		int r2 = end.getRed();
		int g2 = end.getGreen();
		int b2 = end.getBlue();
		r2 *= r2; g2 *= g2; b2 *= b2;

		return new Color(MathHelper.sqrt(r1*iDelta + r2*delta)/255f, MathHelper.sqrt(g1*iDelta + g2*delta)/255f, MathHelper.sqrt(b1*iDelta + b2*delta)/255f);
	}
}
