package net.planckton.lock.settings;

import net.planckton.lock.Lock;
import net.planckton.lock.utils.ConfigFile;

public class Settings {
	
	private boolean useMySQL;
	
	private ConfigFile settingsFile;
	
	/**
	 * @param lock
	 */
	public Settings(Lock lock) {
		this.settingsFile = new ConfigFile(lock, "Settings.yml");
		
	    useMySQL = settingsFile.getBoolean("database.use");
	}
	
	/**
	 * @return String
	 */
	public String getDatabaseURL() {
		return "jdbc:mysql://" + settingsFile.getString("database.host") + ":" + settingsFile.getString("database.port") + "/" 
				+ settingsFile.getString("database.name") + "?autoReconnect=true";
	}
	
	/**
	 * @return String
	 */
	public String getUserName() {
		return settingsFile.getString("database.username");
	}
	
	/**
	 * @return String
	 */ 
	public String getPassword() {
		return settingsFile.getString("database.password");
	}

	/**
	 * @return boolean
	 */
	public boolean isUseMySQL() {
		return useMySQL;
	}
}
