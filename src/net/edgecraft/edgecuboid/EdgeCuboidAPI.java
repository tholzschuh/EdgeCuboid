package net.edgecraft.edgecuboid;

import net.edgecraft.edgecuboid.cuboid.CuboidHandler;
import net.edgecraft.edgecuboid.world.WorldManager;

public class EdgeCuboidAPI {
	
	private static final CuboidHandler cuboidAPI = EdgeCuboid.getCuboidAPI();
	private static final WorldManager worldAPI = EdgeCuboid.getWorldAPI();
	
	private EdgeCuboidAPI() { /* ... */ }
	
	/**
	 * Returns the CuboidAPI
	 * @return CuboidHandler
	 */
	public static final CuboidHandler cuboidAPI() {
		return cuboidAPI;
	}
	
	/**
	 * Returns the WorldAPI
	 * @return WorldManager
	 */
	public static final WorldManager worldAPI() {
		return worldAPI;
	}
}
