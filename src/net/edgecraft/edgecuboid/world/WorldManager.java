package net.edgecraft.edgecuboid.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WorldManager {
		
	private int worldBorder;
	
	private boolean allowFrameRotation;
	private boolean allowGlobalBlockInteraction;
	private boolean allowIceMelting;
	private boolean allowFireSpread;
	private boolean allowChunkLoading;
	private boolean allowStructureGrowing;
	
	private Material creationItem;
	private Material findItem;
	
	private static final WorldManager instance = new WorldManager();
	
	public static WorldManager getInstance() {
		return instance;
	}
	
	private WorldManager() { /* ... */ }
	
	public List<Chunk> getActiveChunks() {
		List<Chunk> chunks = new ArrayList<Chunk>();
		
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				
				chunks.add(entity.getLocation().getChunk());
				
			}
		}
		
		return chunks;
	}
	
	public int getWorldBorder() {
		return worldBorder;
	}
	
	public boolean isFrameRotationAllowed() {
		return allowFrameRotation;
	}
	
	public boolean isGlobalBlockInteractionAllowed() {
		return allowGlobalBlockInteraction;
	}
	
	public boolean isIceMeltAllowed() {
		return allowIceMelting;
	}
	
	public boolean isFireSpreadAllowed() {
		return allowFireSpread;
	}
	
	public boolean isChunkLoadingAllowed() {
		return allowChunkLoading;
	}
	
	public boolean isStructureGrowingAllowed() {
		return allowStructureGrowing;
	}
	
	public Material getCreationItem() {
		return creationItem;
	}
	
	public Material getFindItem() {
		return findItem;
	}
	
	public void setWorldBorder(int radius) {
		worldBorder = radius;
	}
	
	public void setFrameRotation(boolean allowed) {
		allowFrameRotation = allowed;
	}
	
	public void setGlobalBlockInteraction(boolean allowed) {
		allowGlobalBlockInteraction = allowed;
	}
	
	public void setIceMelting(boolean allowed) {
		allowIceMelting = allowed;
	}
	
	public void setFireSpread(boolean allowed) {
		allowFireSpread = allowed;
	}
	
	public void setChunkLoading(boolean allowed) {
		allowChunkLoading = allowed;
	}
	
	public void setStructureGrowing(boolean allowed) {
		allowStructureGrowing = allowed;
	}
	
	public void setCreationItem(Material material) {
		creationItem = material;
	}
	
	public void setFindItem(Material material) {
		findItem = material;
	}
}
