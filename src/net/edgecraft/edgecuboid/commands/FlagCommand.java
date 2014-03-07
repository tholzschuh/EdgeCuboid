package net.edgecraft.edgecuboid.commands;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;
import net.edgecraft.edgecuboid.cuboid.Flag;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlagCommand extends AbstractCommand {
	
	private final CuboidHandler cuboidHandler = CuboidHandler.getInstance();
	
	@Override
	public Level getLevel() {
		return Level.valueOf(EdgeCuboid.getInstance().getConfig().getString("Command.flag"));
	}
	
	@Override
	public String[] getNames() {
		String[] names = { "flag" };
		return names;
	}
	
	@Override
	public boolean validArgsRange(String[] args) {
		return (args.length > 1 && args.length < 6);
	}
	
	@Override
	public void sendUsage(CommandSender sender) {
		if (sender instanceof Player) {
			
			User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
			
			if (u != null) {
				
				if (!Level.canUse(u, getLevel())) return;
				
				sender.sendMessage(EdgeCore.usageColor + "/flag toggle <cuboid> <user> <flag>");
				sender.sendMessage(EdgeCore.usageColor + "/flag check <cuboid> <user>");
				sender.sendMessage(EdgeCore.usageColor + "/flag list");
				
			}
		}
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
		if (!Level.canUse(user, getLevel())) {
			player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
			return true;
		}
		
		if (args[1].equalsIgnoreCase("toggle")) {
			if (args.length != 5) {
				sendUsage(player);
				return true;
			}
			
			Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
			User flagUser = EdgeCoreAPI.userAPI().getUser(args[3]);
			
			if (cuboid == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
				return true;
			}
			
			if (flagUser == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
				return true;
			}
			
			Flag flag = Flag.valueOf(args[4]);
			
			if (Flag.hasFlag(cuboid, flag, flagUser.getName())) {
				
				Flag.removeFlag(cuboid, flagUser.getName(), flag);
				player.sendMessage(lang.getColoredMessage(userLang, "flag_toggle_false").replace("[0]", flagUser.getName()).replace("[1]", flag.name()));
				
			} else {
				
				Flag.giveFlag(cuboid, flagUser.getName(), flag);
				player.sendMessage(lang.getColoredMessage(userLang, "flag_toggle_true").replace("[0]", flagUser.getName()).replace("[1]", flag.name()));
				
			}
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("check")) {
			if (args.length != 4) {
				sendUsage(player);
				return true;
			}
			
			Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
			User flagUser = EdgeCoreAPI.userAPI().getUser(args[3]);
			
			if (cuboid == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
				return true;
			}
			
			if (flagUser == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
				return true;
			}
			
			StringBuilder sb = new StringBuilder();
			
			for (Flag flag : Flag.getFlags()) {
				if (Flag.hasFlag(cuboid, flag, flagUser.getName())) {
					
					if (sb.length() > 0)
						sb.append(ChatColor.GOLD + ", ");
					
					sb.append(ChatColor.GOLD + flag.name());
				}
			}
			
			player.sendMessage(lang.getColoredMessage(userLang, "flag_check").replace("[0]", flagUser.getName()).replace("[1]", sb.toString()));
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("list")) {
			
			StringBuilder sb = new StringBuilder();
			
			for (Flag flag : Flag.getFlags()) {
				if (sb.length() > 0)
					sb.append(ChatColor.GOLD + ", ");
				
				sb.append(ChatColor.GOLD + flag.name());
			}
			
			player.sendMessage(lang.getColoredMessage(userLang, "flag_list").replace("[0]", sb.toString()));
			
			return true;
		}
		
		return true;
	}
	
	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}
}
