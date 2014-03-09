package net.edgecraft.edgecuboid.commands;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.CuboidEvent;
import net.edgecraft.edgecuboid.cuboid.CuboidHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventCommand extends AbstractCommand {
	
	private final CuboidHandler cuboidHandler = CuboidHandler.getInstance();
	
	private static final EventCommand instance = new EventCommand();
	
	private EventCommand() { super(); }
	
	public static final EventCommand getInstance() {
		return instance;
	}
	
	@Override
	public Level getLevel() {
		return Level.valueOf(EdgeCuboid.getInstance().getConfig().getString("Command.event"));
	}
	
	@Override
	public String[] getNames() {
		String[] names = { "event" };
		return names;
	}
	
	@Override
	public boolean validArgsRange(String[] args) {
		return (args.length > 1 && args.length < 5);
	}
	
	@Override
	public void sendUsageImpl(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/event toggle <cuboid> <event>");
		sender.sendMessage(EdgeCore.usageColor + "/event status <cuboid> [<event>]");
		sender.sendMessage(EdgeCore.usageColor + "/event list");	
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
		if (args[1].equalsIgnoreCase("toggle")) {
			if (args.length != 4) {
				sendUsage(player);
				return true;
			}
			
			Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
			
			if (cuboid == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
				return true;
			}
			
			CuboidEvent event = CuboidEvent.valueOf(args[3]);
			
			if (CuboidEvent.hasEvent(cuboid, event)) {
				
				CuboidEvent.disableEvent(cuboid, event);
				player.sendMessage(lang.getColoredMessage(userLang, "event_toggle_false").replace("[0]", event.name()));
				
			} else {
				
				CuboidEvent.enableEvent(cuboid, event);
				player.sendMessage(lang.getColoredMessage(userLang, "event_toggle_true").replace("[0]", event.name()));
				
			}
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("status")) {
			
			Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
			
			if (cuboid == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
				return true;
			}
			
			if (args.length == 3) {
				
				StringBuilder sb = new StringBuilder();
				
				for (CuboidEvent event : CuboidEvent.getCuboidEvents()) {
					if (sb.length() > 0)
						sb.append(ChatColor.WHITE + ", ");
					
					sb.append(CuboidEvent.hasEvent(cuboid, event) ? ChatColor.GREEN : ChatColor.RED);
					sb.append(event.name());
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "event_status_all").replace("[0]", cuboid.getName()).replace("[1]", sb.toString()));
				
				return true;
			}
			
			if (args.length == 4) {
				
				CuboidEvent event = CuboidEvent.valueOf(args[3]);
				String msg = CuboidEvent.hasEvent(cuboid, event) 
								? lang.getColoredMessage(userLang, "event_status_event_true").replace("[0]", args[3]) 
								: lang.getColoredMessage(userLang, "event_status_event_false").replace("[0]", args[3]);
				
				player.sendMessage(msg);
				
				return true;
			}
		}
		
		if (args[1].equalsIgnoreCase("list")) {
			
			StringBuilder sb = new StringBuilder();
			
			for (CuboidEvent event : CuboidEvent.getCuboidEvents()) {
				if (sb.length() > 0)
					sb.append(ChatColor.GOLD + ", ");
				
				sb.append(ChatColor.GOLD + event.name());
			}
			
			player.sendMessage(lang.getColoredMessage(userLang, "event_list").replace("[0]", sb.toString()));
			
			return true;
		}
		
		return true;
	}
	
	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}
}
