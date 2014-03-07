package net.edgecraft.edgecuboid.commands;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CCancelCommand extends AbstractCommand {
	
	private final CuboidHandler cuboidHandler = CuboidHandler.getInstance();
	
	@Override
	public Level getLevel() {
		return Level.valueOf(EdgeCuboid.getInstance().getConfig().getString("Command.ccancel"));
	}
	
	@Override
	public String[] getNames() {
		String[] names = { "ccancel" };
		return names;
	}
	
	@Override
	public boolean validArgsRange(String[] args) {
		return args.length == 1;
	}
	
	@Override
	public  void sendUsage(CommandSender sender) {
		if (sender instanceof Player) {
			
			User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
			
			if (u != null) {
				
				if (!Level.canUse(u, getLevel())) return;
				
				sender.sendMessage(EdgeCore.usageColor + "/ccancel");
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
		
		cuboidHandler.getCreatingPlayers().remove(player.getName());
		cuboidHandler.getSearchingPlayers().remove(player.getName());
		
		player.sendMessage(lang.getColoredMessage(userLang, "cuboid_creation_cancel"));
		
		return true;
	}
	
	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}
}
