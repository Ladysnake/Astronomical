package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.item.*;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import xyz.amymialee.mialeemisc.itemgroup.MialeeItemGroup;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
	MialeeItemGroup ASTRONOMICAL_ITEM_GROUP = MialeeItemGroup.create(Astronomical.id("astronomical"));
	Item ASTRAL_FRAGMENT = createItem("astral_fragment", new Item(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP)));
	Item ASTRAL_CONTAINER = createItem("astral_container", new Item(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP)));
	Item ASTRAL_BUNDLE_PLANET = createItem("astral_bundle_planet", new AstralBundleItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP), AstralBundleItem.Type.PLANET));
	Item ASTRAL_BUNDLE_STAR = createItem("astral_bundle_star", new AstralBundleItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP), AstralBundleItem.Type.STAR));
	Item ASTRAL_BUNDLE_COSMOS = createItem("astral_bundle_cosmos", new AstralBundleItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP), AstralBundleItem.Type.COSMOS));
	Item ASTRAL_BUNDLE_RING = createItem("astral_bundle_ring", new AstralBundleItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP), AstralBundleItem.Type.RING));
	Item NANO_PLANET = createItem("nano_planet", new NanoPlanetItem(new Item.Settings()));
	Item NANO_STAR = createItem("nano_star", new NanoStarItem(new Item.Settings()));
	Item NANO_COSMOS = createItem("nano_cosmos", new NanoCosmosItem(new Item.Settings()));
	Item NANO_RING = createItem("nano_ring", new NanoRingItem(new Item.Settings()));

	Item MARSHMALLOW = createItem("marshmallow", new Item(new QuiltItemSettings().food(new FoodComponent.Builder().snack().hunger(2).saturationModifier(0.1F).alwaysEdible().build()).group(ASTRONOMICAL_ITEM_GROUP)));
	Item MARSHMALLOW_STICK = createItem("marshmallow_stick", new MarshmallowStickItem(new QuiltItemSettings().maxCount(1).maxDamage(1).group(ASTRONOMICAL_ITEM_GROUP)));

	private static <T extends Item> T createItem(String name, T item) {
		ITEMS.put(item, Astronomical.id(name));
		return item;
	}

	static void initialize() {
		ITEMS.keySet().forEach(item -> Registry.register(Registry.ITEM, ITEMS.get(item), item));
		initItemGroups();
	}

	static void initItemGroups() {
		ASTRONOMICAL_ITEM_GROUP.setItems((itemStacks, itemGroup) -> {
			// additional vanilla items that fit in the category
			itemStacks.add(Items.SPYGLASS.getDefaultStack());

			for (var item : ITEMS.keySet()) {
				if (item == Items.AIR || item.getGroup() == null || !item.getGroup().equals(ModItems.ASTRONOMICAL_ITEM_GROUP))
					continue;
				if (item == MARSHMALLOW_STICK) {
					for (var state : MarshmallowStickItem.CookState.values()) {
						var stack = new ItemStack(item, 1);
						stack.getOrCreateNbt().putFloat("RoastTicks", state.cookTime);
						itemStacks.add(stack);
					}
					continue;
				}
				itemStacks.add(item.getDefaultStack());
			}
		});
		ASTRONOMICAL_ITEM_GROUP.setIcon(ASTRAL_FRAGMENT.asItem().getDefaultStack());
	}
}
