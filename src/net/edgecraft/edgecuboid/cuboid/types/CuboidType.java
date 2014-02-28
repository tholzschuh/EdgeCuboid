package net.edgecraft.edgecuboid.cuboid.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.edgecraft.edgecuboid.cuboid.CuboidEvent;

public enum CuboidType {
	
	PUBLIC(0, new CuboidEvent[] { CuboidEvent.PVP }),
	BANK(1, new CuboidEvent[] { CuboidEvent.PVP }),
	JAIL(2, new CuboidEvent[] { CuboidEvent.GOD, CuboidEvent.NOCHAT, CuboidEvent.NOENTER }),
	HOSPITAL(3, new CuboidEvent[] { CuboidEvent.GOD, CuboidEvent.HEAL }),
	POLICE(4, new CuboidEvent[] { CuboidEvent.PVP, CuboidEvent.HEAL }),
	FIREDEPARTMENT(5, new CuboidEvent[] { CuboidEvent.PVP, CuboidEvent.HEAL }),
	ATM(6, new CuboidEvent[] { CuboidEvent.PVP }),
	SHOP(7, new CuboidEvent[] { CuboidEvent.PVP }),
	SIGHT(8, new CuboidEvent[] { }),
	RAIL(9, new CuboidEvent[] { CuboidEvent.NOENTER }),
	STREET(10, new CuboidEvent[] { }),
	PARK(11, new CuboidEvent[] { CuboidEvent.PVP }),
	HABITAT(12, new CuboidEvent[] { CuboidEvent.PVP }),
	LIFT(13, new CuboidEvent[] { CuboidEvent.GOD, CuboidEvent.NOCHAT });
	
	private int typeID;
	private CuboidEvent[] events;
	
	private CuboidType(int typeID, CuboidEvent[] events) {
		this.typeID = typeID;
		this.events = events;
	}
	
	public int getTypeID() {
		return this.typeID;
	}
	
	public CuboidEvent[] getEvents() {
		return this.events;
	}
	
	public List<CuboidEvent> getEventList() {
		return new ArrayList<CuboidEvent>(Arrays.asList(getEvents()));
	}
	
	public static CuboidType[] getCuboidTypes() {
		
		CuboidType[] types = 
			{ 
				CuboidType.PUBLIC, CuboidType.BANK, CuboidType.JAIL, 
				CuboidType.HOSPITAL, CuboidType.POLICE, CuboidType.FIREDEPARTMENT, 
				CuboidType.ATM, CuboidType.SHOP, CuboidType.SIGHT, 
				CuboidType.RAIL, CuboidType.STREET, CuboidType.PARK, CuboidType.HABITAT 
			};
		
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
