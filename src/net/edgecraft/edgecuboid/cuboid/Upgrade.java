package net.edgecraft.edgecuboid.cuboid;

public class Upgrade {
	
	public enum UpgradeType {
	
		FULL_INSURANCE(0, "Full Insurance", false),
		FIRE_INSURANCE(1, "Fire Insurance", false),
		WATER_INSURANCE(2, "Water Insurance", false),
		THIEF_INSURANCE(3, "Thief Insurance", false),
		
		ELECTRICITY_LIMITED(4, "Limited Electricity", false),
		ELECTRICITY_EXTENDED(5, "Extended Electricity", false),
		ELECTRICITY_UNLIMITED(6, "Unlimited Electricity", false),
		
		PROTECTION(7, "Protection", false),
		PROTECTION_REINFORCED(8, "Reinforced Protection", false),
		PROTECTION_ULTIMATE(9, "Ultimate Protection", false),
		
		EXPANSION(10, "Expansion", true);
		
		private int id;
		private String name;
		private boolean multiple;
		
		private UpgradeType(int id, String name, boolean multiple) {
			this.id = id;
			this.name = name;
			this.multiple = multiple;
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
	}
	
	private UpgradeType type;
	private double price;
	private double monthlyPrice;
	
	protected Upgrade() { /* ... */ }
	
	public Upgrade(UpgradeType type, double price, double monthlyPrice) {
		setType(type);
		setPrice(price);
		setMonthlyPrice(monthlyPrice);
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
