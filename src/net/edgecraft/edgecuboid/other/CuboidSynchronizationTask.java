package net.edgecraft.edgecuboid.other;

import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;

import org.bukkit.scheduler.BukkitRunnable;

public class CuboidSynchronizationTask extends BukkitRunnable {
	
	public CuboidSynchronizationTask() { }
	
	public void run() {
		
		EdgeCuboid.log.info(EdgeCuboid.cuboidbanner + "Starte Cuboid-Synchronisation..");
		CuboidHandler.getInstance().synchronizeCuboidManagement(true, true);
		EdgeCuboid.log.info(EdgeCuboid.cuboidbanner + "Automatische Cuboid-Synchronisation abgeschlossen!");
		
	}
}
