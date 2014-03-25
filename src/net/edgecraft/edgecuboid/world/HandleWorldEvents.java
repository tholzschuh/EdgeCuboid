package net.edgecraft.edgecuboid.world;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecore.user.UserManager;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.Cuboid;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.util.Vector;

public class HandleWorldEvents implements Listener {
	
	private static final UserManager users = EdgeCoreAPI.userAPI();
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBorderCollision(PlayerMoveEvent event) {
		
		if( ! EdgeCuboid.isEventTaskReady() ) return;
			
		final Player player = event.getPlayer();
		final User user = users.getUser(player.getName());
	
		if( user == null ) return;
		
		final Location location = player.getLocation().clone();
		final Location spawnLoc = player.getWorld().getHighestBlockAt(player.getWorld().getSpawnLocation()).getLocation();
		
		if (!Level.canUse(user, Level.ARCHITECT)) {
				
				// Get radius and distance to radius
				final int radius = WorldManager.getInstance().getWorldBorder();
				final double distance = location.distance(spawnLoc);
									
				if (distance >= radius) {
						
				final Entity vehicle = player.getVehicle();
				
				if (vehicle != null) {
						player.leaveVehicle();
							
						if (!(vehicle instanceof LivingEntity)) {
							vehicle.remove();
						} else {
							final Vector unit = event.getFrom().toVector().subtract(event.getTo().toVector()).normalize();
							vehicle.setVelocity(unit.multiply(2));
							player.sendMessage(EdgeCoreAPI.languageAPI().getColoredMessage(user.getLanguage(), "radiusreached"));
						}
					}
						
					final Vector unit = event.getFrom().toVector().subtract(event.getTo().toVector()).normalize();
					player.setVelocity(unit.multiply(2));
					player.sendMessage(EdgeCoreAPI.languageAPI().getColoredMessage(user.getLanguage(), "radiusreached"));
				}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onIceMelt(BlockFadeEvent event) {
		
		final Block block = event.getBlock();
		
		if (!WorldManager.getInstance().isIceMeltAllowed()) {
			if (block.getType() == Material.ICE) {
				
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void handleBlockBurn(BlockBurnEvent event) {		
		if (!WorldManager.getInstance().isFireSpreadAllowed() && Cuboid.getCuboid(event.getBlock().getLocation()) == null) {
			
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
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void handleItemFrameRotation(PlayerInteractEntityEvent event) {
		
		final Player player = event.getPlayer();
		User user = users.getUser(player.getName());
		
		if (user == null)
			return;
		
		if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
			
			final ItemFrame frame = (ItemFrame) event.getRightClicked();
			
			if (frame.getItem() == null || frame.getItem().getType() == Material.AIR)
				return;
			
			if (!Level.canUse(user, Level.ARCHITECT)) {
				event.setCancelled(true);
			}
		}
	}
}
