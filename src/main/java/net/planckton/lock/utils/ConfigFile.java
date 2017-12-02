package net.planckton.lock.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.planckton.lock.Lock;

public class ConfigFile {
	
	private File file;
	private FileConfiguration bukkitFile;
	
	/**
	 * @param lock
	 * @param filename
	 */
	public ConfigFile(Lock lock, String filename) {
		this.file = new File(lock.getDataFolder(), filename);
		
		if (!file.exists()) file.getParentFile().mkdirs();
		
		if (lock.getResource(filename) == null) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		
		this.bukkitFile = YamlConfiguration.loadConfiguration(file);
		System.out.println("Loaded config file: " + filename);
	}
	
	/**
	 * @param path
	 * @return boolean
	 */
	public boolean getBoolean(String path) {
		return bukkitFile.getBoolean(path);
	}
	
	/**
	 * @param path
	 * @return String
	 */
	public String getString(String path) {
		return bukkitFile.getString(path);
	}
	
	/**
	 * @param uuid
	 * @param location
	 */
	public void addContainer(String uuid, String location) {
		bukkitFile.set(location.replace(",", ";").replace(".", ","), uuid);
		
		try {
			bukkitFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param location
	 */
	public void removeContainer(String location) {
		bukkitFile.set(location.replace(",", ";").replace(".", ","), null);
		
		try {
			bukkitFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return {@link FileConfiguration}
	 */
	public FileConfiguration getBukkitFile() {
		return bukkitFile;
	}
	
}
