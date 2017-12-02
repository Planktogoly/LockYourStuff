package net.planckton.lock.utils;

import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class Utils {
	
	public static Block getBlockInSight(Player player) {
		BlockIterator iterator = new BlockIterator(player, 5);
		
		Block block = null;
		while (iterator.hasNext()) {
			Block blockIterator = iterator.next();
			
			if (blockIterator.getType() == Material.AIR) continue;
			
			block = blockIterator;
			break;
		}
		
		return block;
	}
	
	public static Block getMultiBlock(Block block) {
		Block multiBlock = null;
		
		if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
			multiBlock = getDoubleChest(block.getLocation(), block.getType());

		} else if (block.getType() == Material.ACACIA_DOOR || block.getType() == Material.BIRCH_DOOR 
				|| block.getType() == Material.DARK_OAK_DOOR || block.getType() == Material.JUNGLE_DOOR 
				|| block.getType() == Material.SPRUCE_DOOR || block.getType() == Material.WOODEN_DOOR) {
			multiBlock = getSecondDoorBlock(block.getLocation(), block.getType());
		}
		
		return multiBlock;
	}
	
	private static Block getSecondDoorBlock(Location location, Material checkBlock) {
		Block block = location.getWorld().getBlockAt(location.getBlockX() , location.getBlockY() + 1, location.getBlockZ());
		
		if (block.getType() == checkBlock) return block;
		
		block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
		
		if (block.getType() == checkBlock) return block;
		
		return null;
	}
	
	//TODO Code this method better waaay better :)
	private static Block getDoubleChest(Location location, Material checkBlock) {
		Block block = location.getWorld().getBlockAt(location.getBlockX() + 1, location.getBlockY(), location.getBlockZ());
		
		if (block.getType() == checkBlock) return block;
		
		block = location.getWorld().getBlockAt(location.getBlockX() - 1, location.getBlockY(), location.getBlockZ());
		
		if (block.getType() == checkBlock) return block;
		
		block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ() + 1);
		
		if (block.getType() == checkBlock) return block;
		
		block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ() - 1);
		
		if (block.getType() == checkBlock) return block;
		
		return null;
	}
	
	public static String getDisplayName(Material material) {		
		return WordUtils.capitalize(material.toString().replace("_", " ").toLowerCase());
	}
	
	public static UUID convertUUID(String uuid) {
	    StringBuilder builder = new StringBuilder(36);
	    builder.append(uuid, 0, 8);
	    builder.append('-');
	    builder.append(uuid, 8, 12);
	    builder.append('-');
	    builder.append(uuid, 12, 16);
	    builder.append('-');
	    builder.append(uuid, 16, 20);
	    builder.append('-');
	    builder.append(uuid, 20, 32);	    
	    
	    return UUID.fromString(builder.toString());
	}
	
	public static void sendActionBar(Player player, String message) {
        IChatBaseComponent bar = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message.replaceAll("&", "§") + "\"}");
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        PacketPlayOutChat packet = new PacketPlayOutChat(bar, (byte)2);
        entityPlayer.playerConnection.sendPacket(packet);
	}

}
