package crunchFarmCore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class FarmGUI implements Listener {
	String Path;
	Plugin plugin;
	private static HashMap<UUID, Inventory> inventories = new HashMap<UUID, Inventory>();

	public FarmGUI(String path, Plugin plugin) {
		// Create a new inventory, with no owner (as this isn't a real inventory), a
		// size of nine, called example
		this.Path = path;
		this.plugin = plugin;
		// Put the items into the inventory

	}

	public ItemStack CreateBag(int tier) {
		ItemStack item = null;
		ItemMeta meta;
		ArrayList<String> lore = new ArrayList<String>();
		switch (tier) {
		case 1:
			item = new ItemStack(Material.RAW_COPPER);
			meta = item.getItemMeta();
			meta.setDisplayName("§6Tier 1 Relic Bag");
			lore.add("§aContains Bronze and Silver Relics");
			lore.add("§aCosts 20 1 Star Points.");
			meta.setLore(lore);
			item.setItemMeta(meta);
			break;

		case 2:
			item = new ItemStack(Material.RAW_IRON);
			meta = item.getItemMeta();
			meta.setDisplayName("§6Tier 2 Relic Bag");
			lore.add("§aContains Bronze, Silver");
			lore.add("§aand Gold Relics");
			lore.add("§aCosts 20 2 Star Points.");
			meta.setLore(lore);
			item.setItemMeta(meta);
			break;

		case 3:
			item = new ItemStack(Material.RAW_GOLD);
			meta = item.getItemMeta();
			meta.setDisplayName("§6Tier 3 Relic Bag");
			lore.add("§aContains Bronze, Silver,");
			lore.add("§aGold and Ancient Relics");
			lore.add("§aCosts 20 3 Star Points.");
			meta.setLore(lore);
			item.setItemMeta(meta);
			break;

		}

		return item;
	}

	public Inventory initializeItems(HumanEntity ent, PlayerData data) {
		Inventory inv = Bukkit.createInventory(null, 54, "");

		Player player = (Player) ent;
		inv.setItem(4, CreateHead(ent, data));
		inv.setItem(10, CreateCrop(Material.WHEAT, data));
		inv.setItem(12, CreateCrop(Material.POTATO, data));
		inv.setItem(14, CreateCrop(Material.CARROT, data));
		inv.setItem(16, CreateCrop(Material.BEETROOT, data));
		inv.setItem(20, CreateCrop(Material.PUMPKIN, data));
		inv.setItem(24, CreateCrop(Material.MELON, data));

		inv.setItem(22, CreateUpgrade(data));
		ItemStack hoe = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta meta = hoe.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		meta.setDisplayName("§2Tier 5 Hoe");
		lore.add("§aPurchase for 50 1 Star, 2 Star");
		lore.add("§aand 3 Star points and 30 Gold Relics.");
		meta.setLore(lore);
		hoe.setItemMeta(meta);

		this.hoe = hoe;
		if (!data.BoughtHoe) {
			inv.setItem(30, hoe);
		}
		ItemStack title = new ItemStack(Material.NAME_TAG);
		ItemMeta meta2 = title.getItemMeta();
		ArrayList<String> lore2 = new ArrayList<String>();
		meta2.setDisplayName("§bMaster Farmer §fTitle");
		lore2.add("§aPurchase for 100 1 Star, 2 Star");
		lore2.add("§aand 3 Star points.");
		meta2.setLore(lore2);
		title.setItemMeta(meta2);
		this.Title = title;
		if (!data.BoughtTitle) {
			inv.setItem(32, title);
		} else {
			lore2.clear();
			lore2.add("§aClick to equip");
			meta2.setLore(lore2);
			title.setItemMeta(meta2);
			inv.setItem(32, title);
		}

		inv.setItem(38, CreateBag(1));
		inv.setItem(40, CreateBag(2));
		inv.setItem(42, CreateBag(3));
		inventories.put(player.getUniqueId(), inv);
		return inv;
	}

	public static ItemStack hoe;
	public static ItemStack Title;

	public ItemStack CreateUpgrade(PlayerData data) {
		ItemStack Upgrade = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = Upgrade.getItemMeta();
		meta.setDisplayName("§6Bonus Chance Upgrade");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§dBonus chance: " + data.increasedChance);
		if (data.increasedChance >= 10) {
			lore.add("§aMaximum bonus achieved.");
		} else {
			if (data.increasedChance < 6) {
				lore.add("§aClick to increase chance by 1%");
				lore.add("§aFor 20 1 Star points");
			} else {
				if (data.increasedChance < 11) {
					lore.add("§aClick to increase chance by 1%");
					lore.add("§aFor 20 2 Star points");
				}
			}
		}
		meta.setLore(lore);
		Upgrade.setItemMeta(meta);

		return Upgrade;
	}

	public ItemStack CreateHead(HumanEntity ent, PlayerData data) {
		ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
		Player player = (Player) ent;
		SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
		playerheadmeta.setOwningPlayer(player);
		playerheadmeta.setDisplayName("§b" + player.getName());
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§e☆☆☆ §fpoints: " + data.Tier3);
		lore.add("§e☆☆ §fpoints: " + data.Tier2);
		lore.add("§e☆ §fpoints: " + data.Tier1);
		lore.add("§dBonus chance: " + data.increasedChance);
		playerheadmeta.setLore(lore);
		playerhead.setItemMeta(playerheadmeta);

		return playerhead;
	}

	public ItemStack CreateCrop(Material mat, PlayerData data) {
		ItemStack item = new ItemStack(mat);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§e☆☆☆ §fDeposited: " + data.GetTierCount(mat, 3));
		lore.add("§e☆☆ §fDeposited: " + data.GetTierCount(mat, 2));
		lore.add("§e☆ §fDeposited: " + data.GetTierCount(mat, 1));
		if (data.GetTierCount(mat, 3) >= 500 && data.Tier3 >= 50) {
			lore.add("§bClick to claim trophy.");
			lore.add("§bfor 50 3 Star points");
		} else {
			if (data.GetTierCount(mat, 2) >= 500 && data.Tier2 >= 50) {
				lore.add("§bClick to claim trophy.");
				lore.add("§bfor 50 2 Star points");
			} else {
				if (data.GetTierCount(mat, 1) >= 500 && data.Tier1 >= 50) {
					lore.add("§bClick to claim trophy.");
					lore.add("§bfor 50 1 Star points");
				}
			}
		}

		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public void openInventory(final HumanEntity ent, PlayerData data) {

		initializeItems(ent, data);
		ent.openInventory(inventories.get(ent.getUniqueId()));

	}

	public ItemStack CreateTrophy(Material mat, int tier, Player player) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		switch (tier) {
		case 1:
			meta.setDisplayName("§e☆ " + crunchFarmCore.Core.getNameForCrop(mat) + " Trophy §f" + player.getName());
			lore.add("§aTrophy achieved for depositing");
			lore.add("§a500 ☆ Star of this item!");
			break;
		case 2:
			meta.setDisplayName("§d☆☆ " + crunchFarmCore.Core.getNameForCrop(mat) + " Trophy §f" + player.getName());
			lore.add("§aTrophy achieved for depositing");
			lore.add("§a500 ☆☆ Star of this item!");
			break;
		case 3:
			meta.setDisplayName("§b☆☆☆ " + crunchFarmCore.Core.getNameForCrop(mat) + " Trophy §f" + player.getName());
			lore.add("§aTrophy achieved for depositing");
			lore.add("§a500 ☆☆☆ Star of this item!");
			break;
		}

		lore.add("§aAchieved by " + player.getName());
		Date date = java.util.Calendar.getInstance().getTime();
		lore.add("§a" + date.toString());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack goldRelic = new ItemStack(Material.SLIME_BALL);
	public static ItemStack silverRelic = new ItemStack(Material.SLIME_BALL);
	public static ItemStack bronzeRelic = new ItemStack(Material.SLIME_BALL);

	public void ooof() {
		goldRelic.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		ItemMeta goldMeta = goldRelic.getItemMeta();
		goldMeta.setDisplayName("§6Gold Relic");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§3Tier 3");
		goldMeta.setCustomModelData(3);
		goldMeta.setLore(lore);
		goldRelic.setItemMeta(goldMeta);
		goldRelic.setAmount(1);

		bronzeRelic.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		ItemMeta bronzeMeta = bronzeRelic.getItemMeta();
		bronzeMeta.setDisplayName("§6Bronze Relic");
		ArrayList<String> lore2 = new ArrayList<String>();
		lore2.add("§3Tier 1");
		bronzeMeta.setCustomModelData(1);
		bronzeMeta.setLore(lore2);
		bronzeRelic.setItemMeta(bronzeMeta);
		bronzeRelic.setAmount(1);

		silverRelic.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		ItemMeta silverMeta = silverRelic.getItemMeta();
		silverMeta.setDisplayName("§6Silver Relic");
		ArrayList<String> lore3 = new ArrayList<String>();
		lore3.add("§3Tier 2");
		silverMeta.setCustomModelData(2);
		silverMeta.setLore(lore3);
		silverRelic.setItemMeta(silverMeta);
		silverRelic.setAmount(1);
	}

	public void UpdateHeads(Inventory inv, PlayerData data, HumanEntity ent) {
		inv.setItem(22, CreateUpgrade(data));
		inv.setItem(4, CreateHead(ent, data));
		Player player = (Player) ent;
		ItemStack title = new ItemStack(Material.NAME_TAG);
		ItemMeta meta2 = title.getItemMeta();
		ArrayList<String> lore2 = new ArrayList<String>();
		meta2.setDisplayName("§bMaster Farmer §fTitle");
		lore2.add("§aPurchase for 100 1 Star, 2 Star");
		lore2.add("§aand 3 Star points.");
		meta2.setLore(lore2);
		title.setItemMeta(meta2);
		this.Title = title;
		if (!data.BoughtTitle) {
			inv.setItem(32, title);
		} else {
			lore2.clear();
			lore2.add("§aClick to equip");
			meta2.setLore(lore2);
			title.setItemMeta(meta2);
			inv.setItem(32, title);
		}
		StorageManager.save(player.getUniqueId(), data);
		crunchFarmCore.Core.PlayerData.put(player.getUniqueId(), data);
	}

	// Check for clicks on items
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) {
		if (e.getInventory() != inventories.get(e.getWhoClicked().getUniqueId()))
			return;
		e.setCancelled(true);
		final ItemStack clickedItem = e.getCurrentItem();
		ItemStack panel = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta panelMeta = panel.getItemMeta();
		panelMeta.setCustomModelData(55555);
		panel.setItemMeta(panelMeta);
		// verify current item is not null
		if (clickedItem == null || clickedItem.getType() == Material.AIR)
			return;
		if (clickedItem.isSimilar(panel)) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		ooof();

		PlayerData data = crunchFarmCore.Core.PlayerData.get(e.getWhoClicked().getUniqueId());
		if (clickedItem.isSimilar(CreateBag(1))) {
			 if (data.Tier1 >= 20) {
				 data.Tier1 -= 20;
				 UpdateHeads(e.getInventory(), data, e.getWhoClicked());
				 String command = "cr give to " + player.getName() + " RelicBag1";
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			 }
			return;
		}
		if (clickedItem.isSimilar(CreateBag(2))) {
			 if (data.Tier2 >= 20) {
				 data.Tier2 -= 20;
				 UpdateHeads(e.getInventory(), data, e.getWhoClicked());
				 String command = "cr give to " + player.getName() + " RelicBag2";
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			 }
			return;
		}
		if (clickedItem.isSimilar(CreateBag(3))) {
			 if (data.Tier3 >= 20) {
				 data.Tier3 -= 20;
				 UpdateHeads(e.getInventory(), data, e.getWhoClicked());
				 String command = "cr give to " + player.getName() + " RelicBag3";
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			 }
			return;
		}
		if (clickedItem.isSimilar(hoe)) {
			if (data.Tier1 >= 50 && data.Tier2 >= 50 && data.Tier3 >= 50) {
				if (player.getInventory().containsAtLeast(goldRelic, 30)) {
					ItemStack cost = goldRelic;
					cost.setAmount(30);
					data.Tier1 -= 50;
					data.Tier2 -= 50;
					data.Tier3 -= 50;
					player.getInventory().removeItem(cost);
					ItemStack newhoe = new ItemStack(Material.DIAMOND_HOE);
					ItemMeta meta = newhoe.getItemMeta();
					meta.setDisplayName("§2Tier 5 Hoe");
					ArrayList<String> lore = new ArrayList<String>();
					lore.add("");
					lore.add("§bTier 5");
					lore.add("");
					lore.add("§fBought by " + player.getName());
					meta.setLore(lore);
					data.BoughtHoe = true;
					newhoe.setItemMeta(meta);
					UpdateHeads(e.getInventory(), data, e.getWhoClicked());
					HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(newhoe);
					for (Integer key : remainingItems.keySet()) {
						// ...
						player.getWorld().dropItem(player.getLocation(), remainingItems.get(key));
						player.sendMessage("No free space in Inventory! Hoe was dropped on floor.");
					}
				}
				return;
			}
		}
		if (clickedItem.isSimilar(Title)) {
			if (!data.BoughtTitle) {
				if (data.Tier1 >= 100 && data.Tier2 >= 100 && data.Tier3 >= 100) {
					data.Tier1 -= 100;
					data.Tier2 -= 100;
					data.Tier3 -= 100;
					data.BoughtTitle = true;

					UpdateHeads(e.getInventory(), data, e.getWhoClicked());
					return;
				}
			} else {
				String command = "lp user " + player.getName() + " meta setprefix \"§bMaster Farmer §f\"";
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				player.sendMessage("Title updated!");
			}
		}
		if (clickedItem.isSimilar(CreateUpgrade(data))) {
			if (data.increasedChance >= 10) {
				return;
			} else {
				if (data.increasedChance >= 5) {
					if (data.Tier2 >= 20) {
						data.Tier2 -= 20;
						data.increasedChance++;
						StorageManager.save(player.getUniqueId(), data);
						crunchFarmCore.Core.PlayerData.put(player.getUniqueId(), data);
						UpdateHeads(e.getInventory(), data, e.getWhoClicked());
						return;
					}
				} else {
					if (data.increasedChance < 5) {
						if (data.Tier1 >= 20) {
							data.Tier1 -= 20;
							data.increasedChance++;
							StorageManager.save(player.getUniqueId(), data);
							crunchFarmCore.Core.PlayerData.put(player.getUniqueId(), data);
							UpdateHeads(e.getInventory(), data, e.getWhoClicked());
							return;
						}
					}
				}
			}
			return;
		}
		if (data.GetTierCount(clickedItem.getType(), 3) >= 500 && data.Tier3 >= 50) {
			data.Tier3 -= 50;
			HashMap<Integer, ItemStack> remainingItems = player.getInventory()
					.addItem(CreateTrophy(clickedItem.getType(), 3, player));
			for (Integer key : remainingItems.keySet()) {
				// ...
				player.getWorld().dropItem(player.getLocation(), remainingItems.get(key));
			}
			UpdateHeads(e.getInventory(), data, e.getWhoClicked());

		} else {
			if (data.GetTierCount(clickedItem.getType(), 2) >= 500 && data.Tier2 >= 50) {
				data.Tier2 -= 50;
				HashMap<Integer, ItemStack> remainingItems = player.getInventory()
						.addItem(CreateTrophy(clickedItem.getType(), 2, player));
				for (Integer key : remainingItems.keySet()) {
					// ...
					player.getWorld().dropItem(player.getLocation(), remainingItems.get(key));
				}
				UpdateHeads(e.getInventory(), data, e.getWhoClicked());
			} else {
				if (data.GetTierCount(clickedItem.getType(), 1) >= 500 && data.Tier1 >= 50) {
					data.Tier1 -= 50;
					HashMap<Integer, ItemStack> remainingItems = player.getInventory()
							.addItem(CreateTrophy(clickedItem.getType(), 1, player));
					for (Integer key : remainingItems.keySet()) {
						// ...
						player.getWorld().dropItem(player.getLocation(), remainingItems.get(key));
					}

					UpdateHeads(e.getInventory(), data, e.getWhoClicked());
				}
			}

		}

		return;
	}
	// Using slots click is a best option for your inventory click's
	// p.sendMessage("You clicked at slot " + e.getRawSlot());

	// Cancel dragging in our inventory
	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory() == inventories.get(e.getWhoClicked().getUniqueId())) {
			e.setCancelled(true);
		}
	}

}
