package net.edgecraft.edgecuboid.cuboid.types;

public enum HabitatType {
	
	House(0),
	Aparment(1),
	Skyscraper(2),
	Hotelroom(3),
	Loft(4),
	Villa(5);
	
	private int typeID;
	
	private HabitatType(int typeID) {
		this.typeID = typeID;
	}
	
	public int getTypeID() {
		return this.typeID;
	}
	
	public static HabitatType[] getHabitatTypes() {
		
		HabitatType[] types = { HabitatType.House, HabitatType.Aparment, HabitatType.Skyscraper, HabitatType.Hotelroom, HabitatType.Loft, HabitatType.Villa };
		return types;
		
	}
	
	public static HabitatType getType(int id) {
		
		HabitatType[] types = getHabitatTypes();
		
		for (int i = 0; i < types.length; i++) {
			if (id == types[i].getTypeID()) {
				return types[i];
			}
		}
		
		return null;
	}
}
