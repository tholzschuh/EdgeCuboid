package net.edgecraft.edgecuboid.other;

import java.io.File;
import java.util.Arrays;

import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.shop.ShopHandler;
import net.edgecraft.edgecuboid.world.WorldManager;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHandler {

	private EdgeCuboid plugin;
	private FileConfiguration config;
	
	protected static final ConfigHandler instance = new ConfigHandler();
	
	protected ConfigHandler() { /* ... */ }
	
	public static final ConfigHandler getInstance( EdgeCuboid plugin ) {
		instance.setPlugin( plugin );
		return instance;
	}
	
	/**
	 * Loads the config of the EdgeConomy-Instance
	 */
	public void loadConfig() {
		
		// Config itself
		setConfig(getPlugin().getConfig());
		
		final FileConfiguration config = getConfig();
		
		// Temporary Command Level / TODO Register normally @nicolas @cross
		config.addDefault("Command.ccancel", Level.ARCHITECT.name());
		config.addDefault("Command.cfind", Level.USER.name());
		config.addDefault("Command.cuboid", Level.ARCHITECT.name());
		config.addDefault("Command.flag", Level.ARCHITECT.name());
		config.addDefault("Command.event", Level.ARCHITECT.name());
		config.addDefault("Command.habitat", Level.ARCHITECT.name());
		config.addDefault("Command.home", Level.USER.name());
		config.addDefault("Command.upgrade", Level.USER.name());
		config.addDefault("Command.rail", Level.ARCHITECT.name());
		config.addDefault("Command.street", Level.ARCHITECT.name());
		config.addDefault("Command.shop", Level.USER.name());
		config.addDefault("Command.home", Level.USER.name());
		
		config.addDefault("World.BorderRadius", 1000);
		config.addDefault("World.AllowFrameRotation", false);
		config.addDefault("World.AllowGlobalBlockInteraction", true);
		config.addDefault("World.AllowIceMelting", false);
		config.addDefault("World.AllowFireSpread", false);
		config.addDefault("World.AllowChunkLoading", true);
		config.addDefault("World.AllowStructureGrowing", true);
		
		config.addDefault("Cuboid.CreationItem", Material.STICK.name());
		config.addDefault("Cuboid.FindItem", Material.BLAZE_ROD.name());
		
		final String[] defaultFrames = new String[] { Material.WOOD_STAIRS.name(), Material.COBBLESTONE_STAIRS.name(), 
													Material.BIRCH_WOOD_STAIRS.name(), Material.DARK_OAK_STAIRS.name(), 
													Material.JUNGLE_WOOD_STAIRS.name(), Material.SPRUCE_WOOD_STAIRS.name(), 
													Material.NETHER_BRICK_STAIRS.name(), Material.QUARTZ_STAIRS.name(), 
													Material.SANDSTONE_STAIRS.name(), Material.BRICK_STAIRS.name(), 
													Material.SMOOTH_STAIRS.name() };
		
		config.addDefault("Shop.FrameItems", Arrays.asList(defaultFrames));
		config.addDefault("Shop.LooseInvOnDeath", false);
		
		config.options().copyDefaults(true);
		getPlugin().saveConfig();
		
		if (!new File(getPlugin().getDataFolder() + "src" + File.pathSeparator + "config.yml").exists());
			getPlugin().saveDefaultConfig();
	}
	
	/**
	 * Updates all local settings using the configuration
	 * @param instance
	 */
	public final void update() {
		
		final WorldManager worlds = WorldManager.getInstance();
		
		worlds.setWorldBorder(getConfig().getInt("World.BorderRadius"));
		worlds.setFrameRotation(getConfig().getBoolean("World.AllowFrameRotation"));
		worlds.setGlobalBlockInteraction(getConfig().getBoolean("World.AllowGlobalBlockInteraction"));
		worlds.setIceMelting(getConfig().getBoolean("World.AllowIceMelting"));
		worlds.setFireSpread(getConfig().getBoolean("World.AllowFireSpread"));
		worlds.setChunkLoading(getConfig().getBoolean("World.AllowChunkLoading"));
		worlds.setStructureGrowing(getConfig().getBoolean("World.AllowStructureGrowing"));
		
		worlds.setCreationItem(Material.getMaterial(getConfig().getString("Cuboid.CreationItem")));
		worlds.setFindItem(Material.getMaterial(getConfig().getString("Cuboid.FindItem")));
		
		ShopHandler.setShopFrames(getConfig().getStringList("Shop.FrameItems"));
		ShopHandler.toggleInventoryLoosing(getConfig().getBoolean("Shop.LooseInvOnDeath"));
		
	}
	
	/**
	 * Returns the used EdgeConomy-Instance
	 * @return EdgeConomy
	 */
	private EdgeCuboid getPlugin() {
		return plugin;
	}
	
	/**
	 * Returns the used FileConfiguration
	 * @return FileConfiguration
	 */
	public FileConfiguration getConfig() {
		return config;
	}
	
	/**
	 * Sets the used EdgeConomy-Instance
	 * @param instance
	 */
	protected void setPlugin(EdgeCuboid instance) {
		if (instance != null)
			plugin = instance;
	}
	
	/**
	 * Sets the used FileConfiguration
	 * @param config
	 */
	protected void setConfig(FileConfiguration config) {
		if (config != null)
			this.config = config;
	}
}
