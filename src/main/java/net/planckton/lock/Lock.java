package net.planckton.lock;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.planckton.lock.commands.LockCommand;
import net.planckton.lock.commands.UnlockCommand;
import net.planckton.lock.inventory.HelpInventory;
import net.planckton.lock.inventory.InventoryListener;
import net.planckton.lock.settings.Settings;
import net.planckton.lock.system.LockManager;

public class Lock extends JavaPlugin {
	
	private static Lock instance;
	
	private LockManager lockManager;
	private Settings settings;
	
	private HelpInventory helpInventory;
	
	@Override
	public void onEnable() {
		instance = this;
		
		this.settings = new Settings(this);
		
		if (settings.isUseMySQL()) new Database(settings);		
		
		this.lockManager = new LockManager(settings);
		
		this.helpInventory = new HelpInventory();
		
		initializePlugin();
	}
	
	@Override
	public void onDisable() {
		instance = null;
		
		this.lockManager.saveLockedContainers();
		
		this.helpInventory = null;
		this.lockManager = null;
		this.settings = null;
	}
	
	private void initializePlugin() {
		Bukkit.getPluginManager().registerEvents(lockManager, this);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		
		getCommand("lock").setExecutor(new LockCommand());
		getCommand("unlock").setExecutor(new UnlockCommand());
	}

	/**
	 * @return {@link Lock}
	 */
	public static Lock getInstance() {
		return instance;
	}

	/**
	 * @return {@link LockManager}
	 */
	public LockManager getLockManager() {
		return lockManager;
	}

	/**
	 * @return {@link Settings}
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @return {@link HelpInventory}
	 */
	public HelpInventory getHelpInventory() {
		return helpInventory;
	}

}
