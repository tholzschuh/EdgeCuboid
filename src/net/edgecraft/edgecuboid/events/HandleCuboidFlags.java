package net.edgecraft.edgecuboid.events;

import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecore.user.UserManager;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.Flag;
import net.edgecraft.edgecuboid.world.WorldManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HandleCuboidFlags implements Listener {
	
	private final static LanguageHandler lang = EdgeCoreAPI.languageAPI();
	private final static UserManager users = EdgeCoreAPI.userAPI();
	
	@EventHandler
	public void handleBlockBreaks(BlockBreakEvent event) {
		
		final Player player = event.getPlayer();
		final User user = users.getUser(player.getName());
		
		if( user == null ) return;
		
		final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());

		if (cuboid == null) {
			
				if (!WorldManager.getInstance().isGlobalBlockInteractionAllowed() && !Level.canUse(user, Level.ARCHITECT)) {
					
					player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
					event.setCancelled(true);
				}
		} else {
				
			if (Cuboid.getCuboid(event.getBlock().getLocation()) == null && !WorldManager.getInstance().isGlobalBlockInteractionAllowed() && !Level.canUse(user, Level.ARCHITECT)) {
					
				player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
				event.setCancelled(true);
			}
				
			if (!Flag.hasFlag(cuboid, Flag.BreakBlocks, player.getName())) {
					
				player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void handleBlockPlacements(BlockPlaceEvent event) {
		
		final Player player = event.getPlayer();
		final User user = users.getUser(player.getName());
		
		if( user == null ) return;
	
		final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
		if (cuboid == null) {
				
			if (!WorldManager.getInstance().isGlobalBlockInteractionAllowed() && !Level.canUse(user, Level.ARCHITECT)) {
					
				player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
				event.setCancelled(true);
			}
		} else {
								
			if (Cuboid.getCuboid(event.getBlock().getLocation()) == null && !WorldManager.getInstance().isGlobalBlockInteractionAllowed() && !Level.canUse(user, Level.ARCHITECT)) {
					
				player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
				event.setCancelled(true);
			}
				
			if (!Flag.hasFlag(cuboid, Flag.PlaceBlocks, player.getName())) {
				
				player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void handleIllegalPlacements(PlayerInteractEvent event) {
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			final Player player = event.getPlayer();
			final User user = users.getUser(player.getName());
						
			if (user != null && event.getItem() != null) {
				
				final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
				final Material material = event.getItem().getType();
				final String item = material.name();
				
				if (material == Material.LAVA || material == Material.STATIONARY_LAVA || material == Material.LAVA_BUCKET ||
					material == Material.WATER || material == Material.STATIONARY_WATER || material == Material.WATER_BUCKET ||
					material == Material.FIRE || material == Material.FIREBALL || material == Material.FLINT_AND_STEEL) {
					
					if (cuboid == null) {
						if (!Level.canUse(user, Level.ARCHITECT)) {
							
							player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_illegalplacement").replace("[0]", item));
							event.setCancelled(true);
						}
					} else {
						if (!Level.canUse(user, Level.ARCHITECT)) {
							
							player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_illegalplacement").replace("[0]", item));
							event.setCancelled(true);
						}
						
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
			
			final Material type = event.getClickedBlock().getType();
			
			if (type == Material.CHEST || type == Material.TRAPPED_CHEST || type == Material.DISPENSER || type == Material.FURNACE || type == Material.DROPPER || type == Material.BREWING_STAND) {
				
				final Player player = event.getPlayer();
				final User user = users.getUser(player.getName());
				
				if( user == null ) return;
				
				final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
				
				if( cuboid == null ) return;
						
				if (!Flag.hasFlag(cuboid, Flag.UseContainer, player.getName())) {
							
						player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
						event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void handleFishCatchings(PlayerFishEvent event) {
		
		Player player = event.getPlayer();
		User user = users.getUser(player.getName());
		
		Entity caught = event.getCaught();
		
		if (user != null && caught instanceof Fish) {
			
			Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
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
		
		final Player player = event.getPlayer();
		final User user = users.getUser(player.getName());
		
		if( user == null ) return;
		
		final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
		if( cuboid == null ) return;

		if (!Flag.hasFlag(cuboid, Flag.UseBuckets, player.getName())) {
					
			player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void handleRedstoneInteractions(PlayerInteractEvent event) {
		
		final Block b = event.getClickedBlock();
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (b.getType() == Material.LEVER || b.getType() == Material.FENCE_GATE || b.getType() == Material.TRAP_DOOR || b.getType() == Material.STONE_BUTTON || b.getType() == Material.WOOD_BUTTON) {
				
				final Player player = event.getPlayer();
				final User user = users.getUser(player.getName());
				
				if( user == null ) return;
				
				final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
					
				if (cuboid == null) {
					if (!Level.canUse(user, Level.ARCHITECT)) {
						
						player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
						event.setCancelled(true);
					}
				} else {
					if (!Level.canUse(user, Level.ARCHITECT)) {
							
						player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission"));
						event.setCancelled(true);
					}
						
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
		
		final Entity defender = event.getEntity();
		final Entity damager = event.getDamager();
		
		if( !(defender instanceof Animals) || !(damager instanceof Player) ) return;
			
		final Player player = (Player) damager;
		final User user = users.getUser(player.getName());
		
		if( user == null ) return;
			
		final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
		if( cuboid == null ) return;

		if (!Flag.hasFlag(cuboid, Flag.InteractAnimals, player.getName())) {
						
				player.sendMessage(lang.getColoredMessage(user.getLanguage(), "cuboid_nopermission_entity"));
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void handleFrameInteraction(PlayerInteractEntityEvent event) {
		
		if( !(event.getRightClicked() instanceof ItemFrame) ) return;
		
		final User user = users.getUser( event.getPlayer().getName() );
			
		if( user == null ) return;
				
		if (!WorldManager.getInstance().isFrameRotationAllowed() && !Level.canUse(user, Level.ARCHITECT))
			event.setCancelled(true);
	}

}
