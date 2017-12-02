package net.planckton.lock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import net.planckton.lock.settings.Settings;
import net.planckton.lock.utils.Utils;

public class Database {
	
	private static Connection connection;	
	private static boolean connected;
	
	/**
	 * Connects the plugin to the database with specific data in the settings.yml file.
	 * 
	 * You should use a Connection Pool here. ;)
	 * 
	 * If it is connected it executes a query to create the table
	 * 
	 * @param settings
	 */
	public Database(Settings settings) {		
		try {
			connection = DriverManager.getConnection(settings.getDatabaseURL(), settings.getUserName(), settings.getPassword());
			connected = true;
		} catch (SQLException e) {
			e.printStackTrace();
			
			connected = false;
			return;
		}		
		
		createTable();
	}
	
	/**
	 * Creates a table named 'locked_containers'. The table contains of 2 columns.
	 * 
	 * location:   
	 *   I used VARCHAR(50) as the datatype because it has a variable length
	 * uuid: 
	 *   I used CHAR(32) as the datatype because a VARCHAR(36) costs more data and two longs is not easy to read
	 */
	public static void createTable() {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS locked_containers("
					+ "location varchar(50) PRIMARY KEY,"
					+ "uuid char(32) NOT NULL"
					+ ");");
			
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null && !statement.isClosed()) statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static final String INSERTQUERY = "INSERT INTO locked_containers(location,uuid) VALUES (?,?) ON DUPLICATE KEY UPDATE uuid=?;";
	private static final String DELETEQUERY = "DELETE FROM locked_containers WHERE location=?;";
	
	/** 
	 * Saves all the containers that is locked or was locked.
	 * 
	 * The hashmap contains of locations with uuids and locations with null
	 * 
	 * 
	 * If the uuid is null that means it needs to be deleted else insert location and data 
	 * in the database where on duplicate update the uuid (Owner changed)
	 * 
	 * It does not need to be saved async because it saves only on disable! 
	 *  
	 * @param map
	 */
	public static void saveLockedContainers(HashMap<String, UUID> map) {
		PreparedStatement statement = null;		
		
		try {
			for (Entry<String, UUID> entry : map.entrySet()) {
				if (entry.getValue() == null) {
					statement = connection.prepareStatement(DELETEQUERY);
					statement.setString(1, entry.getKey());
				} else {
					statement = connection.prepareStatement(INSERTQUERY);
					statement.setString(1, entry.getKey());
					statement.setString(2, entry.getValue().toString().replace("-", ""));
					statement.setString(3, entry.getValue().toString().replace("-", ""));
				}				
				statement.execute();				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null && !statement.isClosed()) statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Loads the locked containers in the database
	 * 
	 * UUID gets converted to the 36 characters length.
	 * 
	 * @param map
	 */
	public static void loadLockedContainers(HashMap<String, UUID> map) {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement("SELECT location, uuid FROM locked_containers");
			
			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				map.put(rs.getString("location"), Utils.convertUUID(rs.getString("uuid")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null && !statement.isClosed()) statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 */
	public static Connection getConnection() {
		return connection;
	}

	/**
	 * @return
	 */
	public static boolean isConnected() {
		return connected;
	}
}
