package net.edgecraft.edgecuboid.cuboid.types;

public enum CuboidType {
	
	CUBOID(0),
	SPHERE(1),
	POLYGONAL(2);
	
	private int typeID;
	
	private CuboidType(int typeID) {
		this.typeID = typeID;
	}
	
	public int getTypeID() {
		return this.typeID;
	}
	
	public static CuboidType[] getCuboidTypes() {
		
		CuboidType[] types = { CuboidType.CUBOID, CuboidType.SPHERE, CuboidType.POLYGONAL };
		return types;
		
	}
	
	public static CuboidType getType(int id) {
		
		CuboidType[] types = getCuboidTypes();
		
		for (int i = 0; i < types.length; i++) {
			if (id == types[i].getTypeID()) {
				return types[i];
			}
		}
		
		return null;
	}
}
