package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.awt.*;

public class AstralBundleItem extends Item {
	public Type type;

	public AstralBundleItem(Settings settings, Type type) {
		super(settings);
		this.type = type;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

		ItemStack retItemStack = ItemStack.EMPTY;

		switch (type) {
			case PLANET -> {
				retItemStack = new ItemStack(ModItems.NANO_PLANET);
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color1", new Color(user.getRandom().nextFloat(), user.getRandom().nextFloat(), user.getRandom().nextFloat()).getRGB());
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color2", new Color(user.getRandom().nextFloat(), user.getRandom().nextFloat(), user.getRandom().nextFloat()).getRGB());
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1 + user.getRandom().nextInt(10));
				String texture = NanoPlanetItem.PlanetTexture.getRandom().name();
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putString("texture", texture);
			}
			case STAR -> {
				retItemStack = new ItemStack(ModItems.NANO_STAR);
				int temp = Astronomical.getRandomStarTemperature(user.getRandom());
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("temperature", temp);
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1 + user.getRandom().nextInt(10));
			}
			case COSMOS -> {
				retItemStack = new ItemStack(ModItems.NANO_COSMOS);
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1 + user.getRandom().nextInt(100));
				String texture = NanoCosmosItem.CosmosTexture.getRandom().name();
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putString("texture", texture);
			}
			case RING -> {
				retItemStack = new ItemStack(ModItems.NANO_RING);
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1 + user.getRandom().nextInt(20));
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color", new Color(user.getRandom().nextFloat(), user.getRandom().nextFloat(), user.getRandom().nextFloat()).getRGB());
				String texture = NanoRingItem.RingTexture.getRandom().name();
				retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putString("texture", texture);
			}
		}

		user.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 1.0f, 1.0f);
		user.giveItemStack(retItemStack);
		return TypedActionResult.success(user.getStackInHand(hand));
	}

	public enum Type {
		PLANET, STAR, COSMOS, RING
	}
}
