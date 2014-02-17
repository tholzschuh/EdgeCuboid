package net.edgecraft.edgecuboid.other;

import java.io.File;

import net.edgecraft.edgecuboid.EdgeCuboid;
import net.edgecraft.edgecuboid.world.WorldManager;

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
		
		getConfig().addDefault("World.BorderRadius", 1000);
		getConfig().addDefault("World.AllowIceMelting", false);
		getConfig().addDefault("World.AllowFireSpread", false);
		getConfig().addDefault("World.AllowChunkLoading", true);
		getConfig().addDefault("World.AllowStructureGrowing", true);
		
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
		WorldManager.getInstance().setIceMelting(getConfig().getBoolean("World.AllowIceMelting"));
		WorldManager.getInstance().setFireSpread(getConfig().getBoolean("World.AllowFireSpread"));
		WorldManager.getInstance().setChunkLoading(getConfig().getBoolean("World.AllowChunkLoading"));
		WorldManager.getInstance().setStructureGrowing(getConfig().getBoolean("World.AllowStructureGrowing"));
		
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
