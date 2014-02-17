package net.edgecraft.edgecuboid.cuboid.types;

public enum HabitatType {
	
	HOUSE(0),
	APARTMENT(1),
	SKYSCRAPER(2),
	HOTELROOM(3),
	LOFT(4),
	VILLA(5);
	
	private int typeID;
	
	private HabitatType(int typeID) {
		this.typeID = typeID;
	}
	
	public int getTypeID() {
		return this.typeID;
	}
	
	public static HabitatType[] getHabitatTypes() {
		
		HabitatType[] types = { HabitatType.HOUSE, HabitatType.APARTMENT, HabitatType.SKYSCRAPER, HabitatType.HOTELROOM, HabitatType.LOFT, HabitatType.VILLA };
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
