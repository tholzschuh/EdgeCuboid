package net.edgecraft.edgecuboid.other;

import net.edgecraft.edgecore.command.CommandHandler;
import net.edgecraft.edgecuboid.commands.CCancelCommand;
import net.edgecraft.edgecuboid.commands.CFindCommand;
import net.edgecraft.edgecuboid.commands.CuboidCommand;
import net.edgecraft.edgecuboid.commands.EventCommand;
import net.edgecraft.edgecuboid.commands.FlagCommand;
import net.edgecraft.edgecuboid.commands.HabitatCommand;
import net.edgecraft.edgecuboid.commands.HomeCommand;
import net.edgecraft.edgecuboid.commands.ShopCommand;

public class CuboidCommands extends CommandHandler {
	
	private static final CuboidCommands instance = new CuboidCommands();
	
	private CuboidCommands() {
		
		super.registerCommand( CCancelCommand.getInstance() );
		super.registerCommand( CFindCommand.getInstance() );
		super.registerCommand( CuboidCommand.getInstance() );
		super.registerCommand( EventCommand.getInstance() );
		super.registerCommand( FlagCommand.getInstance() );
		super.registerCommand( HabitatCommand.getInstance() );
		super.registerCommand( HomeCommand.getInstance() );
		super.registerCommand( ShopCommand.getInstance() );
	}
	
	public static final CuboidCommands getInstance() {
		return instance;
	}
}
