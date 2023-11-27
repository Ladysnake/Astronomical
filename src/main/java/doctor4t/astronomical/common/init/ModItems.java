package doctor4t.astronomical.common.init;

import com.terraformersmc.modmenu.util.mod.Mod;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.item.NanoCosmosItem;
import doctor4t.astronomical.common.item.AstralBundleItem;
import doctor4t.astronomical.common.item.NanoPlanetItem;
import doctor4t.astronomical.common.item.NanoStarItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import xyz.amymialee.mialeemisc.itemgroup.MialeeItemGroup;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();
	MialeeItemGroup ASTRONOMICAL_ITEM_GROUP = MialeeItemGroup.create(Astronomical.id("astronomical"));
	Item ASTRAL_BUNDLE_PLANET = createItem("astral_bundle_planet", new AstralBundleItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP), AstralBundleItem.Type.PLANET));
	Item ASTRAL_BUNDLE_STAR = createItem("astral_bundle_star", new AstralBundleItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP), AstralBundleItem.Type.STAR));
	Item ASTRAL_BUNDLE_COSMOS = createItem("astral_bundle_cosmos", new AstralBundleItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP), AstralBundleItem.Type.COSMOS));
	Item NANO_PLANET = createItem("nano_planet", new NanoPlanetItem(new Item.Settings()));
	Item NANO_STAR = createItem("nano_star", new NanoStarItem(new Item.Settings()));
	Item NANO_COSMOS = createItem("nano_cosmos", new NanoCosmosItem(new Item.Settings()));

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
			for (Item item : ITEMS.keySet()) {
				if (item == Items.AIR || item.getGroup() == null || !item.getGroup().equals(ModItems.ASTRONOMICAL_ITEM_GROUP)) continue;
				itemStacks.add(item.getDefaultStack());
			}
		});
		ASTRONOMICAL_ITEM_GROUP.setIcon(ASTRAL_BUNDLE_PLANET.asItem().getDefaultStack());
	}
}
