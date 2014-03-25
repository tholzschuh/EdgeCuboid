package net.edgecraft.edgecuboid.shop;

import net.edgecraft.edgeconomy.EdgeConomyAPI;
import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.economy.EconomyPlayer;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.other.EdgeItemStack;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class HandleShopEvents implements Listener {
	
	private final Economy economy = EdgeConomyAPI.economyAPI();
	private final ShopHandler shops = ShopHandler.getInstance();
	
	@EventHandler
	public void openShopGUI(PlayerInteractEvent event) {
		if( event.getAction() != Action.RIGHT_CLICK_BLOCK ) return;

		final Player player = event.getPlayer();
		final Block block = event.getClickedBlock();
		final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
		if( cuboid == null ) return;
			
			if (!cuboid.isInside(block.getLocation()))
				return;
				
			final Shop shop = shops.getShop(cuboid);
				
			if (shop == null)
				return;
				
			if (!shops.getShopFrames().contains(block.getType()))
				return;
				
			player.openInventory(shop.getGui());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void handleItemPurchase(InventoryClickEvent event) {

		if( event.getClick() != ClickType.LEFT ) return;
		
		final Player player = (Player) event.getWhoClicked();
		final Inventory inventory = event.getInventory();
		final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
		if( cuboid == null ) return;
			
			final Shop shop = shops.getShop(cuboid);
				
			if (shop == null)
				return;
			
			if (inventory.getName().equals(shop.getGui().getName())) {
					
				event.setCancelled(true);
				player.updateInventory();
					
				if ( economy.getAccount(player.getName()) == null) {
				
					final EconomyPlayer ep = economy.getEconomyPlayer(player.getName());
					final EdgeItemStack item = new EdgeItemStack(event.getCurrentItem());
					shop.buyItem(ep, item);
					} else {
						
						final BankAccount acc = economy.getAccount(player.getName());
						final EdgeItemStack item = new EdgeItemStack(event.getCurrentItem());
						
						shop.buyItem(acc, item);
						
					}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void handleItemSale(InventoryClickEvent event) {

		if( event.getClick() != ClickType.RIGHT ) return;
		
		final Player player = (Player) event.getWhoClicked();
		final Inventory inventory = event.getInventory();
		final Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
		if( cuboid == null ) return;
			
		final Shop shop = shops.getShop(cuboid);
				
		if (shop == null) return;
							
		if ( inventory.getName().equals(shop.getGui().getName() ) ) {
					
			event.setCancelled(true);
			player.updateInventory();
					
			if ( economy.getAccount(player.getName()) == null) {
						
				final EconomyPlayer ep = EdgeConomyAPI.economyAPI().getEconomyPlayer(player.getName());
				final EdgeItemStack item = new EdgeItemStack(event.getCurrentItem());
						
				shop.sellItem(ep, item);
			} else {
						
				final BankAccount acc = EdgeConomyAPI.economyAPI().getAccount(player.getName());
				final EdgeItemStack item = new EdgeItemStack(event.getCurrentItem());
					
					shop.sellItem(acc, item);
			}
		}
	}
}
