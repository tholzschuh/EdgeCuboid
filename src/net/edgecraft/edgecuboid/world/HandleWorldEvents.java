package net.edgecraft.edgecuboid.world;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboid;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class HandleWorldEvents implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBorderCollision(PlayerMoveEvent event) {
		
		// Check if event tick is ready to go
		if (EdgeCuboid.isEventTaskReady()) {
			
			Player player = event.getPlayer();
			User user = EdgeCoreAPI.userAPI().getUser(player.getName());
			
			Location location = player.getLocation().clone();
			Location spawnLoc = player.getWorld().getHighestBlockAt(player.getWorld().getSpawnLocation()).getLocation();
			
			// Check if user exists and it's level > Architect
			if (user != null) {
				if (!Level.canUse(user, Level.ARCHITECT)) {
					
					// Get radius and distance to radius
					int radius = WorldManager.getInstance().getWorldBorder();
					double distance = location.distance(spawnLoc);
										
					if (distance >= radius) {
						
						// Check if the player's in a vehicle
						Entity vehicle = player.getVehicle();
						
						// Let the player leave the vehicle
						if (vehicle != null) {
							player.leaveVehicle();
							
							/*
							 * If the vehicle is an instance of LivingEntity (like horse or pig), teleport it to the from-location
							 * If not, remove the entity
							 */
							if (!(vehicle instanceof LivingEntity)) {
								vehicle.remove();
							} else {
								vehicle.teleport(vehicle.getWorld().getHighestBlockAt(vehicle.getWorld().getSpawnLocation()).getLocation());
								player.sendMessage(EdgeCoreAPI.languageAPI().getColoredMessage(user.getLanguage(), "radiusreached"));
							}
						}
						
						// Finally, after all checks, teleport the player to the location it's coming from and let him know why
						player.teleport(player.getWorld().getHighestBlockAt(player.getWorld().getSpawnLocation()).getLocation());
						player.sendMessage(EdgeCoreAPI.languageAPI().getColoredMessage(user.getLanguage(), "radiusreached"));
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onIceMelt(BlockFadeEvent event) {
		
		Block block = event.getBlock();
		
		if (!WorldManager.getInstance().isIceMeltAllowed()) {
			if (block.getType() == Material.ICE) {
				
				event.setCancelled(true);
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void handleBlockBurn(BlockBurnEvent event) {		
		if (!WorldManager.getInstance().isFireSpreadAllowed()) {
			
			event.setCancelled(true);
			
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void handleFireSpread(BlockSpreadEvent event) {
		if (!WorldManager.getInstance().isFireSpreadAllowed()) {
			if (event.getSource().getType() == Material.FIRE) {
				
				event.setCancelled(true);
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void handleStructureGrowing(StructureGrowEvent event) {
		if (!WorldManager.getInstance().isStructureGrowingAllowed()) {
			
			event.setCancelled(true);
			
		}
	}
}
