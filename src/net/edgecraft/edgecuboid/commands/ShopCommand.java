package net.edgecraft.edgecuboid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;

public class ShopCommand extends AbstractCommand {
	
	private static final ShopCommand instance = new ShopCommand();
	
	private ShopCommand() { super(); }
	
	public static final ShopCommand getInstance() {
		return instance;
	}
	
	@Override
	public Level getLevel() {
		return Level.USER;
	}

	@Override
	public String[] getNames() {
		return new String[] { "shop" };
	}

	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		return false;
	}

	@Override
	public void sendUsageImpl(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/shop buy <shop>");
		sender.sendMessage(EdgeCore.usageColor + "/shop rent <shop>");
		sender.sendMessage(EdgeCore.usageColor + "/shop additem [<amount>] <price>");
		sender.sendMessage(EdgeCore.usageColor + "/shop getitem [<amount>]");
		sender.sendMessage(EdgeCore.usageColor + "/shop itemprice <item> <price>");
		sender.sendMessage(EdgeCore.usageColor + "/shop near <distance>");
		sender.sendMessage(EdgeCore.usageColor + "/shop providing <item> <distance>");
		sender.sendMessage(EdgeCore.usageColor + "/shop overview");
		
		User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
		
		if (u == null || !Level.canUse(u, Level.ARCHITECT)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/shop create <name> <type> [<owner>]");
		sender.sendMessage(EdgeCore.usageColor + "/shop recreate <shop> [<type>]");
		sender.sendMessage(EdgeCore.usageColor + "/shop delete <shop>");
		sender.sendMessage(EdgeCore.usageColor + "/shop setowner <shop> <owner>");
		sender.sendMessage(EdgeCore.usageColor + "/shop setbuyable <shop> <boolean>");
		sender.sendMessage(EdgeCore.usageColor + "/shop setrental <shop> <rental>");
		sender.sendMessage(EdgeCore.usageColor + "/shop setrentable <shop> <boolean>");
		sender.sendMessage(EdgeCore.usageColor + "/shop info <shop>");
		sender.sendMessage(EdgeCore.usageColor + "/shop reload [<shop>]");
		sender.sendMessage(EdgeCore.usageColor + "/shop types");
	}

	@Override
	public boolean sysAccess(CommandSender arg0, String[] arg1) {
		return true;
	}

	@Override
	public boolean validArgsRange(String[] args) {
		return (args.length > 1 && args.length <= 5);
	}
}
