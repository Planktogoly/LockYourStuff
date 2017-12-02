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
import org.bukkit.permissions.Permission;

import net.planckton.lock.Lock;
import net.planckton.lock.system.LockManager;
import net.planckton.lock.utils.Utils;

public class UnlockCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		
		if (!"unlock".equalsIgnoreCase(label)) return false;
		
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
			player.sendMessage(ChatColor.RED + "This is not an unlockable block!");
			return false;
		}
		
		if (!lockManager.isLocked(block.getLocation())) {
			player.sendMessage(ChatColor.RED + "You can not unlock an unlocked " + 
		                       ChatColor.DARK_RED.toString() + ChatColor.BOLD + Utils.getDisplayName(block.getType()) + ChatColor.RED + "!");
			return false;
		}
		
		boolean hasPermission = player.hasPermission(new Permission("lock.use.locked"));
		
		if (!player.getUniqueId().equals(lockManager.getOwner(block.getLocation())) && !hasPermission) {
			player.sendMessage(ChatColor.RED + "This is not your " + 
				              ChatColor.DARK_RED.toString() + ChatColor.BOLD + Utils.getDisplayName(block.getType()) 
				              + ChatColor.RED + "! Do not touch it.");
			return false;
		}
		
		Block multiBlock = Utils.getMultiBlock(block);
		
		if (multiBlock != null) {
			lockManager.removeContainer(multiBlock.getLocation());
		}
		
		OfflinePlayer target = Bukkit.getOfflinePlayer(lockManager.getOwner(block.getLocation()));		
		
		lockManager.removeContainer(block.getLocation());
		
		if (target != null && !target.getName().equalsIgnoreCase(player.getName())) {
			player.sendMessage(ChatColor.GREEN + "You unlocked the " + ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + 
					Utils.getDisplayName(block.getType()) + ChatColor.GREEN + " of " + target.getName() + "!");
			player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
			return true;
		}
		
		player.sendMessage(ChatColor.GREEN + "You unlocked your " + ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + 
				Utils.getDisplayName(block.getType()) + ChatColor.GREEN + "!");
		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);	
		return true;
	}

}
