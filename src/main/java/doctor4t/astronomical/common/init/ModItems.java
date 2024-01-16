package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
	Set<Item> NO_GROUP = new HashSet<>();
	Item ASTRAL_FRAGMENT = createItem("astral_fragment", new Item(new Item.Settings()));
	Item ASTRAL_CONTAINER = createItem("astral_container", new Item(new Item.Settings()));
	Item ASTRAL_BUNDLE_PLANET = createItem("astral_bundle_planet", new AstralBundleItem(new Item.Settings(), AstralBundleItem.Type.PLANET));
	Item ASTRAL_BUNDLE_STAR = createItem("astral_bundle_star", new AstralBundleItem(new Item.Settings(), AstralBundleItem.Type.STAR));
	Item ASTRAL_BUNDLE_COSMOS = createItem("astral_bundle_cosmos", new AstralBundleItem(new Item.Settings(), AstralBundleItem.Type.COSMOS));
	Item ASTRAL_BUNDLE_RING = createItem("astral_bundle_ring", new AstralBundleItem(new Item.Settings(), AstralBundleItem.Type.RING));
	Item NANO_PLANET = createItem("nano_planet", new NanoPlanetItem(new Item.Settings().maxCount(1)), true);
	Item NANO_STAR = createItem("nano_star", new NanoStarItem(new Item.Settings().maxCount(1)), true);
	Item NANO_COSMOS = createItem("nano_cosmos", new NanoCosmosItem(new Item.Settings().maxCount(1)), true);
	Item NANO_RING = createItem("nano_ring", new NanoRingItem(new Item.Settings().maxCount(1)), true);
	Item THE_EYE_OF_THE_UNIVERSE = createItem("the_eye_of_the_universe", new TheEyeOfTheUniverseItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)), true);

	Item MARSHMALLOW = createItem("marshmallow", new Item(new QuiltItemSettings().food(new FoodComponent.Builder().snack().hunger(2).saturationModifier(0.1F).alwaysEdible().build())));
	Item MARSHMALLOW_STICK = createItem("marshmallow_stick", new MarshmallowStickItem(new QuiltItemSettings().maxCount(1).maxDamage(1)));
	Item STARMALLOW = createItem("starmallow", new Item(new QuiltItemSettings().food(new FoodComponent.Builder().snack().hunger(4).saturationModifier(0.2F).alwaysEdible().build())));
	Item STARMALLOW_STICK = createItem("starmallow_stick", new MarshmallowStickItem(new QuiltItemSettings().maxCount(1).maxDamage(1)));

	ItemStack CREATIVE_TAB_ASTRAL_FRAGMENT = new ItemStack(ASTRAL_FRAGMENT);

	private static <T extends Item> T createItem(String name, T item, boolean noGroup) {
		ITEMS.put(item, Astronomical.id(name));
		if (noGroup) {
			NO_GROUP.add(item);
		}
		return item;
	}

	private static <T extends Item> T createItem(String name, T item) {
		return createItem(name, item, false);
	}

	static void initialize() {
		ITEMS.keySet().forEach(item -> Registry.register(Registries.ITEM, ITEMS.get(item), item));
		initItemGroups();
	}

	static void initItemGroups() {
		Astronomical.ASTRONOMICAL_ITEM_GROUP = FabricItemGroup.builder().name(Text.translatable("itemGroup.astronomical.astronomical")).icon(() -> CREATIVE_TAB_ASTRAL_FRAGMENT).entries((displayParameters, itemStackCollector) -> ITEMS.keySet().forEach(item -> {
			if (!NO_GROUP.contains(item)) {
				if (item instanceof MarshmallowStickItem) {
					for (var state : MarshmallowStickItem.CookState.values()) {
						var stack = new ItemStack(item, 1);
						stack.getOrCreateNbt().putFloat("RoastTicks", state.cookTime);
						itemStackCollector.addStack(stack);
					}
				}
				else {
					itemStackCollector.addItem(item);
				}
			}
		})).build();
		Registry.register(Registries.ITEM_GROUP, Astronomical.id(Astronomical.MOD_ID), Astronomical.ASTRONOMICAL_ITEM_GROUP);
	}
}
