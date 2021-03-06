package net.edgecraft.edgecuboid.commands;

import net.edgecraft.edgecore.EdgeCore;
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
	
	private static final CFindCommand instance = new CFindCommand();
	
	private CFindCommand() { /* ... */ }
	
	public static final CFindCommand getInstance() {
		return instance;
	}
	
	// wtf?
	@Override
	public Level getLevel() {
		return Level.valueOf(EdgeCuboid.getInstance().getConfig().getString("Command.cfind"));
	}
	
	@Override
	public String[] getNames() {
		return new String[]{ "cfind" };
	}
	
	@Override
	public boolean validArgsRange(String[] args) {
		return ( args.length == 1 );
	}
	
	@Override
	public  void sendUsageImpl(CommandSender sender) {
		sender.sendMessage(EdgeCore.usageColor + "/cfind");
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		final String userLang = user.getLanguage();
		
		if (cuboidHandler.isSearching( player.getName() ) ) {
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_find_stop"));
			cuboidHandler.getSearchingPlayers().remove(player.getName());
			
			return true;
		}
		
		player.sendMessage(lang.getColoredMessage(userLang, "cuboid_find_start").replace("[0]", WorldManager.getInstance().getFindItem().name()));
		cuboidHandler.getSearchingPlayers().add(player.getName());
		
		return true;
	}
}
