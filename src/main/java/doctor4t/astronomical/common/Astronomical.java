package doctor4t.astronomical.common;

import doctor4t.astronomical.common.effects.StargazingStatusEffect;
import doctor4t.astronomical.common.init.ModBlockEntities;
import doctor4t.astronomical.common.init.ModBlocks;
import doctor4t.astronomical.common.init.ModEntities;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.init.ModSoundEvents;
import doctor4t.astronomical.common.screen.AstralDisplayScreenHandler;
import doctor4t.astronomical.common.screen.PlanetColorScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Astronomical implements ModInitializer {
	public static final Vec3d UP = new Vec3d(0, 1, 0);
	public static final String MOD_ID = "astronomical";
	private static final HashMap<Integer, Integer> STAR_TEMPERATURE_COLORS = new HashMap<>();
	static {
		STAR_TEMPERATURE_COLORS.put(10000, 0x1F8CFF);
		STAR_TEMPERATURE_COLORS.put(9000, 0x65B5FF);
		STAR_TEMPERATURE_COLORS.put(8000, 0xA6C2FF);
		STAR_TEMPERATURE_COLORS.put(7000, 0xE2E2FF);
		STAR_TEMPERATURE_COLORS.put(6000, 0xFFE0CC);
		STAR_TEMPERATURE_COLORS.put(5000, 0xFFDB87);
		STAR_TEMPERATURE_COLORS.put(4000, 0xFFBE1C);
		STAR_TEMPERATURE_COLORS.put(3000, 0xFF8800);
		STAR_TEMPERATURE_COLORS.put(2000, 0xFF5500);
		STAR_TEMPERATURE_COLORS.put(1000, 0xFF0000);
	}

	public static final ScreenHandlerType<AstralDisplayScreenHandler> ASTRAL_DISPLAY_SCREEN_HANDLER = Registry.register(Registry.SCREEN_HANDLER, id("astral_display"), new ScreenHandlerType<>(AstralDisplayScreenHandler::new));
	public static final ScreenHandlerType<PlanetColorScreenHandler> PLANET_COLOR_SCREEN_HANDLER = Registry.register(Registry.SCREEN_HANDLER, id("planet_color"), new ScreenHandlerType<>(PlanetColorScreenHandler::new));
	public static final StatusEffect STARGAZING_EFFECT = Registry.register(Registry.STATUS_EFFECT, id("stargazing"), new StargazingStatusEffect(StatusEffectType.BENEFICIAL, 0x6300E5));
    public static final TagKey<Block> HEAT_SOURCES = TagKey.of(Registry.BLOCK_KEY, id("heat_sources"));

    @Override
	public void onInitialize(ModContainer mod) {
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModEntities.initialize();
		ModItems.initialize();
		ModSoundEvents.initialize();
		ModParticles.initialize();

		ServerPlayNetworking.registerGlobalReceiver(id("y_level"), (server, player, handler, buf, responseSender) -> {
			var yLevel = buf.readDouble();
			server.execute(() -> {
				var screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof AstralDisplayScreenHandler astralDisplayScreenHandler) {
					astralDisplayScreenHandler.entity().yLevel.setValue(yLevel);

					var state = astralDisplayScreenHandler.entity().getWorld().getBlockState(astralDisplayScreenHandler.entity().getPos());
					astralDisplayScreenHandler.entity().getWorld().updateListeners(astralDisplayScreenHandler.entity().getPos(), state, state, Block.NOTIFY_LISTENERS);

					astralDisplayScreenHandler.entity().markDirty();
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(id("rot_speed"), (server, player, handler, buf, responseSender) -> {
			var rotSpeed = buf.readDouble();
			server.execute(() -> {
				var screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof AstralDisplayScreenHandler astralDisplayScreenHandler) {
					astralDisplayScreenHandler.entity().rotSpeed.setValue(rotSpeed);

					var state = astralDisplayScreenHandler.entity().getWorld().getBlockState(astralDisplayScreenHandler.entity().getPos());
					astralDisplayScreenHandler.entity().getWorld().updateListeners(astralDisplayScreenHandler.entity().getPos(), state, state, Block.NOTIFY_LISTENERS);

					astralDisplayScreenHandler.entity().markDirty();
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(id("spin"), (server, player, handler, buf, responseSender) -> {
			var spin = buf.readDouble();
			server.execute(() -> {
				var screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof AstralDisplayScreenHandler astralDisplayScreenHandler) {
					astralDisplayScreenHandler.entity().spin.setValue(spin);

					var state = astralDisplayScreenHandler.entity().getWorld().getBlockState(astralDisplayScreenHandler.entity().getPos());
					astralDisplayScreenHandler.entity().getWorld().updateListeners(astralDisplayScreenHandler.entity().getPos(), state, state, Block.NOTIFY_LISTENERS);

					astralDisplayScreenHandler.entity().markDirty();
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(id("holding"), (server, player, handler, buf, responseSender) -> {
			var holding = buf.readBoolean();
			server.execute(() -> player.astronomical$setHoldingAttack(holding));
		});
	}

	public static @NotNull Vec3d rotateViaQuat(@NotNull Vec3d rot, @NotNull Quaternion quat) {
		float x = (float) rot.x;
		float y = (float) rot.y;
		float z = (float) rot.z;

		float ux = quat.getX();
		float uy = quat.getY();
		float uz = quat.getZ();

		float scalar = quat.getW();

		float cx = -y*uz+(z*uy);
		float cy = -z*ux+(x*uz);
		float cz = -x*uy+(y*ux);

		double s1 = 2.0f * (ux*x + uy*y + uz*z);
		double s2 = scalar*scalar - (ux*ux + uy*uy + uz*uz);
		double s3 = 2.0f * scalar;

		double vpx = s1 * ux + s2 * x + s3 * cx;
		double vpy = s1 * uy + s2 * y + s3 * cy;
		double vpz = s1 * uz + s2 * z + s3 * cz;

		return new Vec3d(vpx, vpy, vpz);
	}

	public static @NotNull Quaternion invert(@NotNull Quaternion in) {
		float invNorm = 1.0f / Math.fma(in.getX(), in.getX(), Math.fma(in.getY(), in.getY(), Math.fma(in.getZ(), in.getZ(), in.getW() * in.getW())));
		return new Quaternion(-in.getX() * invNorm, -in.getY() * invNorm, -in.getZ() * invNorm, in.getW() * invNorm);
	}

	public static @NotNull Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	public static int getStarColorForTemperature(int temperature) {
		if (STAR_TEMPERATURE_COLORS.containsKey(temperature)) {
			return STAR_TEMPERATURE_COLORS.get(temperature);
		}
		return 0;
	}

	public static int getRandomStarTemperature(RandomGenerator randomGenerator) {
		List<Integer> temperatures = new ArrayList<>(Astronomical.STAR_TEMPERATURE_COLORS.keySet());
		return temperatures.get(randomGenerator.nextInt(temperatures.size()));
	}
	public static int getRandomStarTemperature(float delta) {
		List<Integer> temperatures = new ArrayList<>(Astronomical.STAR_TEMPERATURE_COLORS.values());
		return temperatures.get(MathHelper.ceil(delta * temperatures.size()) - 1);
	}
}
