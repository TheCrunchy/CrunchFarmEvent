package crunchFarmCore;

import java.awt.List;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {
	public Plugin plugin;
	public String path;

	public ArrayList<String> crops = new ArrayList<String>();
    public FarmGUI gui;
	@Override
	public void onEnable() {
		plugin = this;
       
		path = this.getDataFolder().getAbsolutePath();
	    StorageManager.setup(path, plugin);
	    gui = new FarmGUI(path, plugin);
		this.getCommand("farmsell").setExecutor(new SellCommand());
		getServer().getPluginManager().registerEvents(new BlockBreak(), this);
		getServer().getPluginManager().registerEvents(gui, this);
		crops.add("minecraft:wheat[age=7]");
		crops.add("minecraft:potatoes[age=7]");
		crops.add("minecraft:carrots[age=7]");
		crops.add("minecraft:melon");
		crops.add("minecraft:pumpkin");
		crops.add("minecraft:beetroots[age=3]");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				ArrayList<Location> removing = new ArrayList<Location>();
				for (Location key : usedLocations.keySet()) {
					if (System.currentTimeMillis() >= usedLocations.get(key)) {
						removing.add(key);
					}
					// ...

				}
				for (Location loc : removing) {
					usedLocations.remove(loc);
				}

			}
		}, 0L, 1200L);
	     File directoryPath = new File(path);
	      //List of all files and directories
	      String contents[] = directoryPath.list();
	      for(int i=0; i<contents.length; i++) {
	         String temp = contents[i].toString();
	         temp = temp.replaceAll(".json", "");
	         PlayerData data  = StorageManager.load(UUID.fromString(temp));
	         PlayerData.put(UUID.fromString(temp), data);
	      }
	}

	public static boolean hasLore(ItemStack item, String lore) {
		if (item == null) {
			return false;
		}
		if (item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().contains(lore)) {
			return true;
		} else {
			return false;
		}

	}

	public void AddToPlayerData(Player player, Material mat, Integer amount, Integer tier) {
		PlayerData data; 
		if (PlayerData.containsKey(player.getUniqueId())) {
			data = PlayerData.get(player.getUniqueId());
			data.AddToDepositCounts(mat, amount, tier);
			PlayerData.put(player.getUniqueId(), data);
		} else {
			data = new PlayerData();
			data.AddToDepositCounts(mat, amount, tier);
			PlayerData.put(player.getUniqueId(), data);
		}
		
	}

	public class SellCommand implements CommandExecutor {

		// This method is called, when somebody uses our command
		@Override
		@EventHandler
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			Player player = (Player) sender;
			ItemStack[] inv = player.getInventory().getStorageContents();
			int toPay = 0;
			for (ItemStack item : inv) {

				if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
					if (hasLore(item, "§e☆☆☆")) {
						AddToPlayerData(player, item.getType(), item.getAmount(), 3);
						toPay += 30 * item.getAmount();
						item.setAmount(0);

					}
					if (hasLore(item, "§e☆☆")) {
						AddToPlayerData(player, item.getType(), item.getAmount(), 2);
						toPay += 15 * item.getAmount();
						item.setAmount(0);
					}
					if (hasLore(item, "§e☆")) {
						toPay += 5 * item.getAmount();
						AddToPlayerData(player, item.getType(), item.getAmount(), 1);
						item.setAmount(0);
					}
				
				}
			}
			if (toPay > 0) {
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
						"eco give " + player.getName() + " " + (toPay));
				PlayerData data = PlayerData.get(player.getUniqueId());
				gui.openInventory(player, data);
				StorageManager.save(player.getUniqueId(), data);
			}
			else {
				player.sendMessage("You do not have any Star crops to sell me.");
			}
			
			return true;
		}
	}

	public static HashMap<UUID, PlayerData> PlayerData = new HashMap<UUID, PlayerData>();

	private HashMap<Location, Long> usedLocations = new HashMap<Location, Long>();
	private HashMap<UUID, Integer> playerCounts = new HashMap<UUID, Integer>();

	public boolean AddToPlayerCount(UUID id) {
		if (playerCounts.containsKey(id)) {
			playerCounts.put(id, playerCounts.get(id) + 1);
			if (playerCounts.get(id) >= 30) {
				playerCounts.put(id, 0);
				return true;
			}
		} else {
			playerCounts.put(id, 1);
		}
		return false;
	}

	public boolean getChance(int minimalChance) {
		Random random = new Random();
		return random.nextInt(99) + 1 <= minimalChance;
	}
	public static String getNameForCrop(Material mat) {
		switch (mat) {
		case WHEAT:
			return "Wheat";
		case CARROT:
			return "Carrot";
		case POTATO:
			return "Potato";
		case MELON:
			return "Melon";
		case PUMPKIN:
			return "Pumpkin";
		case BEETROOT:
			return "Beetroot";
			
		}
		return "no name";
	}
	public void DropCrop(Player player, String type) {
		PlayerData data;
		if (PlayerData.containsKey(player.getUniqueId())) {
			data = PlayerData.get(player.getUniqueId());
		}
		else {
			data = new PlayerData();
			PlayerData.put(player.getUniqueId(), data);
		}
		ItemStack DropItem = null;
		ItemMeta meta;
		ArrayList<String> lore = new ArrayList<String>();
		switch (type) {
		case "minecraft:wheat[age=7]":
			DropItem = new ItemStack(Material.WHEAT);
			break;
		case "minecraft:potatoes[age=7]":
			DropItem = new ItemStack(Material.POTATO);
			break;
		case "minecraft:carrots[age=7]":
			DropItem = new ItemStack(Material.CARROT);
			break;
		case "minecraft:melon":
			DropItem = new ItemStack(Material.MELON);
			break;
		case "minecraft:beetroots[age=3]":
			DropItem = new ItemStack(Material.BEETROOT);
			break;
		case "minecraft:pumpkin":
			DropItem = new ItemStack(Material.PUMPKIN);
			break;
		}
		// tier 3
		if (getChance(2 + data.increasedChance)) {
			meta = DropItem.getItemMeta();
			lore.add("§e☆☆☆");
			meta.setDisplayName("§e☆☆☆ §f" + getNameForCrop(DropItem.getType()));
			meta.setLore(lore);
			DropItem.setItemMeta(meta);
			player.sendMessage("§bYou harvested a §e☆☆☆ §bcrop!");
			HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(DropItem);
			for (Integer key : remainingItems.keySet()) {
				// ...
				player.getWorld().dropItem(player.getLocation(), remainingItems.get(key));
			}
			return;
		}
		// tier 2
		if (getChance(20 + data.increasedChance)) {
			meta = DropItem.getItemMeta();
			lore.add("§e☆☆");
			meta.setDisplayName("§e☆☆ §f" + getNameForCrop(DropItem.getType()));
			meta.setLore(lore);
			DropItem.setItemMeta(meta);
			player.sendMessage("§dYou harvested a §e☆☆ §dcrop!");
			HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(DropItem);
			for (Integer key : remainingItems.keySet()) {
				// ...
				player.getWorld().dropItem(player.getLocation(), remainingItems.get(key));
			}
			return;
		}
		meta = DropItem.getItemMeta();
		lore.add("§e☆");
		meta.setDisplayName("§e☆ §f" + getNameForCrop(DropItem.getType()));
		meta.setLore(lore);
		DropItem.setItemMeta(meta);
		player.sendMessage("§fYou harvested a §e☆ §fcrop!");
		HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(DropItem);
		for (Integer key : remainingItems.keySet()) {
			// ...
			player.getWorld().dropItem(player.getLocation(), remainingItems.get(key));
		}
		// tier 1, no chance check

	}

	public boolean isFish(ItemStack item) {
		if (item.getType() == Material.COD || item.getType() == Material.SALMON || item.getType() == Material.TROPICAL_FISH) {
			return true;
		}
		
		return false;
	}
	
	public class BlockBreak implements Listener {
	

		
		@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onBlockBreak(BlockBreakEvent event) {
			if (event.isCancelled()) {
				return;
			}

			// event.getPlayer().sendMessage(event.getBlock().getBlockData().getAsString());
			if (event.getPlayer() != null) {

				if (crops.contains(event.getBlock().getBlockData().getAsString())) {
					if (usedLocations.containsKey(event.getBlock().getLocation())) {
						if (System.currentTimeMillis() >= usedLocations.get(event.getBlock().getLocation())) {
							if (AddToPlayerCount(event.getPlayer().getUniqueId())) {
								DropCrop(event.getPlayer(), event.getBlock().getBlockData().getAsString());
								usedLocations.put(event.getBlock().getLocation(), System.currentTimeMillis() + 180000);
							}
						}
					} else {
						if (AddToPlayerCount(event.getPlayer().getUniqueId())) {
							DropCrop(event.getPlayer(), event.getBlock().getBlockData().getAsString());
							usedLocations.put(event.getBlock().getLocation(), System.currentTimeMillis() + 180000);
						}
					}

				}
			}

		}
	}
}
