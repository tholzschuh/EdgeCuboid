package net.edgecraft.edgecuboid.other;

import net.edgecraft.edgecuboid.EdgeCuboid;

import org.bukkit.scheduler.BukkitRunnable;

public class EventTask extends BukkitRunnable {
	
	public EventTask() { }
	
	public void run() {
		
		if (EdgeCuboid.isEventTaskReady())
			EdgeCuboid.setEventTask(false);
		else
			EdgeCuboid.setEventTask(true);
		
	}
}
