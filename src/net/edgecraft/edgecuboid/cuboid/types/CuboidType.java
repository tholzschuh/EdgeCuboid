package net.edgecraft.edgecuboid.cuboid.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.edgecraft.edgecuboid.cuboid.CuboidEvent;

public enum CuboidType {
	
	Survival(0, new CuboidEvent[] { CuboidEvent.PvP }),
	Creative(1, new CuboidEvent[] { }),
	Public(2, new CuboidEvent[] { CuboidEvent.PvP }),
	Rail(3, new CuboidEvent[] { CuboidEvent.NoEnter }),
	Street(4, new CuboidEvent[] { }),
	Park(5, new CuboidEvent[] { CuboidEvent.PvP }),
	Sight(6, new CuboidEvent[] { }),
	Habitat(7, new CuboidEvent[] { CuboidEvent.PvP }),
	Shop(8, new CuboidEvent[] { CuboidEvent.PvP }),
	Lift(9, new CuboidEvent[] { CuboidEvent.God, CuboidEvent.NoChat }),
	Jail(10, new CuboidEvent[] { CuboidEvent.God, CuboidEvent.NoChat, CuboidEvent.NoEnter }),
	Airport(11, new CuboidEvent[] { CuboidEvent.PvP }),
	Station(12, new CuboidEvent[] { CuboidEvent.PvP }),
	Bank(13, new CuboidEvent[] { CuboidEvent.PvP }),
	ATM(14, new CuboidEvent[] { CuboidEvent.PvP }),
	Hospital(15, new CuboidEvent[] { CuboidEvent.God, CuboidEvent.Heal }),
	PoliceStation(16, new CuboidEvent[] { CuboidEvent.PvP, CuboidEvent.Heal }),
	FireStation(17, new CuboidEvent[] { CuboidEvent.PvP, CuboidEvent.Heal });
	
	private final int typeID;
	private final CuboidEvent[] events;
	
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
		
		final CuboidType[] types = 
			{ 
				
				CuboidType.Survival, CuboidType.Creative, CuboidType.Public,
				CuboidType.Rail, CuboidType.Street, CuboidType.Park,
				CuboidType.Sight, CuboidType.Habitat, CuboidType.Shop,
				CuboidType.Lift, CuboidType.Jail, CuboidType.Airport,
				CuboidType.Station, CuboidType.Bank, CuboidType.ATM,
				CuboidType.Hospital, CuboidType.PoliceStation, CuboidType.FireStation
				
			};
		
		return types;
		
	}
	
	public static CuboidType getType(int id) {
		
		final CuboidType[] types = getCuboidTypes();
		
		for (int i = 0; i < types.length; i++) {
			if (id == types[i].getTypeID()) {
				return types[i];
			}
		}
		
		return null;
	}
}
