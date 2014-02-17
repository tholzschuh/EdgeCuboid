package net.edgecraft.edgecuboid.cuboid;

public enum CuboidEvent {
	
	PVP(0),
	GOD(1),
	MOB(2),
	HEAL(3),
	HURT(4),
	INVIS(6),
	CRASH(7),
	NOCHAT(8),
	NOENTER(9);
	
	private int typeID;
	
	private CuboidEvent(int typeID) {
		this.typeID = typeID;
	}
	
	public int getTypeID() {
		return this.typeID;
	}
	
	public static CuboidEvent[] getCuboidEvents() {
		
		CuboidEvent[] types = { CuboidEvent.PVP, CuboidEvent.GOD, CuboidEvent.MOB, CuboidEvent.HEAL, CuboidEvent.HURT, CuboidEvent.INVIS, CuboidEvent.CRASH, CuboidEvent.NOCHAT, CuboidEvent.NOENTER };
		return types;
		
	}
	
	public static CuboidEvent getType(int id) {
		
		CuboidEvent[] types = getCuboidEvents();
		
		for (int i = 0; i < types.length; i++) {
			if (id == types[i].getTypeID()) {
				return types[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if an event is enabled in the given cuboid
	 * @param c
	 * @param event
	 * @return true/false
	 */
	public static boolean hasEvent(Cuboid c, CuboidEvent event) {
		if (c == null || event == null) return false;
		
		return c.getEvents().contains(event);
	}
	
	/**
	 * Enables the given event in the given cuboid
	 * @param c
	 * @param event
	 */
	public static void enableEvent(Cuboid c, CuboidEvent event) {
		if (c == null || event == null) return;
		if (c.getEvents().contains(event)) return;
		
		c.getEvents().add(event);
	}
	
	/**
	 * Disabled the given event in the given cuboid
	 * @param c
	 * @param event
	 */
	public static void disableEvent(Cuboid c, CuboidEvent event) {
		if (c == null || event == null) return;
		if (!c.getEvents().contains(event)) return;
		
		c.getEvents().remove(event);
	}
}
