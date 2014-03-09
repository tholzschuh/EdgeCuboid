package net.edgecraft.edgecuboid.commands;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CCancelCommand extends AbstractCommand {
	
	private final CuboidHandler cuboidHandler = CuboidHandler.getInstance();
	
	private static final CCancelCommand instance = new CCancelCommand();
	
	private CCancelCommand() { super(); }
	
	public static final CCancelCommand getInstance() {
		return instance;
	}
	
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
	public  void sendUsageImpl(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/ccancel");
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
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
