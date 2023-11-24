package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.item.NanoPlanetItem;
import doctor4t.astronomical.common.item.NanoStarItem;
import doctor4t.astronomical.common.item.NanoGiverItem;
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
	Item NANO_PLANET = createItem("nano_planet", new NanoPlanetItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP)));
	Item NANO_STAR = createItem("nano_star", new NanoStarItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP)));
	Item NANO_GIVER = createItem("nano_giver", new NanoGiverItem(new Item.Settings().group(ASTRONOMICAL_ITEM_GROUP)));

	private static <T extends Item> T createItem(String name, T item) {
		ITEMS.put(item, Astronomical.id(name));
		return item;
	}

	static void initialize() {
		ITEMS.keySet().forEach(item -> Registry.register(Registry.ITEM, ITEMS.get(item), item));
		initItemGroups();
	}

	static void initItemGroups() {
		DefaultedList<ItemStack> stacks = DefaultedList.of();
		ITEMS.keySet().forEach(item -> stacks.add(new ItemStack(item)));
		ASTRONOMICAL_ITEM_GROUP.setItems((itemStacks, itemGroup) -> {
			for (Item item : ITEMS.keySet()) {
				if (item == Items.AIR) continue;
				itemStacks.add(item.getDefaultStack());
			}
		});
		ASTRONOMICAL_ITEM_GROUP.setIcon(Items.NETHER_STAR.asItem().getDefaultStack());
	}
}
