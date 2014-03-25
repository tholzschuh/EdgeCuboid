package net.edgecraft.edgecuboid.events;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidEvent;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;

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
import org.bukkit.util.Vector;

public class HandleCuboidEvents implements Listener {
	
	@EventHandler
	public void handleEvents(PlayerMoveEvent event) {
		
		if (EdgeCuboid.isEventTaskReady()) {
			
			final Player player = event.getPlayer();
			final Location from = event.getFrom();
			final Location to = event.getTo();
			
			final User user = EdgeCoreAPI.userAPI().getUser(player.getName());
			final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
			if (user != null) {
				
				if (cuboid == null) {
					
					for (Player p : Bukkit.getOnlinePlayers()) {
						player.showPlayer(p);
					}
					
					for (Cuboid c : CuboidHandler.getInstance().getCuboids().values()) {
						if (c.getParticipants().contains(player.getName()))
							c.getParticipants().remove(player.getName());
					}
				} 
				
				if (cuboid != null) {				
					
					if (!cuboid.getParticipants().contains(player.getName())) {
						cuboid.getParticipants().add(player.getName());
					}
										
					if (!cuboid.getParticipants().contains(player.getName())) return; // Do not go further for events if players aren't participants
					
					if (cuboid.hasEvent(CuboidEvent.Heal)) {
						
						double health = player.getHealth() + 2D;					
						if (health >= 20) health = 20;
						
						player.setHealth(health);
						
					}
					
					if (cuboid.hasEvent(CuboidEvent.Hurt)) {
						
						if (player.getGameMode() == GameMode.CREATIVE || cuboid.hasEvent(CuboidEvent.God) || cuboid.hasEvent(CuboidEvent.Heal)) {
							return;
						}
						
						double health = player.getHealth() - 1D;
						if (health <= 0) health = 0;
						
						player.setHealth(health);
						
					}
					
					if (cuboid.hasEvent(CuboidEvent.Invis)) {
						
						for (Player p : Bukkit.getOnlinePlayers()) {
							
							User u = EdgeCoreAPI.userAPI().getUser(p.getName());
							
							if (u != null)
								player.hidePlayer(p);
						}
						
					}
					
					if (cuboid.hasEvent(CuboidEvent.NoEnter)) {						
						if (!Level.canUse(user, Level.ARCHITECT)) {	
							if (!cuboid.isInside(from) && cuboid.isInside(to)) {
								Vector unit = player.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
								player.setVelocity(unit.multiply(2));
							}
						}						
					}
					
					if (!Level.canUse(user, cuboid.getModifyLevel())) {
						if (!cuboid.isInside(from) && cuboid.isInside(to)) {
							Vector unit = player.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
							player.setVelocity(unit.multiply(2));
						}
					}
				}				
			}
		}
		
	}
	
	@EventHandler
	public void handleDamage(EntityDamageEvent event) {
		
		if( !(event.getEntity() instanceof Player) )
			return;
		
		final Player player = (Player) event.getEntity();
		final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
		if( cuboid == null ) return;	

		if (cuboid.hasEvent(CuboidEvent.God) ) event.setCancelled(true);
				
		if ( event instanceof EntityDamageByEntityEvent ) {
					
				Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
					
				if (!cuboid.hasEvent(CuboidEvent.PvP)) {
						
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
	
	@EventHandler
	public void handleChat(AsyncPlayerChatEvent event) {
		
		final Player player = event.getPlayer();
		final User user = EdgeCoreAPI.userAPI().getUser(player.getName());
		
		if( user == null ) return;
		
		final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
		if( cuboid == null ) return;
		
		if (cuboid.hasEvent(CuboidEvent.NoChat) && cuboid.getParticipants().contains(player.getName())) {
					
				if (!Level.canUse(user, Level.SUPPORTER)) {
						
					event.getRecipients().remove(player.getName());
					event.setCancelled(true);
				}
		}				
	}	
}
