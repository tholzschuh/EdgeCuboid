package net.edgecraft.edgecuboid.shop;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.db.DatabaseHandler;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.other.EdgeItemStack;
import net.edgecraft.edgecuboid.shop.Shop.ShopType;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopHandler {
	
	public final static String shopTable = "edgecuboid_shops";
	private static final DatabaseHandler db = EdgeCoreAPI.databaseAPI();
	
	private final Map<Integer, Shop> shops = new LinkedHashMap<>();
	private static List<Material> frames;
	private static boolean looseInvOnDeath;
	
	private static final ShopHandler instance = new ShopHandler();
	
	protected ShopHandler() { /* Singleton */ }	
	
	public static ShopHandler getInstance() {
		return instance;
	}
	
	public Map<Integer, Shop> getShops() {
		return shops;
	}
	
	public int amountOfShops() {
		return getShops().size();
	}
	
	public List<Shop> getNearShops(Player p, double distance) {
		
		final List<Shop> nearShops = new ArrayList<>();
		
		for (Shop shop : getShops().values()) {
			
			if (shop.getCuboid().getCenter().distance(p.getLocation()) <= distance)
				nearShops.add(shop);
			
		}
		
		return nearShops;
	}
	
	public List<Shop> getProvidingShops(Player p, EdgeItemStack item, double distance) {

		final List<Shop> providingShops = new ArrayList<>();
		
		for (Shop shop : getShops().values()) {
			if (shop.getGuiItems().containsKey(item)) {
				
				if (shop.getCuboid().getCenter().distance(p.getLocation()) <= distance)
					providingShops.add(shop);
				
			}
		}
		
		return providingShops;
	}
	
	public List<Material> getShopFrames() {
		return frames;
	}
	
	public static void setShopFrames(List<String> frames) {
		final List<Material> material = new ArrayList<>();
		
		for (String s : frames) {
			if (s != null)
				material.add(Material.valueOf(s));
		}
		
		ShopHandler.frames = material;
	}
	
	public boolean getInventoryLoosing() {
		return looseInvOnDeath;
	}
	
	public static void toggleInventoryLoosing(boolean var) {
		ShopHandler.looseInvOnDeath = var;
	}
	
	public void registerShop(Cuboid c, ShopType type, String owner, double price, boolean buyable, double rental, boolean rentable, Map<EdgeItemStack, Double> guiItems, double income, boolean distribution) {		
		try {
			
			final Shop shop = new Shop(c, type, owner, price, buyable, rental, rentable, guiItems, income, distribution);
			final Blob blob = new SerialBlob( shop.toByteArray() );
			
			final PreparedStatement registerShop = db.prepareUpdate("INSERT INTO " + ShopHandler.shopTable + " (cuboidid, shop) VALUES (?, ?);");
			registerShop.setInt(1, c.getID());
			registerShop.setBlob(2, blob);
			registerShop.executeUpdate();
			
			synchronizeShop(shop.getCuboidID());
			
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void deleteShop(int id) {
		if (id <= 0) return;
		if (!existsShop(id)) return;
		
		try {
			
			final PreparedStatement deleteShop = db.prepareUpdate("DELETE FROM " + ShopHandler.shopTable + " WHERE cuboidid = '" + id + "';");
			deleteShop.executeUpdate();
			
			getShops().remove(id);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int generateID() throws Exception {
		if (amountOfShops() <= 0) return 1;
		
		return greatestID() + 1;
	}
	
	public int greatestID() throws Exception {
		final List<Map<String, Object>> tempVar = db.getResults("SELECT COUNT(cuboidid) AS amount FROM " + ShopHandler.shopTable);
		int tempID = Integer.parseInt(String.valueOf(tempVar.get(0).get("amount")));
		
		if (tempID <= 0) return 1;
		
		return tempID;
	}
	
	public boolean existsShop(Cuboid c) {
		for (Shop shop : getShops().values()) {
			if (shop.getCuboid().equals(c))
				return true;
		}
		
		return false;
	}
	
	public boolean existsShop(int id) {
		return getShops().containsKey(id);
	}
	
	public boolean existsShop(Shop shop) {
		return getShops().containsValue(shop);
	}
	
	public boolean existsShop(String cuboid) {
		for (Shop shop : getShops().values()) {
			if (shop.getCuboid().getName().equals(cuboid))
				return true;
		}
		
		return false;
	}
	
	public Shop getShop(String owner) {
		for (Shop shop : getShops().values()) {
			if (shop.isOwner(owner))
				return shop;
		}
		
		return null;
	}
	
	public Shop getShop(Cuboid c) {
		for (Shop shop : getShops().values()) {
			if (shop.getCuboid().equals(c))
				return shop;
		}
		
		return null;
	}
	
	public Shop getShop(int id) {
		return getShops().get(id);
	}
	
	public void synchronizeShops() {
		try {
			
			for (int i = 1; i <= greatestID(); i++) {
				synchronizeShop(i);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void synchronizeShop(int id) {
		try {
			
			final List<Map<String, Object>> results = db.getResults("SELECT * FROM " + ShopHandler.shopTable + " WHERE cuboidid = '" + id + "';");
			
			if (results.isEmpty()) {
				EdgeCuboid.log.info(EdgeCuboid.cuboidbanner + "No Synchronizable Shop Entries Found! Cancelling synchronization..");
				return;
			}
			
			final byte[] byteToShop = (byte[]) results.get(0).get("shop");
			
			final Shop shop = Shop.toShop(byteToShop);
			
			if (getShop(shop.getCuboidID()) != null) {
				Shop s = shop;
					
				if (!s.equals(shop)) {
					
					final PreparedStatement updateShop = db.prepareUpdate("UPDATE " + ShopHandler.shopTable + " SET cuboidid = ?, shop = ?;");
					final Blob blob = new SerialBlob(s.toByteArray());
					
					updateShop.setInt(1, s.getCuboidID());
					updateShop.setBlob(2, blob);
					updateShop.executeUpdate();
					
					getShops().put(s.getCuboidID(), s);
				}
				
				return;
			}
			
			getShops().put(shop.getCuboidID(), shop);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
