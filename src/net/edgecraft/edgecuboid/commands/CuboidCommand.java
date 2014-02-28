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
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;
import net.edgecraft.edgecuboid.world.WorldManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CuboidCommand extends AbstractCommand {
	
	private final CuboidHandler cuboidHandler = CuboidHandler.getInstance();
	
	@Override
	public Level getLevel() {
		return Level.valueOf(EdgeCuboid.getInstance().getConfig().getString("Command.cuboid"));
	}
	
	@Override
	public String[] getNames() {
		String[] names = { "cuboid", "c" };
		return names;
	}
	
	@Override
	public boolean validArgsRange(String[] args) {
		return args.length > 1;
	}
	
	@Override
	public void sendUsage(CommandSender sender) {
		if (sender instanceof Player) {
			
			User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
			
			if (u != null) {
				
				if (!Level.canUse(u, getLevel())) return;
				
				sender.sendMessage(EdgeCore.usageColor + "/cuboid create <name> <type>");
				sender.sendMessage(EdgeCore.usageColor + "/cuboid recreate <name> [<type>]");
				sender.sendMessage(EdgeCore.usageColor + "/cuboid delete <name>");
				sender.sendMessage(EdgeCore.usageColor + "/cuboid participant");
				sender.sendMessage(EdgeCore.usageColor + "/cuboid commands");
				sender.sendMessage(EdgeCore.usageColor + "/cuboid edit");
				sender.sendMessage(EdgeCore.usageColor + "/cuboid switchowner <new owner>");
				sender.sendMessage(EdgeCore.usageColor + "/cuboid info <name>");
				sender.sendMessage(EdgeCore.usageColor + "/cuboid types");
				
			}
		}
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
		if (args[1].equalsIgnoreCase("create")) {
			if (cuboidHandler.isCreating(player.getName())) {
				player.sendMessage(lang.getColoredMessage(userLang, "cuboid_creation_inprogress"));
				return true;
			}
			
			if (cuboidHandler.existsCuboid(args[2])) {
				player.sendMessage(lang.getColoredMessage(userLang, "admin_cuboid_create_alreadyexists"));
				return true;
			}
			
			String name = args[2];
			CuboidType type = CuboidType.valueOf(args[3].toUpperCase());
			
			Cuboid c = new Cuboid();
			
			c.setName(name);
			c.switchOwner(user.getID());
			c.updateCuboidType(type);
			c.updateModifyLevel(user.getLevel());
			
			cuboidHandler.getCreatingPlayers().put(player.getName(), c);				
			player.sendMessage(lang.getColoredMessage(userLang, "admin_cuboid_create_start").replace("[0]", WorldManager.getInstance().getCreationItem().name().toLowerCase()));
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("recreate")) {
			player.sendMessage(lang.getColoredMessage(userLang, "pluginexception").replace("[0]", "EdgeCuboid"));
			return true;
		}
		
		if (args[1].equalsIgnoreCase("delete")) {
			
			Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
			
			if (cuboid == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
				return true;
			}
			
			if (!Flag.hasFlag(cuboid, Flag.EditCuboids, player.getName())) {
				player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
				return true;
			}
			
			cuboidHandler.deleteCuboid(cuboid.getID());
			player.sendMessage(lang.getColoredMessage(userLang, "admin_cuboid_delete_success").replace("[0]", args[2]));
			
			return true;			
		}
		
		if (args[1].equalsIgnoreCase("participant")) {
			
			if (args.length == 2) {
				player.sendMessage(EdgeCore.usageColor + "/cuboid participant add <cuboid> <user>");
				player.sendMessage(EdgeCore.usageColor + "/cuboid participant remove <cuboid> <user>");
				
				return true;
			}
			
			if (args[2].equalsIgnoreCase("add")) {
				
				Cuboid cuboid = CuboidHandler.getInstance().getCuboid(args[3]);
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[3]));
					return true;
				}
				
				User part = EdgeCoreAPI.userAPI().getUser(args[4]);
				
				if (part == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (cuboid.isParticipant(part.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_participant_add_alreadyparticipant"));
					return true;
				}
				
				if (!Flag.hasFlag(cuboid, Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				cuboid.getParticipants().add(user.getName());
				player.sendMessage(lang.getColoredMessage(userLang, "cuboid_participant_add_success").replace("[0]", part.getName()));
				
				return true;
			}
			
			if (args[2].equalsIgnoreCase("remove")) {
				
				Cuboid cuboid = CuboidHandler.getInstance().getCuboid(args[3]);
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[3]));
					return true;
				}
				
				User part = EdgeCoreAPI.userAPI().getUser(args[4]);
				
				if (part == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (!cuboid.isParticipant(part.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_participant_remove_noparticipant"));
					return true;
				}
				
				if (!Flag.hasFlag(cuboid, Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				cuboid.getParticipants().remove(user.getName());
				player.sendMessage(lang.getColoredMessage(userLang, "cuboid_participant_remove_success").replace("[0]", part.getName()));
				
				return true;
			}
		}
		
		if (args[1].equalsIgnoreCase("command")) {
			
			if (args.length == 2) {
				player.sendMessage(EdgeCore.usageColor + "/cuboid command add <cuboid> <command[,..]>");
				player.sendMessage(EdgeCore.usageColor + "/cuboid command remove <cuboid> <command[,..]>");
				player.sendMessage(EdgeCore.usageColor + "/cuboid command check <cuboid> <command>");
				
				return true;
			}
			
			if (args[2].equalsIgnoreCase("add")) {
				
				Cuboid cuboid = CuboidHandler.getInstance().getCuboid(args[3]);
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[3]));
					return true;
				}
				
				if (!Flag.hasFlag(cuboid, Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				String[] commands = args[4].split(",");
				
				for (String command : commands) {
					if (cuboid.isCommandAllowed(command)) continue;
					
					cuboid.allowCommand(command);
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "cuboid_command_allow_success"));
				
				return true;
			}
			
			if (args[2].equalsIgnoreCase("remove")) {
				
				Cuboid cuboid = CuboidHandler.getInstance().getCuboid(args[3]);
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[3]));
					return true;
				}
				
				if (!Flag.hasFlag(cuboid, Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				String[] commands = args[4].split(",");
				
				for (String command : commands) {
					if (!cuboid.isCommandAllowed(command)) continue;
					
					cuboid.disallowCommand(command);
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "cuboid_command_disallow_success"));
				
				return true;
			}
			
			if (args[2].equalsIgnoreCase("check")) {
				
				Cuboid cuboid = CuboidHandler.getInstance().getCuboid(args[3]);
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[3]));
					return true;
				}
				
				if (!Flag.hasFlag(cuboid, Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				if (cuboid.isCommandAllowed(args[4])) {
					
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_command_check_true").replace("[0]", "/" + args[4]));
					
				} else {
					
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_command_check_false").replace("[0]", "/" + args[4]));
					
				}
				
				return true;
			}
		}
		
		if (args[1].equalsIgnoreCase("edit")) {
			
			if (args.length == 2) {
				player.sendMessage(EdgeCore.usageColor + "/cuboid edit entermsg <cuboid> <enter message>");
				player.sendMessage(EdgeCore.usageColor + "/cuboid edit leavemsg <cuboid> <leave message>");
				
				return true;
			}
			
			if (args[2].equalsIgnoreCase("entermsg")) {
				
				StringBuilder sb = new StringBuilder();
				Cuboid cuboid = cuboidHandler.getCuboid(args[4]);
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[4]));
					return true;
				}
				
				if (!Flag.hasFlag(cuboid, Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				for (int i = 5; i < args.length; i++) {
					if (sb.length() > 0)
						sb.append(" ");
					
					sb.append(args[i]);
				}
				
				cuboid.updateEnterMessage(sb.toString());				
				player.sendMessage(lang.getColoredMessage(userLang, "cuboid_edit_entermsg_success").replace("[0]", sb.toString()));
				
				return true;
			}
			
			if (args[2].equalsIgnoreCase("leavemsg")) {
				
				StringBuilder sb = new StringBuilder();
				Cuboid cuboid = cuboidHandler.getCuboid(args[4]);
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[4]));
					return true;
				}
				
				if (!Flag.hasFlag(cuboid, Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				for (int i = 5; i < args.length; i++) {
					if (sb.length() > 0)
						sb.append(" ");
					
					sb.append(args[i]);
				}
				
				cuboid.updateEnterMessage(sb.toString());				
				player.sendMessage(lang.getColoredMessage(userLang, "cuboid_edit_leavermsg_success").replace("[0]", sb.toString()));
				
				return true;
			}
		}
		
		if (args[1].equalsIgnoreCase("switchowner")) {
			
			Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
			User newOwner = EdgeCoreAPI.userAPI().getUser(args[3]);
			
			if (cuboid == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
				return true;
			}
			
			if (newOwner == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
				return true;
			}
			
			if (cuboid.isOwner(newOwner)) {
				player.sendMessage(lang.getColoredMessage(userLang, "cuboid_switchowner_alreadyowner"));
				return true;
			}
			
			cuboid.switchOwner(newOwner.getID());
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_switchowner_success").replace("[0]", newOwner.getName()).replace("[1]", cuboid.getName()));
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("info")) {
			
			Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
			
			if (cuboid == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
				return true;
			}
			
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_info_title").replace("[0]", args[2]));
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_info_name").replace("[0]", args[2]));
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_info_type").replace("[0]", CuboidType.getType(cuboid.getCuboidType()).name()));
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_info_owner").replace("[0]", cuboid.getOwner().getName()));
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_info_center").replace("[0]", cuboid.getCenter().getBlockX() + "")
																					.replace("[1]", cuboid.getCenter().getBlockY() + "")
																					.replace("[2]", cuboid.getCenter().getBlockZ() + ""));
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_info_blocks").replace("[0]", cuboid.getArea() + "").replace("[1]", cuboid.getVolume() + ""));
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_info_participants").replace("[0]", cuboid.getParticipants().size() + ""));
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("types")) {
			
			StringBuilder sb = new StringBuilder();
			
			for (CuboidType type : CuboidType.getCuboidTypes()) {
				if (sb.length() > 0)
					sb.append(",");
				
				sb.append(ChatColor.GOLD + type.name());
			}
			
			player.sendMessage(lang.getColoredMessage(userLang, "cuboid_typelist").replace("[0]", sb.toString()));
			
			return true;
		}
		
		return true;
	}
	
	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}
}
