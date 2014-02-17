package net.edgecraft.edgecuboid.events;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.Flag;
import net.edgecraft.edgecuboid.world.WorldManager;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HandleCuboidFlags implements Listener {
	
	private final LanguageHandler lang = EdgeCoreAPI.languageAPI();
	
	@EventHandler
	public void handleBlockBreaks(BlockBreakEvent event) {
		
		Player player = event.getPlayer();
		User user = EdgeCoreAPI.userAPI().getUser(player.getName());
		
		if (user != null) {
			
			Cuboid cuboid = Cuboid.getCuboid(player);
			
			if (cuboid == null ) {
				if (!WorldManager.getInstance().isGlobalBlockInteractionAllowed()) {
					
					player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
					event.setCancelled(true);
					
				}
			}
			
			if (cuboid != null) {
				if (!Flag.hasFlag(cuboid, Flag.BreakBlocks, player.getName())) {
					
					player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
					event.setCancelled(true);
					
				}
			}
		}
	}
	
	@EventHandler
	public void handleBlockPlacements(BlockPlaceEvent event) {
		
		Player player = event.getPlayer();
		User user = EdgeCoreAPI.userAPI().getUser(player.getName());
		
		if (user != null) {
			
			Cuboid cuboid = Cuboid.getCuboid(player);
			
			if (cuboid == null ) {
				if (!WorldManager.getInstance().isGlobalBlockInteractionAllowed()) {
					
					player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
					event.setCancelled(true);
					
				}
			}
			
			if (cuboid != null) {
				if (!Flag.hasFlag(cuboid, Flag.PlaceBlocks, player.getName())) {
					
					player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
					event.setCancelled(true);
					
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void handleIllegalPlacements(PlayerInteractEvent event) {
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			Player player = event.getPlayer();
			User user = EdgeCoreAPI.userAPI().getUser(player.getName());
			
			Material material = event.getItem().getType();
			String item = material.name().toLowerCase();
			
			if (user != null) {
				
				Cuboid cuboid = Cuboid.getCuboid(player);
				
				if (material == Material.LAVA || material == Material.STATIONARY_LAVA || material == Material.LAVA_BUCKET ||
					material == Material.WATER || material == Material.STATIONARY_WATER || material == Material.WATER_BUCKET ||
					material == Material.FIRE || material == Material.FIREBALL || material == Material.FLINT_AND_STEEL) {
					
					if (cuboid == null) {
						if (!Level.canUse(user, Level.TEAM)) {
							
							player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_illegalplacement").replace("[0]", item));
							event.setCancelled(true);
							
						}
					}
					
					if (cuboid != null) {
						if (!Flag.hasFlag(cuboid, Flag.PlaceIllegalBlocks, player.getName())) {
							
							player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_illegalplacement").replace("[0]", item));
						}
					}
				}
			}
			
		}
		
	}
	
	@EventHandler
	public void handleContainerUsages(PlayerInteractEvent event) {
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			Player player = event.getPlayer();
			User user = EdgeCoreAPI.userAPI().getUser(player.getName());
			
			if (user != null) {
				
				Cuboid cuboid = Cuboid.getCuboid(player);
				
				if (cuboid != null) {
					if (!Flag.hasFlag(cuboid, Flag.UseContainer, player.getName())) {
						
						player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
						event.setCancelled(true);
						
					}
				}
			}
			
		}
		
	}
	
	@EventHandler
	public void handleFishCatchings(PlayerFishEvent event) {
		
		Player player = event.getPlayer();
		User user = EdgeCoreAPI.userAPI().getUser(player.getName());
		
		Entity caught = event.getCaught();
		
		if (user != null && caught instanceof Fish) {
			
			Cuboid cuboid = Cuboid.getCuboid(player);
			
			if (cuboid != null) {
				if (!Flag.hasFlag(cuboid, Flag.InteractAnimals, player.getName())) {
					
					player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission_entity"));
					event.setCancelled(true);
					
				}
			}
		}
	}
	
	@EventHandler
	public void handleBucketFillings(PlayerBucketFillEvent event) {
		
		Player player = event.getPlayer();
		User user = EdgeCoreAPI.userAPI().getUser(player.getName());
		
		if (user != null) {
			
			Cuboid cuboid = Cuboid.getCuboid(player);
			
			if (cuboid != null) {
				if (!Flag.hasFlag(cuboid, Flag.UseBuckets, player.getName())) {
					
					player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
					event.setCancelled(true);
					
				}
			}
		}
	}
	
	@EventHandler
	public void handleRedstoneInteractions(PlayerInteractEvent event) {
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			
			Player player = event.getPlayer();
			User user = EdgeCoreAPI.userAPI().getUser(player.getName());
			
			if (user != null) {
				
				Cuboid cuboid = Cuboid.getCuboid(player);
				
				if (cuboid == null) {
					if (!Level.canUse(user, Level.ARCHITECT)) {
						
						player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
						event.setCancelled(true);
						
					}
				}
				
				if (cuboid != null) {
					if (!Flag.hasFlag(cuboid, Flag.InteractRedstone, player.getName())) {
						
						player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
						event.setCancelled(true);
						
					}
				}
			}
			
		}
		
	}
	
	@EventHandler
	public void handleEntityInteractions(EntityDamageByEntityEvent event) {
		
		Entity defender = event.getEntity();
		Entity damager = event.getDamager();
		
		if (defender instanceof Animals && damager instanceof Player) {
			
			Player player = (Player) damager;
			User user = EdgeCoreAPI.userAPI().getUser(player.getName());
			
			if (user != null) {
				
				Cuboid cuboid = Cuboid.getCuboid(player);
				
				if (cuboid != null) {
					if (!Flag.hasFlag(cuboid, Flag.InteractAnimals, player.getName())) {
						
						player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission_entity"));
						event.setCancelled(true);
						
					}
				}
			}
		}
	}
}
