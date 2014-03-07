package net.edgecraft.edgecuboid.cuboid;

import java.io.Serializable;

public class Upgrade implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum UpgradeType implements Serializable {
	
		FULL_INSURANCE(0, "Full Insurance", false, 13500, 4000),
		FIRE_INSURANCE(1, "Fire Insurance", false, 5000, 1500),
		WATER_INSURANCE(2, "Water Insurance", false, 5000, 1500),
		THIEF_INSURANCE(3, "Thief Insurance", false, 5000, 1500),
		
		ELECTRICITY_LIMITED(4, "Limited Electricity", false, 2000, 450),
		ELECTRICITY_EXTENDED(5, "Extended Electricity", false, 3500, 650),
		ELECTRICITY_UNLIMITED(6, "Unlimited Electricity", false, 6500, 800),
		
		PROTECTION(7, "Protection", false, 500, 50),
		PROTECTION_REINFORCED(8, "Reinforced Protection", false, 1250, 120),
		PROTECTION_ULTIMATE(9, "Ultimate Protection", false, 3450, 320),
		
		EXPANSION(10, "Expansion", true, 1000, 5);
		
		private int id;
		private String name;
		private boolean multiple;
		private double price;
		private double monthlyPrice;
		
		private UpgradeType(int id, String name, boolean multiple, double price, double monthlyPrice) {
			this.id = id;
			this.name = name;
			this.multiple = multiple;
			this.price = price;
		}
		
		public int getID() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean isMultiple() {
			return multiple;
		}
		
		public double getPrice() {
			return price;
		}
		
		public double getMonthlyPrice() {
			return monthlyPrice;
		}
	}
	
	private UpgradeType type;
	private double price;
	private double monthlyPrice;
	
	protected Upgrade() { /* ... */ }
	
	public Upgrade(UpgradeType type, double monthlyPrice) {
		setType(type);
		setPrice(type.getPrice());
		setMonthlyPrice(monthlyPrice);
	}
	
	public Upgrade(UpgradeType type) {
		setType(type);
		setPrice(type.getPrice());
		setMonthlyPrice(type.getMonthlyPrice());
	}
	
	public UpgradeType getType() {
		return type;
	}
	
	public int getTypeID() {
		return getType().getID();
	}
	
	public String getTypeName() {
		return getType().getName();
	}
	
	public double getPrice() {
		return price;
	}
	
	public double getMonthlyPrice() {
		return monthlyPrice;
	}
	
	public boolean multipleUsage() {
		return getType().isMultiple();
	}
	
	protected void setType(UpgradeType type) {
		if (type != null)
			this.type = type;
	}
	
	protected void setPrice(double price) {
		if (price > 0)
			this.price = price;
	}
	
	protected void setMonthlyPrice(double monthlyPrice) {
		if (monthlyPrice > 0)
			this.monthlyPrice = monthlyPrice;
	}
}
