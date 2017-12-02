package net.planckton.lock.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryListener implements Listener {
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		if (!event.getInventory().getTitle().equalsIgnoreCase("Help Menu")) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (!event.getInventory().getTitle().equalsIgnoreCase("Help Menu")) return;
		
		event.setCancelled(true);
	}

}
