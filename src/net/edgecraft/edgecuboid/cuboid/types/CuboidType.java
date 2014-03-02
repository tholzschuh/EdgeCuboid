package net.edgecraft.edgecuboid.cuboid.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.edgecraft.edgecuboid.cuboid.CuboidEvent;

public enum CuboidType {
	
	Public(0, new CuboidEvent[] { CuboidEvent.PvP }),
	Bank(1, new CuboidEvent[] { CuboidEvent.PvP }),
	Jail(2, new CuboidEvent[] { CuboidEvent.God, CuboidEvent.NoChat, CuboidEvent.NoEnter }),
	Hospital(3, new CuboidEvent[] { CuboidEvent.God, CuboidEvent.Heal }),
	PoliceStation(4, new CuboidEvent[] { CuboidEvent.PvP, CuboidEvent.Heal }),
	FireDepartment(5, new CuboidEvent[] { CuboidEvent.PvP, CuboidEvent.Heal }),
	ATM(6, new CuboidEvent[] { CuboidEvent.PvP }),
	Shop(7, new CuboidEvent[] { CuboidEvent.PvP }),
	Sight(8, new CuboidEvent[] { }),
	Rail(9, new CuboidEvent[] { CuboidEvent.NoEnter }),
	Street(10, new CuboidEvent[] { }),
	Park(11, new CuboidEvent[] { CuboidEvent.PvP }),
	Habitat(12, new CuboidEvent[] { CuboidEvent.PvP }),
	Lift(13, new CuboidEvent[] { CuboidEvent.God, CuboidEvent.NoChat });
	
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
				CuboidType.Public, CuboidType.Bank, CuboidType.Jail, 
				CuboidType.Hospital, CuboidType.PoliceStation, CuboidType.FireDepartment, 
				CuboidType.ATM, CuboidType.Shop, CuboidType.Sight, 
				CuboidType.Rail, CuboidType.Street, CuboidType.Park, CuboidType.Habitat 
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
