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
	
	public static final ConfigHandler getInstance(EdgeCuboid plugin) {
		instance.setPlugin(plugin);
		return instance;
	}
	
	/**
	 * Loads the config of the EdgeConomy-Instance
	 */
	public void loadConfig() {
		
		// Config itself
		setConfig(getPlugin().getConfig());
		
		// Temporary Command Level / TODO Register normally @nicolas @cross
		getConfig().addDefault("Command.ccancel", Level.ARCHITECT.name());
		getConfig().addDefault("Command.cfind", Level.USER.name());
		getConfig().addDefault("Command.cuboid", Level.ARCHITECT.name());
		getConfig().addDefault("Command.flag", Level.ARCHITECT.name());
		getConfig().addDefault("Command.event", Level.ARCHITECT.name());
		getConfig().addDefault("Command.habitat", Level.ARCHITECT.name());
		getConfig().addDefault("Command.home", Level.USER.name());
		getConfig().addDefault("Command.upgrade", Level.USER.name());
		getConfig().addDefault("Command.rail", Level.ARCHITECT.name());
		getConfig().addDefault("Command.street", Level.ARCHITECT.name());
		getConfig().addDefault("Command.shop", Level.USER.name());
		getConfig().addDefault("Command.home", Level.USER.name());
		
		getConfig().addDefault("World.BorderRadius", 1000);
		getConfig().addDefault("World.AllowFrameRotation", false);
		getConfig().addDefault("World.AllowGlobalBlockInteraction", true);
		getConfig().addDefault("World.AllowIceMelting", false);
		getConfig().addDefault("World.AllowFireSpread", false);
		getConfig().addDefault("World.AllowChunkLoading", true);
		getConfig().addDefault("World.AllowStructureGrowing", true);
		
		getConfig().addDefault("Cuboid.CreationItem", Material.STICK.name());
		getConfig().addDefault("Cuboid.FindItem", Material.BLAZE_ROD.name());
		
		String[] defaultFrames = new String[] { Material.WOOD_STAIRS.name(), Material.COBBLESTONE_STAIRS.name(), 
													Material.BIRCH_WOOD_STAIRS.name(), Material.DARK_OAK_STAIRS.name(), 
													Material.JUNGLE_WOOD_STAIRS.name(), Material.SPRUCE_WOOD_STAIRS.name(), 
													Material.NETHER_BRICK_STAIRS.name(), Material.QUARTZ_STAIRS.name(), 
													Material.SANDSTONE_STAIRS.name(), Material.BRICK_STAIRS.name(), 
													Material.SMOOTH_STAIRS.name() };
		
		getConfig().addDefault("Shop.FrameItems", Arrays.asList(defaultFrames));
		getConfig().addDefault("Shop.LooseInvOnDeath", false);
		
		getConfig().options().copyDefaults(true);
		getPlugin().saveConfig();
		
		if (!new File(getPlugin().getDataFolder() + "src" + File.pathSeparator + "config.yml").exists());
			getPlugin().saveDefaultConfig();
	}
	
	/**
	 * Updates all local settings using the configuration
	 * @param instance
	 */
	public final void update() {
		
		WorldManager.getInstance().setWorldBorder(getConfig().getInt("World.BorderRadius"));
		WorldManager.getInstance().setFrameRotation(getConfig().getBoolean("World.AllowFrameRotation"));
		WorldManager.getInstance().setGlobalBlockInteraction(getConfig().getBoolean("World.AllowGlobalBlockInteraction"));
		WorldManager.getInstance().setIceMelting(getConfig().getBoolean("World.AllowIceMelting"));
		WorldManager.getInstance().setFireSpread(getConfig().getBoolean("World.AllowFireSpread"));
		WorldManager.getInstance().setChunkLoading(getConfig().getBoolean("World.AllowChunkLoading"));
		WorldManager.getInstance().setStructureGrowing(getConfig().getBoolean("World.AllowStructureGrowing"));
		
		WorldManager.getInstance().setCreationItem(Material.getMaterial(getConfig().getString("Cuboid.CreationItem")));
		WorldManager.getInstance().setFindItem(Material.getMaterial(getConfig().getString("Cuboid.FindItem")));
		
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
