package net.edgecraft.edgecuboid.cuboid;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;

public enum Flag {
	
	BreakBlocks(0),
	PlaceBlocks(1),
	PlaceIllegalBlocks(2),
	
	InteractRedstone(3),
	InteractAnimals(4),
	
	UseContainer(5),
	UseBuckets(6),
	
	EditCuboids(7),
	EditFlags(8),
	EditEvents(9);
	
	private int id;
	
	private Flag(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public static Flag[] getFlags() {
		
		Flag[] flags = { 
				
				Flag.BreakBlocks, Flag.PlaceBlocks, Flag.PlaceIllegalBlocks,
				Flag.InteractAnimals, Flag.InteractRedstone, 
				Flag.UseContainer, Flag.UseBuckets, Flag.EditCuboids, 
				Flag.EditFlags, Flag.EditEvents 
				
				};
		
		return flags;
	}
	
	public static Flag getFlag(int id) {
		
		Flag[] flags = getFlags();
		
		for (int i = 0; i < flags.length; i++) {
			if (id == flags[i].getID()) {
				return flags[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns if a player has the given flag in the given cuboid
	 * @param c
	 * @param flag
	 * @param player
	 * @return true/false
	 */
	public static boolean hasFlag(Cuboid c, Flag flag, String player) {
		if (c == null || player == null || flag == null) return false;
		
		User user = EdgeCoreAPI.userAPI().getUser(player);
		if (user != null && c.isOwner(user)) return true;
		if (user != null && Level.canUse(user, c.getModifyLevel())) return true;
		
		return c.getFlags().get(flag).contains(player);
	}
	
	/**
	 * Gives the player the permission for the given flag in the given cuboid
	 * @param c
	 * @param player
	 * @param flag
	 */
	public static void giveFlag(Cuboid c, String player, Flag flag) {
		if (c == null || player == null || flag == null) return;
		if (c.getFlags().get(flag).contains(player)) return;
		
		c.getFlags().get(flag).add(player);
	}
	
	/**
	 * Removes the permission for the given flag in the given cuboid for the given player
	 * @param c
	 * @param player
	 * @param flag
	 */
	public static void removeFlag(Cuboid c, String player, Flag flag) {
		if (c == null || player == null || flag == null) return;
		if (!c.getFlags().get(flag).contains(player)) return;
		
		c.getFlags().get(flag).remove(player);
	}
}
