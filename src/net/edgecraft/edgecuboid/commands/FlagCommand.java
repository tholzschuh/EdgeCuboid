package net.edgecraft.edgecuboid.commands;

import net.edgecraft.edgecore.EdgeCore;
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
	
	private final static CuboidHandler cuboidHandler = CuboidHandler.getInstance();
	
	private static final FlagCommand instance = new FlagCommand();
	
	private FlagCommand() { /* ... */ }
	
	public static final FlagCommand getInstance() {
		return instance;
	}
	
	// wtf?
	@Override
	public Level getLevel() {
		return Level.valueOf(EdgeCuboid.getInstance().getConfig().getString("Command.flag"));
	}
	
	@Override
	public String[] getNames() {
		return new String[]{ "flag" };
	}
	
	@Override
	public boolean validArgsRange(String[] args) {
		return ( args.length == 2 || args.length == 4 || args.length == 5 );
	}
	
	@Override
	public void sendUsageImpl(CommandSender sender) {
		sender.sendMessage(EdgeCore.usageColor + "/flag toggle <cuboid> <user> <flag>");
		sender.sendMessage(EdgeCore.usageColor + "/flag check <cuboid> <user>");
		sender.sendMessage(EdgeCore.usageColor + "/flag list");	
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) {
		
		final String userLang = user.getLanguage();
		
		if (args[1].equalsIgnoreCase("toggle")) {
			if (args.length != 5) {
				sendUsage(player);
				return true;
			}
			
			final Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
			final User flagUser = users.getUser(args[3]);
			
			if (cuboid == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
				return true;
			}
			
			if (flagUser == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
				return true;
			}
			
			final Flag flag = Flag.valueOf( args[4] );
			
			if ( Flag.hasFlag(cuboid, flag, flagUser.getName() ) ) {
				
				Flag.removeFlag(cuboid, flagUser.getName(), flag);
				player.sendMessage(lang.getColoredMessage(userLang, "flag_toggle_false").replace("[0]", flagUser.getName()).replace("[1]", flag.name()));
				
			} else {
				
				Flag.giveFlag(cuboid, flagUser.getName(), flag);
				player.sendMessage(lang.getColoredMessage(userLang, "flag_toggle_true").replace("[0]", flagUser.getName()).replace("[1]", flag.name()));
				
			}
			
			return true;
		}
		
		if ( args[1].equalsIgnoreCase( "check" ) ) {
			if (args.length != 4) {
				sendUsage(player);
				return true;
			}
			
			final Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
			final User flagUser = users.getUser(args[3]);
			
			if ( cuboid == null ) {
				player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
				return true;
			}
			
			if ( flagUser == null ) {
				player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
				return true;
			}
			
			final StringBuilder sb = new StringBuilder();
			
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
		
		if( args[1].equalsIgnoreCase( "list" ) ) {
			
			final StringBuilder sb = new StringBuilder();
			
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
}
