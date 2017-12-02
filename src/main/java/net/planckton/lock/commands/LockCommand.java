package net.planckton.lock.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.planckton.lock.Lock;
import net.planckton.lock.system.LockManager;
import net.planckton.lock.utils.Utils;

public class LockCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		
		if (!"lock".equalsIgnoreCase(label)) return false;
		
		Player player = (Player) sender;
		
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("help")) {
				Lock.getInstance().getHelpInventory().open(player);
				return true;
			}				
		}
		
		Block block = Utils.getBlockInSight(player);
		
		if (block == null) {
			player.sendMessage(ChatColor.RED + "You are too far away!");
			return false;
		}
		
		LockManager lockManager = Lock.getInstance().getLockManager();
		
		if (!lockManager.isLockableContainer(block.getType())) {
			player.sendMessage(ChatColor.RED + "This is not a lockable block!");
			return false;
		}
		
		if (lockManager.isLocked(block.getLocation())) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(lockManager.getOwner(block.getLocation()));
			if (target == null) {
				player.sendMessage(ChatColor.RED + "The " + ChatColor.DARK_RED.toString() + ChatColor.BOLD + 
						Utils.getDisplayName(block.getType()) + ChatColor.RED + " is already locked!");
				return false;
			}			
			
			if (target.getName().equalsIgnoreCase(player.getName())) {
				player.sendMessage(ChatColor.RED + "The " + ChatColor.DARK_RED.toString() + ChatColor.BOLD + 
						Utils.getDisplayName(block.getType()) + ChatColor.RED + " is already locked by you!");
				return false;
			}
			
			player.sendMessage(ChatColor.RED + "The " + ChatColor.DARK_RED.toString() + ChatColor.BOLD + 
					Utils.getDisplayName(block.getType()) + ChatColor.RED + " is already locked by " + target.getName() + "!");
			return false;
		}		
		
		Block multiBlock = Utils.getMultiBlock(block);
		
		if (multiBlock != null) {
			lockManager.addContainer(player, multiBlock.getLocation());
		}
		
		lockManager.addContainer(player, block.getLocation());
		
		player.sendMessage(ChatColor.GREEN + "You locked a " + ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + 
				Utils.getDisplayName(block.getType()) + ChatColor.GREEN + "!");
		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
		return false;
	}

}
