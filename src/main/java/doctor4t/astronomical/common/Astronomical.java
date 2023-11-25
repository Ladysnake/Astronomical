package doctor4t.astronomical.common;

import doctor4t.astronomical.common.init.ModBlockEntities;
import doctor4t.astronomical.common.init.ModBlocks;
import doctor4t.astronomical.common.init.ModEntities;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.init.ModSoundEvents;
import doctor4t.astronomical.common.screen.AstralDisplayScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class Astronomical implements ModInitializer {
	public static final String MOD_ID = "astronomical";

	public static final ScreenHandlerType<AstralDisplayScreenHandler> ASTRAL_DISPLAY_SCREEN_HANDLER = Registry.register(Registry.SCREEN_HANDLER, id("astral_display"), new ScreenHandlerType<>(AstralDisplayScreenHandler::new));

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
					astralDisplayScreenHandler.entity().yLevel = yLevel;
					astralDisplayScreenHandler.entity().markDirty();
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(id("rot_speed"), (server, player, handler, buf, responseSender) -> {
			var rotSpeed = buf.readDouble();
			server.execute(() -> {
				var screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof AstralDisplayScreenHandler astralDisplayScreenHandler) {
					astralDisplayScreenHandler.entity().rotSpeed = rotSpeed;
					astralDisplayScreenHandler.entity().markDirty();
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(id("spin"), (server, player, handler, buf, responseSender) -> {
			var spin = buf.readDouble();
			server.execute(() -> {
				var screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof AstralDisplayScreenHandler astralDisplayScreenHandler) {
					astralDisplayScreenHandler.entity().spin = spin;
					astralDisplayScreenHandler.entity().markDirty();
				}
			});
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
