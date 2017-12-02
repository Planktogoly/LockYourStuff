package net.planckton.lock.inventory;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HelpInventory {
	
	private Inventory inventory;
	
	public HelpInventory() {
		this.inventory = Bukkit.createInventory(null, 27, "Help Menu");
		
		inventory.setItem(11, generateItem(Material.BOOK, (byte) 0, 1, 
				ChatColor.YELLOW.toString() + ChatColor.BOLD + "/lock" + ChatColor.GREEN + " Command", 
				ChatColor.GRAY + "Look at a block you want to lock",
				ChatColor.GRAY + "and run the command /lock!",
				" ",
				ChatColor.GRAY + "This feature supports multiblocks!",
				" ",
				ChatColor.RED.toString() + ChatColor.BOLD + "Warning! " + ChatColor.RED + "They can still break the block."));
		
		inventory.setItem(13, generateItem(Material.PAPER, (byte) 0, 1, 
				ChatColor.AQUA.toString() + ChatColor.BOLD + "Block list", 
				ChatColor.GRAY + "You can protect your personal items",
				ChatColor.GRAY + "and your cozy house!",
				ChatColor.GRAY + "Check here the block list:",
				" ",
				ChatColor.WHITE + "- " + ChatColor.YELLOW + "All the different doors",
				ChatColor.WHITE + "- " + ChatColor.YELLOW + "Normal chests",
				ChatColor.WHITE + "- " + ChatColor.YELLOW + "Trapped chests",
				ChatColor.WHITE + "- " + ChatColor.YELLOW + "Furnaces",
				" ",
				ChatColor.GREEN + "Use " + ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "/lock" + ChatColor.GREEN + " to lock a block."));
		
		inventory.setItem(15, generateItem(Material.BOOK, (byte) 0, 1, 
				ChatColor.YELLOW.toString() + ChatColor.BOLD + "/unlock" + ChatColor.GREEN + " Command", 
				ChatColor.GRAY + "Look at a block you want to unlock",
				ChatColor.GRAY + "and run the command /unlock!",
				" ",
				ChatColor.GRAY + "This feature supports multiblocks!",
				" ",
				ChatColor.RED.toString() + ChatColor.BOLD + "Warning! " + ChatColor.RED + "Your items are now are vulnerable."));
	}
	
	/**
	 * @param player
	 */
	public void open(Player player) {
		player.openInventory(inventory);
	}
	
	/**
	 * @param material
	 * @param data
	 * @param amount
	 * @param name
	 * @param lore
	 * @return {@link ItemStack}
	 */
	private ItemStack generateItem(Material material, byte data, int amount, String name, String... lore) {
		ItemStack item = new ItemStack(material, amount, data);
		
		ItemMeta meta = item.getItemMeta();		
		meta.setDisplayName(name);
		
		meta.setLore(new ArrayList<>(Arrays.asList(lore)));
		
		item.setItemMeta(meta);
		
		return item;		
	}

}
