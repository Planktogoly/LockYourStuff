package net.planckton.lock.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.planckton.lock.Database;
import net.planckton.lock.Lock;
import net.planckton.lock.settings.Settings;
import net.planckton.lock.utils.ConfigFile;
import net.planckton.lock.utils.Utils;

public class LockManager implements Listener {
	
	/**
	 *  A list with all the lockable blocks
	 */
	private ArrayList<Material> lockableBlocks = new ArrayList<>(); {
		lockableBlocks.add(Material.FURNACE);
		lockableBlocks.add(Material.CHEST);
		lockableBlocks.add(Material.TRAPPED_CHEST);
		lockableBlocks.add(Material.ACACIA_DOOR);
		lockableBlocks.add(Material.BIRCH_DOOR);
		lockableBlocks.add(Material.DARK_OAK_DOOR);
		lockableBlocks.add(Material.JUNGLE_DOOR);
		lockableBlocks.add(Material.SPRUCE_DOOR);
		lockableBlocks.add(Material.WOODEN_DOOR);
	}	
	
	private ConfigFile containersFile;
	
	private HashMap<String, UUID> lockedContainers;
	
	/**
	 * Checks whether he wants to use flat-file or database 
	 * 
	 * Loads all the data from the chosen database.
	 * 
	 * @param settings
	 */
	public LockManager(Settings settings) {
		this.lockedContainers = new HashMap<String, UUID>();
		
		if (settings.isUseMySQL() && Database.isConnected()) Database.loadLockedContainers(lockedContainers);
		else {
			this.containersFile = new ConfigFile(Lock.getInstance(), "data.yml");
			
			loadDataFromFile();
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerBreakBlock(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		if (!lockableBlocks.contains(block.getType())) return;
		
		UUID uuid = lockedContainers.get(block.getLocation().toVector().toString());
		if (uuid == null) return;
		
		if (event.getPlayer().getUniqueId().toString().equals(uuid.toString())) {
			removeContainer(block.getLocation());
			
			Block multiBlock = Utils.getMultiBlock(block);
			if (multiBlock != null) removeContainer(multiBlock.getLocation());
			return;
		}
		
		Player target = Bukkit.getPlayer(uuid);
		if (target == null) return;
		
		BaseComponent[] message = new ComponentBuilder(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Warning! ")
				.append(ChatColor.RED + event.getPlayer().getDisplayName() + " broke your ")
				.append(ChatColor.RED.toString() + ChatColor.BOLD + Utils.getDisplayName(block.getType()) + ChatColor.RED + ".")
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Location: " + 
						ChatColor.WHITE + block.getLocation().toVector().toString()).create()))
				.create();
		
		target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 1.0f);
		target.spigot().sendMessage(message);		
		
		removeContainer(block.getLocation());
		
		Block multiBlock = Utils.getMultiBlock(block);
		if (multiBlock != null) removeContainer(multiBlock.getLocation());
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		Block block = event.getClickedBlock();
		Material blockType = block.getType();
		
		if (!lockableBlocks.contains(blockType)) return;
		
		Player player = event.getPlayer();
		
		UUID uuid = lockedContainers.get(block.getLocation().toVector().toString());
		
		if (uuid == null) return;
		if (uuid.toString().equals(player.getUniqueId().toString())) return;		
		if (player.hasPermission("lock.use.locked")) return;
		
		OfflinePlayer target = Bukkit.getOfflinePlayer(getOwner(block.getLocation()));
		if (target == null) {
			Utils.sendActionBar(player, ChatColor.RED.toString() + ChatColor.BOLD + "You are not allowed to open this!");
		} else {
			Utils.sendActionBar(player, ChatColor.RED.toString() + ChatColor.BOLD + "You are not allowed to open this! It is owned by " + target.getName());
		}
		
		event.setCancelled(true);
		event.setUseInteractedBlock(Result.DENY);
		return;
	}
	
	/**
	 * @param location
	 * @return
	 */
	public UUID getOwner(Location location) {
		return lockedContainers.get(location.toVector().toString());
	}
	
	/**
	 * @param location
	 */
	public void removeContainer(Location location) {
		lockedContainers.put(location.toVector().toString(), null);
		
		if (containersFile != null) containersFile.removeContainer(location.toVector().toString());
	}
	
	/**
	 * @param location
	 * @return
	 */
	public boolean isLocked(Location location) {
		return lockedContainers.get(location.toVector().toString()) != null;
	}
	
	/**
	 * @param player
	 * @param location
	 */
	public void addContainer(Player player, Location location) {
		lockedContainers.put(location.toVector().toString(), player.getUniqueId());
		
		if (containersFile != null) containersFile.addContainer(player.getUniqueId().toString().replace("-", ""), location.toVector().toString());
	}
	
	/**
	 * Loads the data from the data.yml file
	 * 
	 * UUID gets converted to the 36 char length
	 * 
	 * and replace the ,'s to .'s and ;'s to ,'s for the location
	 * 
	 */
	private void loadDataFromFile() {
		containersFile.getBukkitFile().getKeys(false).forEach(path -> {
			String finalPath = path.replace(",", ".").replace(";", ",");		
			
			
			lockedContainers.put(finalPath, Utils.convertUUID(containersFile.getString(path)));
		});		
	}
	
	/**
	 * @param material
	 * @return
	 */
	public boolean isLockableContainer(Material material) {
		return lockableBlocks.contains(material);
	}
	
	/**
	 * Saves the locked blocks to the database 
	 * 
	 */
	public void saveLockedContainers() {
		Database.saveLockedContainers(lockedContainers);
	}
}
