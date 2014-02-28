package net.edgecraft.edgecuboid.events;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidEvent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class HandleCuboidEvents implements Listener {
	
	@EventHandler
	public void handleEvents(PlayerMoveEvent event) {
		
		if (EdgeCuboid.isEventTaskReady()) {
			
			Player player = event.getPlayer();
			Location from = event.getFrom();
			Location to = event.getTo();
			
			User user = EdgeCoreAPI.userAPI().getUser(player.getName());
			Cuboid cuboid = Cuboid.getCuboid(player);
			
			if (user != null) {
				
				if (cuboid == null) {
					
					for (Player p : Bukkit.getOnlinePlayers()) {
						player.showPlayer(p);
					}
					
				} 
				
				if (cuboid != null) {				
					
					if (!cuboid.getParticipants().contains(player.getName())) {
						cuboid.getParticipants().add(player.getName());
					}
					
					if (cuboid.getParticipants().contains(player.getName())) {
						cuboid.getParticipants().remove(player.getName());
					}
					
					if (cuboid.getParticipants().contains(player.getName())) return; // Do not go further for events if players aren't participant
					
					if (cuboid.hasEvent(CuboidEvent.HEAL)) {
						
						double health = player.getHealth() + 2D;					
						if (health >= 20) health = 20;
						
						player.setHealth(health);
						
					}
					
					if (cuboid.hasEvent(CuboidEvent.HURT)) {
						
						if (player.getGameMode() == GameMode.CREATIVE)
							player.setHealth(player.getHealth());
						
						double health = player.getHealth() - 2D;
						if (health <= 0) health = 0;
						
						player.setHealth(health);
						
					}
					
					if (cuboid.hasEvent(CuboidEvent.INVIS)) {
						
						for (Player p : Bukkit.getOnlinePlayers()) {
							
							User u = EdgeCoreAPI.userAPI().getUser(p.getName());
							
							if (u != null)
								if (!Level.canUse(u, Level.TEAM)) {
									player.hidePlayer(p);
								}
						}
						
					}
					
					if (cuboid.hasEvent(CuboidEvent.NOENTER)) {						
						if (!Level.canUse(user, Level.TEAM)) {	
							
							if (!cuboid.isInside(from) && cuboid.isInside(to)) 
								player.teleport(from);
								
						}						
					}
				}				
			}
		}
		
	}
	
	@EventHandler
	public void handleDamage(EntityDamageEvent event) {
		
		if (EdgeCuboid.isEventTaskReady()) {
			
			Entity entity = event.getEntity();
			
			if (entity instanceof Player) {
				
				Player player = (Player) entity;
				Cuboid cuboid = Cuboid.getCuboid(player);
				
				if (cuboid != null) {
					
					if (cuboid.hasEvent(CuboidEvent.GOD)) {
						event.setCancelled(true);
					}
					
					if (event instanceof EntityDamageByEntityEvent) {
						
						Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
						
						if (!cuboid.hasEvent(CuboidEvent.PVP)) {
							
							if (damager instanceof Arrow) {
								
								damager = ((Arrow) damager).getShooter();
								
								if (damager instanceof Player)
									event.setCancelled(true);
							}
							
							if (damager instanceof Player)
								event.setCancelled(true);
						}
					}
				}
			}
			
		}		
	}
	
	@EventHandler
	public void handleChat(AsyncPlayerChatEvent event) {
		
		if (EdgeCuboid.isEventTaskReady()) {
			
			Player player = event.getPlayer();
			User user = EdgeCoreAPI.userAPI().getUser(player.getName());
			
			if (user != null) {
				
				Cuboid cuboid = Cuboid.getCuboid(player);
				
				if (cuboid != null) {				
					if (cuboid.hasEvent(CuboidEvent.NOCHAT) && cuboid.getParticipants().contains(player.getName())) {
						
						if (!Level.canUse(user, Level.TEAM)) {
							
							event.getRecipients().remove(player.getName());
							event.setCancelled(true);
							
						}
					}				
				}
			}
			
		}		
	}
}
