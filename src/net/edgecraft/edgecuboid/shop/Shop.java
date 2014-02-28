package net.edgecraft.edgecuboid.shop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.edgecraft.edgeconomy.EdgeConomyAPI;
import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.economy.EconomyPlayer;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.serializable.EdgeItemStack;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Shop implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final LanguageHandler lang = EdgeCoreAPI.languageAPI();
	
	public enum ShopType {
		
		STANDARD(false),
		USER(true);
		
		private boolean buyable;
		
		private ShopType(boolean buyable) {
			this.buyable = buyable;
		}
		
		public boolean isBuyable() {
			return buyable;
		}
	}
	
	private Cuboid cuboid;
	private int cuboidID;
	private int shopID;
	private ShopType type;
	private String owner;
	
	private double price;
	private boolean buyable;
	
	private double rental;
	private boolean rentable;
	
	private Inventory gui;
	private Map<EdgeItemStack, Double> guiItems = new HashMap<>();
	private boolean allowDistribution;
	
	protected Shop() { /* Singleton */ }
	
	public Shop(Cuboid cuboid, ShopType type, String owner, double price, boolean buyable, double rental, boolean rentable, Map<EdgeItemStack, Double> guiItems, boolean distribution) {
		
		setCuboid(cuboid);
		setType(type);
		setOwner(owner);
		setPrice(price);
		setBuyableStatus(buyable);
		setRental(rental);
		setRentableStatus(rentable);
		setupShopGui();
		setGuiItems(guiItems);
		setDistribution(distribution);
		
	}
	
	public static Shop toShop(byte[] byteArray) {
		try {
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> infoMap = (Map<String, Object>) objectInputStream.readObject();
			
			Shop shop = new Shop();
			shop.deserialize(infoMap);
			
			return shop;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] toByteArray() {
		try {
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(serialize());
			
			return byteArrayOutputStream.toByteArray();
			
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	
	private Map<String, Object> serialize() {
		Map<String, Object> infoMap = new LinkedHashMap<String, Object>();
		
		infoMap.put("object-type", "Shop");
		infoMap.put("object", this);
		
		return infoMap;
	}
	
	private void deserialize(Map<String, Object> infoMap) {
		if (!infoMap.containsKey("object-type") || !infoMap.get("object-type").equals("Shop")) throw new java.util.UnknownFormatFlagsException("No Shop!");
		if (infoMap.get("object") == null || this.equals(infoMap.get("object"))) return;
		
		Shop shop = (Shop) infoMap.get("object");
		
		setCuboid(shop.getCuboid());
		setType(shop.getType());
		setOwner(shop.getOwner());
		setPrice(shop.getPrice());
		setBuyableStatus(shop.isBuyable());
		setRental(shop.getRental());
		setRentableStatus(shop.isRentable());
		setGuiItems(shop.getGuiItems());
		setDistribution(shop.isDistributionAllowed());
		setupShopGui();
		
	}
	
	public Cuboid getCuboid() {
		return cuboid;
	}
	
	public int getCuboidID() {
		return cuboidID;
	}
	
	public int getShopID() {
		return shopID;
	}
	
	public ShopType getType() {
		return type;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public boolean isOwner(String owner) {
		return getOwner().equals(owner);
	}
	
	public void switchOwner(User user) {
		if (user != null)
			setOwner(user.getName());
	}
	
	public void switchOwner(String name) {
		setOwner(name);
	}
	
	public double getPrice() {
		calculatePrice();
		return price;
	}
	
	private void calculatePrice() {
		price = (double) getCuboid().getArea() * getGuiItems().size() / 100;
	}
	
	public boolean isBuyable() {
		return buyable;
	}
	
	public double getRental() {
		return rental;
	}
	
	public boolean isRentable() {
		return rentable;
	}
	
	public Inventory getGui() {
		return gui;
	}
	
	public Map<EdgeItemStack, Double> getGuiItems() {
		return guiItems;
	}
	
	public double getItemPrice(EdgeItemStack item) {
		if (item != null)
			return getGuiItems().get(item);
		else
			return 0.0D;
	}
	
	public boolean isDistributionAllowed() {
		return allowDistribution;
	}

	protected void setCuboid(Cuboid cuboid) {
		if (cuboid != null)
			this.cuboid = cuboid;
	}

	protected void setCuboidID(int cuboidID) {
		if (cuboidID == getCuboid().getID())
			this.cuboidID = cuboidID;
	}
	
	protected void setShopID(int shopID) {
		if (shopID > 0)
			this.shopID = shopID;
	}

	protected void setType(ShopType type) {
		if (type != null)
			this.type = type;
	}

	protected void setOwner(String owner) {
		if (owner != null)
			this.owner = owner;
	}
	
	public void updatePrice(double price) {
		setPrice(price);
	}
	
	protected void setPrice(double price) {
		if (price > 0.0D)
			this.price = price;
	}
	
	public void setBuyable(boolean var) {
		setBuyableStatus(var);
	}
	
	protected void setBuyableStatus(boolean buyable) {
		this.buyable = buyable;
		
		if (isBuyable())
			setRentableStatus(false);
		else
			setRentableStatus(isRentable());
	}
	
	public void updateRental(double rental) {
		setRental(rental);
	}
	
	protected void setRental(double rental) {
		if (rental > getPrice())
			this.rental = getPrice();
		
		if (rental > 0.0D)
			this.rental = rental;
	}
	
	public void setRentable(boolean var) {
		setRentableStatus(var);
	}
	
	protected void setRentableStatus(boolean rentable) {
		this.rentable = rentable;
	}
	
	public void updateGuiItems(Map<EdgeItemStack, Double> guiItems) {
		setGuiItems(guiItems);
	}
	
	public void addItem(EdgeItemStack item, double pricePerItem) {
		getGuiItems().put(item, pricePerItem);
	}
	
	public void removeItem(EdgeItemStack item) {
		getGuiItems().remove(item);
	}
	
	public boolean sellsItem(EdgeItemStack item) {
		return getGuiItems().containsKey(item);
	}
	
	public void openGui(Player p) {
		if (p != null)
			p.openInventory(getGui());
	}
	
	public void closeGui(Player p) {
		if (p == null) return;
		
		if (p.getInventory().equals(getGui()))
			p.closeInventory();
	}
	
	public synchronized void buyItem(EconomyPlayer ep, EdgeItemStack guiItem) throws Exception {
		if (!getGuiItems().containsKey(guiItem)) return;
		if (getItemPrice(guiItem) > ep.getCash()) {
			ep.getUser().getPlayer().sendMessage(lang.getColoredMessage(ep.getUser().getLanguage(), "notenoughmoney"));
			return;
		}
		
		Player player = ep.getUser().getPlayer();
		BankAccount shop = Economy.getInstance().getAccount(getOwner());
		
		ep.updateCash(ep.getCash() - getItemPrice(guiItem));
		shop.updateBalance(shop.getBalance() + getItemPrice(guiItem));
		
		player.getInventory().addItem(new ItemStack(guiItem.toBukkitItemStack()));
		
		player.sendMessage(lang.getColoredMessage(ep.getUser().getLanguage(), "shop_buyitem_success").replace("[0]", guiItem.getType().name()).replace("[1]", getItemPrice(guiItem) + ""));
	}
	
	public synchronized void buyItem(BankAccount acc, EdgeItemStack guiItem) throws Exception {
		if (!getGuiItems().containsKey(guiItem)) return;
		if (getItemPrice(guiItem) > acc.getBalance()) {
			acc.getUser().getPlayer().sendMessage(lang.getColoredMessage(acc.getUser().getLanguage(), "notenoughmoney"));
			return;
		}
		
		Player player = acc.getUser().getPlayer();
		BankAccount shop = Economy.getInstance().getAccount(getOwner());
		
		acc.updateBalance(acc.getBalance() - getItemPrice(guiItem));
		shop.updateBalance(shop.getBalance() + getItemPrice(guiItem));
		
		EdgeConomyAPI.transactionAPI().addTransaction(shop, acc, getItemPrice(guiItem), "Shop Transaction");
		
		player.getInventory().addItem(new ItemStack(guiItem.toBukkitItemStack()));
		
		player.sendMessage(lang.getColoredMessage(acc.getUser().getLanguage(), "shop_buyitem_success").replace("[0]", guiItem.getType().name()).replace("[1]", getItemPrice(guiItem) + ""));
	}
	
	public synchronized void sellItem(EconomyPlayer ep, EdgeItemStack guiItem) throws Exception {
		if (!getGuiItems().containsKey(guiItem)) return;
		if (!isDistributionAllowed()) return;
		
		Player player = ep.getUser().getPlayer();
		
		if (getItemPrice(guiItem) > ep.getCash()) {
			player.sendMessage(lang.getColoredMessage(ep.getUser().getLanguage(), "notenoughmoney"));
			return;
		}
		
		if (!player.getItemInHand().getType().equals(guiItem.getType())) {
			player.sendMessage(lang.getColoredMessage(ep.getUser().getLanguage(), "shop_sellitem_wrongitem"));
			return;
		}
		
		BankAccount shop = Economy.getInstance().getAccount(getOwner());
		
		if (!player.getInventory().contains(guiItem.getType())) {
			player.sendMessage(lang.getColoredMessage(ep.getUser().getLanguage(), "shop_sellitem_noitem"));
			return;
		}
		
		ep.updateCash(ep.getCash() + getItemPrice(guiItem));
		shop.updateBalance(shop.getBalance() - getItemPrice(guiItem));
		
		player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
		
		player.sendMessage(lang.getColoredMessage(ep.getUser().getLanguage(), "shop_sellitem_success").replace("[0]", guiItem.getType().name()).replace("[1]", getItemPrice(guiItem) + ""));
	}
	
	public synchronized void sellItem(BankAccount acc, EdgeItemStack guiItem) throws Exception {
		if (!getGuiItems().containsKey(guiItem)) return;
		if (!isDistributionAllowed()) return;
		
		Player player = acc.getUser().getPlayer();
		
		if (getItemPrice(guiItem) > acc.getBalance()) {
			player.sendMessage(lang.getColoredMessage(acc.getUser().getLanguage(), "notenoughmoney"));
			return;
		}
		
		if (!player.getItemInHand().getType().equals(guiItem.getType())) {
			player.sendMessage(lang.getColoredMessage(acc.getUser().getLanguage(), "shop_sellitem_wrongitem"));
			return;
		}
		
		BankAccount shop = Economy.getInstance().getAccount(getOwner());
		
		if (!player.getInventory().contains(guiItem.getType())) {
			player.sendMessage(lang.getColoredMessage(acc.getUser().getLanguage(), "shop_sellitem_noitem"));
			return;
		}
		
		acc.updateBalance(acc.getBalance() + getItemPrice(guiItem));
		shop.updateBalance(shop.getBalance() - getItemPrice(guiItem));
		
		player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
		
		player.sendMessage(lang.getColoredMessage(acc.getUser().getLanguage(), "shop_sellitem_success").replace("[0]", guiItem.getType().name()).replace("[1]", getItemPrice(guiItem) + ""));
	}
	
	protected void setupShopGui() {
		Inventory inv = null;
		
		final int guiSize = getGuiItems().size();
		int invSize = 0;
		
		if (guiSize <= 9) {
			invSize = 9;
			
		} else if(guiSize > 9 && guiSize <= 18) {
			invSize = 18;
			
		} else if(guiSize > 18 && guiSize <= 27) {
			invSize = 27;
			
		} else if(guiSize > 27 && guiSize <= 36) {
			invSize = 36;
			
		} else if(guiSize > 36 && guiSize <= 45) {
			invSize = 45;
			
		} else if(guiSize > 45 && guiSize <= 54) {
			invSize = 54; // max inventory size
		}
		
		List<ItemStack> tempContent = new ArrayList<>();
		
		for (EdgeItemStack stack : getGuiItems().keySet()) {
			if (stack != null) tempContent.add(stack.toBukkitItemStack());
		}
		
		ItemStack[] content = new ItemStack[tempContent.size()];
		content = tempContent.toArray(content);
		
		inv = Bukkit.createInventory(null, invSize, "Shop-GUI_beta");
		inv.setContents(content);
		
		this.gui = inv;
	}
	
	protected void setGuiItems(Map<EdgeItemStack, Double> guiItems) {
		if (guiItems != null)
			this.guiItems = guiItems;
	}
	
	public void allowDistribution(boolean allow) {
		this.allowDistribution = allow;
	}
	
	protected void setDistribution(boolean var) {
		this.allowDistribution = var;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		final Shop another = (Shop) obj;
		
		if (getCuboidID() == another.getCuboidID()) {
			if (getGuiItems().equals(another.getGuiItems())) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "Shop {" + getCuboidID() + ", " + getOwner() + ", $" + getPrice() + ", " + isBuyable() + ", " + getGuiItems() + "}";
	}
}
