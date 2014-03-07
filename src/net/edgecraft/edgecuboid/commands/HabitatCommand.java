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
import net.edgecraft.edgecuboid.cuboid.Habitat;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;
import net.edgecraft.edgecuboid.cuboid.types.HabitatType;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HabitatCommand extends AbstractCommand {
	
	private final CuboidHandler cuboidHandler = CuboidHandler.getInstance();
	
	@Override
	public Level getLevel() {
		return Level.valueOf(EdgeCuboid.getInstance().getConfig().getString("Command.habitat"));
	}
	
	@Override
	public String[] getNames() {
		String[] names = { "habitat", "h" };
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
				
				sender.sendMessage(EdgeCore.usageColor + "/habitat create <cuboid> <type> <worth>");
				sender.sendMessage(EdgeCore.usageColor + "/habitat recreate <habitat> [<type>]");
				sender.sendMessage(EdgeCore.usageColor + "/habitat delete <habitat>");
				sender.sendMessage(EdgeCore.usageColor + "/habitat setowner <habitat> <owner>");
				sender.sendMessage(EdgeCore.usageColor + "/habitat settenant <habitat> <tenant>");
				sender.sendMessage(EdgeCore.usageColor + "/habitat setworth <habitat> <worth>");
				sender.sendMessage(EdgeCore.usageColor + "/habitat setrental <habitat> <rental>");
				sender.sendMessage(EdgeCore.usageColor + "/habitat info <habitat>");
				sender.sendMessage(EdgeCore.usageColor + "/habitat types");
				
			}
		}
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
		try {
			
			if (args[1].equalsIgnoreCase("create")) {
				if (args.length != 5) {
					sendUsage(player);
					return true;
				}
				
				if (cuboidHandler.existsHabitat(args[2])) {
					player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_create_alreadyexists"));
					return true;
				}
				
				Cuboid cuboid = cuboidHandler.getCuboid(args[2]);
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknowncuboid").replace("[0]", args[2]));
					return true;
				}
				
				if (cuboid.getCuboidType() != CuboidType.Habitat.getTypeID()) {
					player.sendMessage(lang.getColoredMessage(userLang, "invalidcuboidtype"));
					return true;
				}
				
				HabitatType type = HabitatType.valueOf(args[3]);
				double worth = Double.parseDouble(args[4]);
				
				if (worth <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				cuboidHandler.registerHabitat(cuboid, type, player.getName(), "", worth, false, 0.0D, false, null);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_create_success").replace("[0]", args[2]));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("recreate")) {
				player.sendMessage(lang.getColoredMessage(userLang, "pluginexception").replace("[0]", "EdgeCuboid"));
				return true;
			}
			
			if (args[1].equalsIgnoreCase("delete")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				Habitat h = cuboidHandler.getHabitat(args[2]);
				
				if (h == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[2]));
					return true;
				}
				
				if (!Flag.hasFlag(h.getCuboid(), Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				cuboidHandler.deleteHabitat(h.getCuboidID());
				player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_delete_success").replace("[0]", args[2]));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("setowner")) {
				if (args.length != 4) {
					sendUsage(player);
					return true;
				}
				
				Habitat habitat = cuboidHandler.getHabitat(args[2]);
				User newOwner = EdgeCoreAPI.userAPI().getUser(args[3]);
				
				if (habitat == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[2]));
					return true;
				}
				
				if (newOwner == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (habitat.isOwner(newOwner.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_setowner_alreadyowner"));
					return true;
				}
				
				habitat.switchOwner(newOwner.getName());
				player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_setowner_success").replace("[0]", newOwner.getName()).replace("[1]", habitat.getCuboid().getName()));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("settenant")) {
				if (args.length != 4) {
					sendUsage(player);
					return true;
				}
				
				Habitat habitat = cuboidHandler.getHabitat(args[2]);
				User newTenant = EdgeCoreAPI.userAPI().getUser(args[3]);
				
				if (habitat == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[2]));
					return true;
				}
				
				if (newTenant == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (habitat.isTenant(newTenant.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_settenant_alreadytenant"));
					return true;
				}
				
				habitat.switchTenant(newTenant.getName());
				player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_settenant_success").replace("[0]", newTenant.getName()).replace("[1]", habitat.getCuboid().getName()));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("setworth")) {
				if (args.length != 4) {
					sendUsage(player);
					return true;
				}
				
				Habitat habitat = cuboidHandler.getHabitat(args[2]);
				double worth = Double.parseDouble(args[3]);
				
				if (habitat == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[2]));
					return true;
				}
				
				if (worth <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				if (!Flag.hasFlag(habitat.getCuboid(), Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				habitat.updateWorth(worth);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_setworth_success").replace("[0]", habitat.getCuboid().getName()).replace("[1]", worth + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("setrental")) {
				if (args.length != 4) {
					sendUsage(player);
					return true;
				}
				
				Habitat habitat = cuboidHandler.getHabitat(args[2]);
				double rental = Double.parseDouble(args[3]);
				
				if (habitat == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[2]));
					return true;
				}
				
				if (rental <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				if (!Flag.hasFlag(habitat.getCuboid(), Flag.EditCuboids, player.getName())) {
					player.sendMessage(lang.getColoredMessage(userLang, "cuboid_nopermission_modify"));
					return true;
				}
				
				habitat.updateRental(rental);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_setrental_success").replace("[0]", habitat.getCuboid().getName()).replace("[1]", rental + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("info")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				Habitat habitat = cuboidHandler.getHabitat(args[2]);
				
				if (habitat == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownhabitat").replace("[0]", args[2]));
					return true;
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "habitat_info_title").replace("[0]", args[2]));
				player.sendMessage(lang.getColoredMessage(userLang, "habitat_info_name").replace("[0]", args[2]));
				player.sendMessage(lang.getColoredMessage(userLang, "habitat_info_type").replace("[0]", habitat.getType().name()));
				player.sendMessage(lang.getColoredMessage(userLang, "habitat_info_owner").replace("[0]", habitat.getOwner()));
				player.sendMessage(lang.getColoredMessage(userLang, "habitat_info_worth").replace("[0]", habitat.getWorth() + ""));
				player.sendMessage(lang.getColoredMessage(userLang, "habitat_info_tenant").replace("[0]", habitat.getTenant()));
				player.sendMessage(lang.getColoredMessage(userLang, "habitat_info_rental").replace("[0]", habitat.getRental() + ""));
				player.sendMessage(habitat.isRentable() ? lang.getColoredMessage(userLang, "habitat_info_rentable_false") : lang.getColoredMessage(userLang, "habitat_info_rentable_true"));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("types")) {
				
				StringBuilder sb = new StringBuilder();
				
				for (HabitatType type : HabitatType.getHabitatTypes()) {
					if (sb.length() > 0)
						sb.append(", ");
					
					sb.append(ChatColor.GOLD + type.name());
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "admin_habitat_typelist").replace("[0]", sb.toString()));
				
				return true;
			}
			
		} catch(NumberFormatException e) {
			player.sendMessage(lang.getColoredMessage(userLang, "numberformatexception"));
		}
		
		return true;
	}
	
	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}
}
