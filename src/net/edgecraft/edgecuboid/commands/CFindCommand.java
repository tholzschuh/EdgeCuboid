package net.edgecraft.edgecuboid.commands;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;
import net.edgecraft.edgecuboid.world.WorldManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CFindCommand extends AbstractCommand {
	
	private final CuboidHandler cuboidHandler = CuboidHandler.getInstance();
	
	@Override
	public Level getLevel() {
		return Level.valueOf(EdgeCuboid.getInstance().getConfig().getString("Command.cfind"));
	}
	
	@Override
	public String[] getNames() {
		String[] names = { "cfind" };
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
				
				sender.sendMessage(EdgeCore.usageColor + "/cfind");
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
		
		if (cuboidHandler.isSearching(player.getName())) {
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_find_stop"));
			cuboidHandler.getSearchingPlayers().remove(player.getName());
			
			return true;
		}
		
		player.sendMessage(lang.getColoredMessage(userLang, "cuboid_find_start").replace("[0]", WorldManager.getInstance().getFindItem().name()));
		cuboidHandler.getSearchingPlayers().add(player.getName());
		
		return true;
	}
	
	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}
}
