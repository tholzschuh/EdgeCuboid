package net.edgecraft.edgecuboid.commands;

import net.edgecraft.edgeconomy.EdgeConomyAPI;
import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.EdgeCuboidAPI;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;
import net.edgecraft.edgecuboid.cuboid.Habitat;
import net.edgecraft.edgecuboid.cuboid.Upgrade;
import net.edgecraft.edgecuboid.cuboid.Upgrade.UpgradeType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand extends AbstractCommand {
	
	private final static CuboidHandler cuboidHandler = EdgeCuboidAPI.cuboidAPI();
	
	private static final HomeCommand instance = new HomeCommand();
	
	private HomeCommand() { /* ... */ }
	
	public static final HomeCommand getInstance() {
		return instance;
	}
	
	// wtf?
	@Override
	public Level getLevel() {
		return Level.valueOf(EdgeCuboid.getInstance().getConfig().getString("Command.home"));
	}

	@Override
	public String[] getNames() {
		return new String[]{ "home" };
	}

	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		final String userLang = user.getLanguage();
		
		try {
			
			if (args[1].equalsIgnoreCase("buy")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				if (!cuboidHandler.existsHabitat(args[2])) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[2]));
					return true;
				}
				
				final Habitat habitat = cuboidHandler.getHabitat(args[2]);
				
				if (!habitat.isBuyable()) {
					player.sendMessage(lang.getColoredMessage(userLang, "home_buy_notbuyable"));
					return true;
				}
				
				if (habitat.isOwner(player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "home_buy_ownhome"));
					return true;
				}
				
				if (habitat.isTenant(player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "home_buy_alreadytenant"));
					return true;
				}
				
				final BankAccount acc = EdgeConomyAPI.economyAPI().getAccount(player.getName());
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				if (acc.getBalance() < habitat.getWorth()) {
					player.sendMessage(lang.getColoredMessage(userLang, "notenoughmoney"));
					return true;
				}
				
				habitat.switchOwner(player.getName());
				habitat.setBuyable(false);
				habitat.setRentable(false);
				
				acc.updateBalance(acc.getBalance() - habitat.getWorth());
				
				player.sendMessage(lang.getColoredMessage(userLang, "home_buy_success").replace("[0]", args[2]).replace("[1]", habitat.getWorth() + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("sell")) {
				if (args.length == 3) {
										
					final Habitat habitat = cuboidHandler.getHabitatByOwner(player.getName());
					
					if (habitat == null) {
						player.sendMessage(lang.getColoredMessage(userLang, "home_sell_nohome"));
						return true;
					}
										
					if (habitat.isBuyable()) {
						player.sendMessage(lang.getColoredMessage(userLang, "home_sell_alreadyforsale"));
						return true;
					}
					
					final double price = Double.parseDouble(args[2]);
										
					if (price <= 0) {
						player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
						return true;
					}
					
					habitat.setBuyable(true);
					habitat.updateWorth(price);
					
					player.sendMessage(lang.getColoredMessage(userLang, "home_sell_success").replace("[0]", habitat.getCuboid().getName()).replace("[1]", price + ""));
					
					return true;
				}
				
				if (args.length == 4) {
					if (!Level.canUse(user, Level.ARCHITECT)) {
						player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
						return true;
					}
					
					if (!cuboidHandler.existsHabitat(args[3])) {
						player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[3]));
						return true;
					}
					
					final Habitat habitat = cuboidHandler.getHabitat(args[3]);
					
					if  (!habitat.isOwner(player.getName())) {
						player.sendMessage(lang.getColoredMessage(userLang, "admin_home_notowner"));
					}
					
					if (habitat.isBuyable()) {
						player.sendMessage(lang.getColoredMessage(userLang, "home_sell_alreadyforsale"));
						return true;
					}
					
					final double price = Double.parseDouble(args[2]);
										
					if (price <= 0) {
						player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
						return true;
					}
					
					if (Bukkit.getPlayerExact(habitat.getOwner()) != null) {
						Bukkit.getPlayerExact(habitat.getOwner()).sendMessage(lang.getColoredMessage(userLang, "admin_home_sell_ownerwarning").replace("[0]", player.getName()).replace("[1]", args[3]));
					}
					
					habitat.setBuyable(true);
					habitat.updateWorth(price);
					
					player.sendMessage(lang.getColoredMessage(userLang, lang.getColoredMessage(userLang, "admin_home_sell_success").replace("[0]", args[3]).replace("[1]", habitat.getOwner())));
					
					return true;
				}
			}
			
			if (args[1].equalsIgnoreCase("lease")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				if (!cuboidHandler.existsHabitat(args[2])) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[2]));
					return true;
				}
				
				final Habitat habitat = cuboidHandler.getHabitat(args[2]);
				final BankAccount acc = EdgeConomyAPI.economyAPI().getAccount(player.getName());
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				if (!habitat.isRentable()) {
					player.sendMessage(lang.getColoredMessage(userLang, "home_lease_notleasable"));
					return true;
				}
				
				if (habitat.getRental() > acc.getBalance()) {
					player.sendMessage(lang.getColoredMessage(userLang, "notenoughmoney"));
					return true;
				}
				
				if (Bukkit.getPlayerExact(habitat.getOwner()) != null) {
					Bukkit.getPlayerExact(habitat.getOwner()).sendMessage(lang.getColoredMessage(userLang, "home_lease_ownerinfo").replace("[0]", args[2]).replace("[1]", player.getName()));
				}
				
				habitat.setRentable(false);
				habitat.switchTenant(player.getName());
				
				player.sendMessage(lang.getColoredMessage(userLang, "home_lease_success").replace("[0]", args[2]).replace("[1]", habitat.getRental() + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("rent")) {
				if (args.length == 3) {
										
					final Habitat habitat = cuboidHandler.getHabitatByOwner(player.getName());
					
					if (habitat == null) {
						player.sendMessage(lang.getColoredMessage(userLang, "home_nohome"));
						return true;
					}
										
					if (habitat.isRentable()) {
						player.sendMessage(lang.getColoredMessage(userLang, "home_rent_alreadyforrent"));
						return true;
					}
					
					final double price = Double.parseDouble(args[2]);
										
					if (price <= 0) {
						player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
						return true;
					}
					
					habitat.setRentable(true);
					habitat.updateRental(price);
					
					player.sendMessage(lang.getColoredMessage(userLang, "home_rent_success").replace("[0]", habitat.getCuboid().getName()).replace("[1]", price + ""));
					
					return true;
				}
				
				if (!Level.canUse(user, Level.ARCHITECT)) {
					player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
					return true;
				}
				
				if (args.length == 4) {
					
					if (!cuboidHandler.existsHabitat(args[3])) {
						player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[3]));
						return true;
					}
					
					final Habitat habitat = cuboidHandler.getHabitat(args[3]);
					
					if  (!habitat.isOwner(player.getName())) {
						player.sendMessage(lang.getColoredMessage(userLang, "admin_home_notowner"));
					}
					
					if (habitat.isRentable()) {
						player.sendMessage(lang.getColoredMessage(userLang, "home_rent_alreadyforrent"));
						return true;
					}
					
					final double price = Double.parseDouble(args[2]);
										
					if (price <= 0) {
						player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
						return true;
					}
					
					if (Bukkit.getPlayerExact(habitat.getOwner()) != null) {
						Bukkit.getPlayerExact(habitat.getOwner()).sendMessage(lang.getColoredMessage(userLang, "admin_home_rentownerwarning").replace("[0]", player.getName()).replace("[1]", args[3]));
					}
					
					habitat.setRentable(true);
					habitat.updateRental(price);
					
					player.sendMessage(lang.getColoredMessage(userLang, lang.getColoredMessage(userLang, "admin_home_rent_success").replace("[0]", args[3]).replace("[1]", price + "")));
					
					return true;
				}
			}
			
			if (!Level.canUse(user, Level.ARCHITECT)) {
				player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
				return true;
			}
			
			if (args[1].equalsIgnoreCase("upgrade")) {
				if (!Level.canUse(user, Level.ARCHITECT)) {
					player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
					return true;
				}
				
				if (args.length == 2) {
					player.sendMessage(EdgeCore.usageColor + "/home upgrade unlock <habitat> <upgrade>");
					player.sendMessage(EdgeCore.usageColor + "/home upgrade remove <habitat> <upgrade>");
					player.sendMessage(EdgeCore.usageColor + "/home upgrade list");
					
					return true;
				}
				
				if (args[2].equalsIgnoreCase("unlock")) {
					if (args.length != 5) {
						sendUsage(player);
						return true;
					}
										
					final Habitat habitat = cuboidHandler.getHabitat(args[3]);
					
					if (habitat == null) {
						player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[3]));
						return true;
					}
										
					final Upgrade upgrade = new Upgrade(UpgradeType.valueOf(args[4]));
					
					if (habitat.getUpgrades().containsKey(upgrade) && !upgrade.multipleUsage()) {
						player.sendMessage(lang.getColoredMessage(userLang, "admin_upgrade_unlock_notmultiple"));
						return true;
					}
					
					habitat.getUpgrades().put(upgrade, habitat.getUpgrades().get(upgrade) + 1);
					player.sendMessage(lang.getColoredMessage(userLang, "admin_upgrade_unlock_success").replace("[0]", upgrade.getTypeName())
																									.replace("[1]", habitat.getCuboid().getName())
																									.replace("[2]", habitat.getUpgrades().get(upgrade) + ""));
					
					return true;
				}
				
				if (args[2].equalsIgnoreCase("remove")) {
					if (args.length != 5) {
						sendUsage(player);
						return true;
					}
					
					final Habitat habitat = cuboidHandler.getHabitat(args[3]);
					
					if (habitat == null) {
						player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[3]));
						return true;
					}
										
					final Upgrade upgrade = new Upgrade(UpgradeType.valueOf(args[4]));
					
					if (!habitat.getUpgrades().containsKey(upgrade)) {
						player.sendMessage(lang.getColoredMessage(userLang, "admin_upgrade_remove_notunlocked"));
						return true;
					}
					
					if (habitat.getUpgrades().get(upgrade) == 0) {
						
						habitat.getUpgrades().remove(upgrade);
						player.sendMessage(lang.getColoredMessage(userLang, "admin_upgrade_unlock_success").replace("[0]", upgrade.getTypeName())
																										.replace("[1]", habitat.getCuboid().getName())
																										.replace("[2]", habitat.getUpgrades().get(upgrade) + ""));
						
						return true;
					}
					
					habitat.getUpgrades().put(upgrade, habitat.getUpgrades().get(upgrade) - 1);
					player.sendMessage(lang.getColoredMessage(userLang, "admin_upgrade_unlock_success").replace("[0]", upgrade.getTypeName())
																									.replace("[1]", habitat.getCuboid().getName())
																									.replace("[2]", habitat.getUpgrades().get(upgrade) + ""));
					
					return true;
				}
				
				if (args[2].equalsIgnoreCase("list")) {
					
					final StringBuilder sb = new StringBuilder();
					
					for (UpgradeType upgrade : Upgrade.UpgradeType.values()) {
						if (sb.length() > 0)
							sb.append(", ");
						
						sb.append(ChatColor.GOLD + upgrade.getName());
					}
					
					player.sendMessage(lang.getColoredMessage(userLang, "home_upgrade_list").replace("[0]", sb.toString()));
					
					return true;
				}
			}
			
		} catch(NumberFormatException e) {
			player.sendMessage(lang.getColoredMessage(userLang, "numberformatexception"));
		}
		
		return true;
	}

	@Override
	public void sendUsageImpl(CommandSender sender) {
		sender.sendMessage(EdgeCore.usageColor + "/home buy <habitat>");
		sender.sendMessage(EdgeCore.usageColor + "/home sell <price> [<habitat>]");
		sender.sendMessage(EdgeCore.usageColor + "/home lease <habitat>");
		sender.sendMessage(EdgeCore.usageColor + "/home rent <rental> [<habitat>]");
		sender.sendMessage(EdgeCore.usageColor + "/home info [<habitat>]");
		
		final User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
		
		if (u == null || !Level.canUse(u, Level.ARCHITECT)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/home upgrade");
	}

	@Override
	public boolean validArgsRange(String[] args) {
		return (args.length > 1 && args.length <= 5);
	}

}
