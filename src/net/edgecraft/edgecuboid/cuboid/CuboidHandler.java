package net.edgecraft.edgecuboid.cuboid;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.db.DatabaseHandler;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;
import net.edgecraft.edgecuboid.cuboid.types.HabitatType;

import org.bukkit.Location;

public class CuboidHandler {
	
	public static String cuboidTable = "edgecuboid_cuboids";
	public static String habitatTable = "edgecuboid_habitats";
	/* 
	 * List of all public areas 
	 * That's why cuboids have to be named like one of these.. for calculation :)
	 * 
	 */ 
	public static final String[] publicAreas = { "public", "bank", "jail", "hospital", "police", "firedepartment", "atm", "shop", "sight", "rail", "street" };
	
	private HashMap<Integer, Cuboid> cuboids = new LinkedHashMap<>();
	private HashMap<Integer, Habitat> habitats = new LinkedHashMap<>();
	
	private static final CuboidHandler instance = new CuboidHandler();
	private final DatabaseHandler db = EdgeCoreAPI.databaseAPI();
	
	protected CuboidHandler() { /* ... */ }
	
	/**
	 * Returns a static instance of this class
	 * @return CuboidHandler
	 */
	public static CuboidHandler getInstance() {
		return instance;
	}
	
	/**
	 * Returns a HashMap containing all stored cuboids
	 * @return HashMap<Integer, Cuboid>
	 */
	public HashMap<Integer, Cuboid> getCuboids() {
		return cuboids;
	}
	
	/**
	 * Returns a HashMap containing all stored habitats
	 * @return HashMap<Integer, Habitat>
	 */
	public HashMap<Integer, Habitat> getHabitats() {
		return habitats;
	}
	
	/**
	 * Returns the amount of existing (non-habitat) cuboids
	 * @return Integer
	 */
	public int amountOfCuboids() {
		return cuboids.size() - habitats.size();
	}
	
	/**
	 * Returns the amount of existing habitats
	 * @return Integer
	 */
	public int amountOfHabitats() {
		return habitats.size();
	}
	
	/**
	 * Returns the amount of currently inhabited habitats
	 * @return Integer
	 */
	public int percentageOfInhabitedHabitats() {
		int amount = 0;
		
		for (Habitat h : habitats.values()) {
			if (h != null) {
				if (h.isInhabited()) amount++;
			}
		}
		
		return amount;
	}
	
	/**
	 * Returns the percentage of green area in the world
	 * @return Integer
	 */
	public int percentageOfGreenArea() {
		int amount = 0;
		
		for (Cuboid c : cuboids.values()) {
			if (c != null)
				if (c.getName().contains("park")) amount++;
		}
		
		return (int) ((double) amount / amountOfCuboids() * 100);
	}
	
	/**
	 * Returns the percentage of dirty area in the world
	 * @return Integer
	 */
	public int percentageOfDirtyArea() {
		int amount = 0;
		
		for (Cuboid c : cuboids.values()) {
			if (c != null)
				if (c.getName().contains("street") || c.getName().contains("rail")) amount++;
		}
		
		return (int) ((double) amount / amountOfCuboids() * 100);
	}
	
	/**
	 * Returns the percentage of public area in the world
	 * @return
	 */
	public int percentageOfPublicAreas() {
		int amount = percentageOfGreenArea();
		
		for (Cuboid c : cuboids.values()) {				
			for (int i = 0; i < publicAreas.length; i++) {				
				if (c != null) {
					
					if (c.getName().contains(publicAreas[i])) amount++;
					
				}				
			}
		}
		
		return (int) ((double) amount / amountOfCuboids() * 100);
	}
	
	/**
	 * Registers a new cuboid
	 * @param name
	 * @param owner
	 * @param type
	 * @param min
	 * @param max
	 * @param enterMsg
	 * @param leaveMsg
	 * @param events
	 * @param flags
	 * @throws Exception 
	 */
	public void registerCuboid(String name, int owner, CuboidType type, Location min, Location max, String enterMsg, String leaveMsg, 
			List<String> participants, List<CuboidEvent> events, HashMap<Flag, List<String>> flags, List<String> commands) throws Exception {
		
		if (name == null || owner < 0 || type == null || min == null || max == null 
				|| enterMsg == null || leaveMsg == null || events == null || flags == null || participants == null || commands == null) return;
		
		if (existsCuboid(name)) return;
		
		Cuboid c = new Cuboid(name, generateID(), owner, type, min, max, min.getWorld(), enterMsg, leaveMsg, participants, events, flags, commands);
		registerCuboid(c);
	}
	
	private void registerCuboid(Cuboid cuboid) {
		try {
			
			byte[] cuboidByteArray = cuboid.toByteArray();
			
			db.executeUpdate("INSERT INTO " + CuboidHandler.cuboidTable + " (id, cuboid) VALUES ('" + cuboid.getID() + "', '" + cuboidByteArray + "');");
			
			synchronizeCuboid(cuboid.getID());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes an existing cuboid
	 * @param id
	 */
	public void deleteCuboid(int id) {
		if (id <= 0) return;
		if (!existsCuboid(id)) return;
		
		try {
			
			db.executeUpdate("DELETE FROM " + CuboidHandler.cuboidTable + " WHERE id = '" + id + "';");
			getCuboids().remove(id);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Registers a new habitat
	 * @param c
	 * @param type
	 * @param owner
	 * @param tenant
	 * @param worth
	 * @param buyable
	 * @param rental
	 * @param rentable
	 */
	public void registerHabitat(Cuboid c, HabitatType type, String owner, String tenant, double worth, boolean buyable, double rental, boolean rentable, Map<Upgrade, Integer> upgrades) {
		if (c == null || type == null || owner == null || tenant == null || worth < 0 || rental < 0) return;
		if (existsHabitat(c.getID()) || existsHabitat(c.getName()) || getHabitatByOwner(owner) != null || getHabitatByTenant(tenant) != null) return;
		if (existsCuboid(c.getID()) || existsCuboid(c) || existsCuboid(c.getName())) return;
		
		try {
			
			registerCuboid(c);
			
			Habitat h = new Habitat(c, type, owner, tenant, worth, buyable, rental, rentable, upgrades);
			byte[] habitatByteArray = h.toByteArray();
			
			db.executeUpdate("INSERT INTO " + CuboidHandler.habitatTable + " (cuboidid, habitat) VALUES ('" + h.getCuboidID() + "', '" + habitatByteArray + "');");
			
			synchronizeHabitat(h.getCuboidID());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes an existing habitat
	 * @param id
	 */
	public void deleteHabitat(int id) {
		if (id <= 0) return;
		if (!existsHabitat(id)) return;
		
		try {
		 	
			db.executeUpdate("DELETE FROM " + CuboidHandler.habitatTable + " WHERE id = '" + id + "';");
			getHabitats().remove(id);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates a new ID
	 * @return Integer
	 * @throws Exception
	 */
	public int generateID() throws Exception {
		if (amountOfCuboids() <= 0) return 1;
		
		return greatestID() + 1;
	}
	
	/**
	 * Returns the greatest ID of the cuboid database table
	 * @return Integer
	 * @throws Exception
	 */
	public int greatestID() throws Exception {
		List<Map<String, Object>> tempVar = db.getResults("SELECT MAX(id) FROM " + CuboidHandler.cuboidTable);
		if (tempVar.isEmpty()) return 1;
		
		return (int) tempVar.get(0).get("id");
	}
	
	/**
	 * Checks if the given cuboid exists
	 * @param c
	 * @return true/false
	 */
	public boolean existsCuboid(Cuboid c) {
		return getCuboids().containsValue(c);
	}
	
	/**
	 * Checks if the given cuboid exists
	 * @param id
	 * @return true/false
	 */
	public boolean existsCuboid(int id) {
		return getCuboids().containsKey(id);
	}
	
	/**
	 * Checks if the given cuboid exists
	 * @param name
	 * @return true/false
	 */
	public boolean existsCuboid(String name) {
		for (Cuboid c : getCuboids().values()) {
			if (c != null) 
				return c.getName().equalsIgnoreCase(name);
		}
		
		return false;
	}
	
	/**
	 * Checks if the given habitat exists
	 * @param h
	 * @return true/false
	 */
	public boolean existsHabitat(Habitat h) {
		return getCuboids().containsValue(h);
	}
	
	/**
	 * Checks if the given habitat exists
	 * @param id
	 * @return true/false
	 */
	public boolean existsHabitat(int id) {
		return getCuboids().containsKey(id);
	}
	
	/**
	 * Checks if the given habitat exists
	 * @param name
	 * @return true/false
	 */
	public boolean existsHabitat(String name) {
		return existsCuboid(name);
	}
	
	/**
	 * Returns the given cuboid
	 * @param id
	 * @return Cuboid
	 */
	public Cuboid getCuboid(int id) {
		return getCuboids().get(id);
	}
	
	/**
	 * Returns the given cuboid
	 * @param name
	 * @return Cuboid
	 */
	public Cuboid getCuboid(String name) {
		for (Cuboid c : getCuboids().values()) {
			if (c != null)
				if (c.getName().equalsIgnoreCase(name)) return c;
		}
		
		return null;
	}
	
	/**
	 * Returns the given habitat
	 * @param id
	 * @return Habitat
	 */
	public Habitat getHabitat(int id) {
		return getHabitats().get(id);
	}
	
	/**
	 * Returns the given habitat
	 * @param name
	 * @return Habitat
	 */
	public Habitat getHabitat(String name) {
		for (Habitat h : getHabitats().values()) {
			if (h != null)
				if (h.getCuboid().getName().equalsIgnoreCase(name)) return h;
		}
		
		return null;
	}
	
	/**
	 * Returns the habitat connected with the given owner
	 * @param owner
	 * @return Habitat
	 */
	public Habitat getHabitatByOwner(String owner) {
		for (Habitat h : getHabitats().values()) {
			if (h != null)
				if (h.getOwner().equalsIgnoreCase(owner)) return h;
		}
		
		return null;
	}
	
	/**
	 * Returns the habitat connected with the given tenant
	 * @param tenant
	 * @return Habitat
	 */
	public Habitat getHabitatByTenant(String tenant) {
		for (Habitat h : getHabitats().values()) {
			if (h != null)
				if (h.getTenant().equalsIgnoreCase(tenant)) return h;
		}
		
		return null;
	}
	
	/**
	 * Synchronizes the complete cuboid management
	 * Synchronized data can be turned on/off
	 * @param cuboids
	 * @param habitats
	 */
	public void synchronizeCuboidManagement(boolean cuboids, boolean habitats) {
		try {
			
			if (cuboids)
				for (int i = 1; i <= greatestID(); i++) {
					synchronizeCuboid(i);
				}
			
			if (habitats)
				for (int i = 1; i <= greatestID(); i++) {
					synchronizeHabitat(i);
				}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Synchronizes the given cuboid
	 * @param id
	 */
	public synchronized void synchronizeCuboid(int id) {
		try {
			
			List<Map<String, Object>> results = db.getResults("SELECT * FROM " + CuboidHandler.cuboidTable + " WHERE id = '" + id + "';");
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(results.get(0));
			
			Cuboid cuboid = Cuboid.toCuboid(byteArrayOutputStream.toByteArray());
			
			if (getCuboid(cuboid.getID()) != null) {
				Cuboid c = getCuboid(cuboid.getID());			
				
				if (!c.equals(cuboid)) {
					db.executeUpdate("UPDATE " + CuboidHandler.cuboidTable + " SET id = '" + c.getID() + "', cuboid = '" + c.toByteArray() + "';");
					getCuboids().put(c.getID(), c);
				}
				
				return;
			}
			
			getCuboids().put(cuboid.getID(), cuboid);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Synchronizes the given habitat
	 * @param id
	 */
	public synchronized void synchronizeHabitat(int id) {
		try {
			
			List<Map<String, Object>> results = db.getResults("SELECT * FROM " + CuboidHandler.habitatTable + " WHERE id = '" + id + "';");
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(results.get(0));
			
			Habitat habitat = Habitat.toHabitat(byteArrayOutputStream.toByteArray());
			
			if (getHabitat(habitat.getCuboidID()) != null) {
				Habitat h = habitat;
				
				db.executeUpdate("UPDATE " + CuboidHandler.habitatTable + " SET cuboidid = '" + h.getCuboidID() + "', habitat = '" + h.toByteArray() + "';");
				getHabitats().put(h.getCuboidID(), h);
				
				return;
			}
			
			getHabitats().put(habitat.getCuboidID(), habitat);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
