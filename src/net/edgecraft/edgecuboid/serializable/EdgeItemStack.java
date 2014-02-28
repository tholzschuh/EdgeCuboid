package net.edgecraft.edgecuboid.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

public class EdgeItemStack {
	
	private Material type;
	private int amount;
	private byte data;
	private short durability;
	private ItemMeta itemMeta;
	
	@SuppressWarnings("deprecation")
	public EdgeItemStack(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			
			this.type = Material.AIR;
			this.amount = 1;
			this.data = 0;
			this.durability = 1;
			this.itemMeta = Bukkit.getItemFactory().getItemMeta(Material.AIR);
			
		} else {
			
			this.type = itemStack.getType();
			this.amount = itemStack.getAmount();
			this.data = itemStack.getData().getData();
			this.durability = itemStack.getDurability();
			this.itemMeta = itemStack.getItemMeta();
			
		}
	}
	
	private EdgeItemStack() { /* Singleton */ }
	
	public Material getType() {
		return type;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public byte getData() {
		return data;
	}
	
	public short getDurability() {
		return durability;
	}
	
	public short getDamage() {
		return durability;
	}
	
	public ItemMeta getItemMeta() {
		return itemMeta;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack toBukkitItemStack() {
		ItemStack itemStack = new ItemStack(type, amount, durability, data);
		return itemStack;
	}
	
	public static EdgeItemStack toItemStack(byte[] byteArray) {
		try {
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> infoMap = (Map<String, Object>) objectInputStream.readObject();
			
			EdgeItemStack edgeItemStack = new EdgeItemStack();
			edgeItemStack.deserialize(infoMap);
			
			return edgeItemStack;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] toByteArray() {
		try {
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(serialize());
			
			return byteArrayOutputStream.toByteArray();
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Map<String, Object> serialize() {
		Map<String, Object> infoMap = new LinkedHashMap<String, Object>();
		infoMap.put("object-type", "EdgeItemStack");
		
		infoMap.put("type", type);
		infoMap.put("amount", amount);
		infoMap.put("data", data);
		infoMap.put("durability", durability);
		
		if (type == Material.AIR) return infoMap;
		
		infoMap.put("display-name", itemMeta.getDisplayName());
		infoMap.put("lore", itemMeta.getLore());
		
		// Enchantments
		Map<String, Integer> enchantmentList = new LinkedHashMap<String, Integer>();
		
		for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
			enchantmentList.put(enchantment.getName(), itemMeta.getEnchants().get(enchantment));
		}
		
		infoMap.put("enchantments", enchantmentList);
		
		// Meta Data
		if (itemMeta instanceof BookMeta) {
			BookMeta bookMeta = (BookMeta) itemMeta;
			
			infoMap.put("meta-type", "book");
			infoMap.put("book-author", bookMeta.getAuthor());
			infoMap.put("book-title", bookMeta.getTitle());
			infoMap.put("book-pages", bookMeta.getPages());
			
		} else if (itemMeta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
			
			infoMap.put("meta-type", "enchantmentstorage");
			
			Map<String, Integer> serializedEnchantment = new LinkedHashMap<String, Integer>();
			
			for (Enchantment enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()) {
				serializedEnchantment.put(enchantment.getName(), enchantmentStorageMeta.getStoredEnchants().get(enchantment));
			}
			
			infoMap.put("enchantmentstorage-stored", serializedEnchantment);
			
		} else if (itemMeta instanceof FireworkEffectMeta) {
			FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) this.itemMeta;
			
			infoMap.put("meta-type", "fireworkeffect");
			infoMap.put("fireworkeffect-haseffect", fireworkEffectMeta.hasEffect());
			
			List<Integer> serializedColors = new ArrayList<Integer>();			
			for (Color color : fireworkEffectMeta.getEffect().getColors()) serializedColors.add(color.asRGB());			
			infoMap.put("fireworkeffect-colors", serializedColors);
			
			List<Integer> serializedFadeColors = new ArrayList<Integer>();			
			for (Color color : fireworkEffectMeta.getEffect().getFadeColors()) serializedFadeColors.add(color.asRGB());			
			infoMap.put("fireworkeffect-fadecolors", serializedFadeColors);
			
			infoMap.put("fireworkeffect-type", fireworkEffectMeta.getEffect().getType().name());
			infoMap.put("fireworkeffect-flicker", fireworkEffectMeta.getEffect().hasFlicker());
			infoMap.put("fireworkeffect-trail", fireworkEffectMeta.getEffect().hasTrail());
			
		} else if(itemMeta instanceof FireworkMeta) {
			FireworkMeta fireworkMeta = (FireworkMeta) this.itemMeta;
			
			infoMap.put("meta-type", "firework");
			infoMap.put("firework-power", fireworkMeta.getPower());
			
			List<Map<String, Object>> fireworkEffects = new ArrayList<Map<String, Object>>();
			
			for (FireworkEffect fireworkEffect : fireworkMeta.getEffects()) {
				Map<String, Object> serializedEffect = new LinkedHashMap<String, Object>();
				
				List<Integer> serializedColors = new ArrayList<Integer>();
				for (Color color : fireworkEffect.getColors()) serializedColors.add(color.asRGB());
				serializedEffect.put("colors", serializedColors);
				
				List<Integer> serializedFadeColors = new ArrayList<Integer>();
				for (Color color : fireworkEffect.getFadeColors()) serializedFadeColors.add(color.asRGB());
				serializedEffect.put("fadecolors", serializedFadeColors);
				
				serializedEffect.put("type", fireworkEffect.getType().name());
				serializedEffect.put("flicker", fireworkEffect.hasFlicker());
				serializedEffect.put("trail", fireworkEffect.hasTrail());
				
				fireworkEffects.add(serializedEffect);
			}
			
			infoMap.put("firework-effects", fireworkEffects);
			
		} else if (this.itemMeta instanceof LeatherArmorMeta) {
			LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemMeta;
			
			infoMap.put("meta-type", "leather-armor");
			infoMap.put("leather-armor-color", leatherArmorMeta.getColor().serialize());
			
		} else if (this.itemMeta instanceof MapMeta) {
			MapMeta mapMeta = (MapMeta) this.itemMeta;
			
			infoMap.put("meta-type", "map");
			infoMap.put("map-scaling", mapMeta.isScaling());
			
		} else if (this.itemMeta instanceof PotionMeta) {
			PotionMeta potionMeta = (PotionMeta) this.itemMeta;
			
			infoMap.put("meta-type", "potion");
			
			List<Map<String, Object>> potionEffects = new ArrayList<Map<String, Object>>();
			
			for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
				Map<String, Object> serializedEffect = new LinkedHashMap<String, Object>();
				
				serializedEffect.put("amplifier", potionEffect.getAmplifier());
				serializedEffect.put("duration", potionEffect.getDuration());
				serializedEffect.put("ambient", potionEffect.isAmbient());
				
				potionEffects.add(serializedEffect);
			}
			
			infoMap.put("potion-effects", potionEffects);
			
		} else if (this.itemMeta instanceof SkullMeta) {
			SkullMeta skullMeta = (SkullMeta) this.itemMeta;
			
			infoMap.put("meta-type", "skull");
			infoMap.put("skull-owner", skullMeta.getOwner());
			
		} else {
			
			infoMap.put("meta-type", "none");
			
		}
		
		return infoMap;
	}
	
	@SuppressWarnings("unchecked")
	private void deserialize(Map<String, Object> infoMap) {
		if (!infoMap.containsKey("object-type") || !infoMap.get("object-type").equals("EdgeItemStack")) throw new java.util.UnknownFormatFlagsException("No EdgeItemStack!");
		
		type = (Material) infoMap.get("type");
		amount = (int) infoMap.get("amount");
		data = (byte) infoMap.get("data");
		durability = (short) infoMap.get("durability");
		
		itemMeta = Bukkit.getItemFactory().getItemMeta(Material.getMaterial(type.name()));
		
		if (type == Material.AIR) return;
		
		itemMeta.setDisplayName(infoMap.get("display-name") != null ? (String) infoMap.get("display-name") : null);
		itemMeta.setLore(infoMap.get("lore") != null ? (List<String>) infoMap.get("lore") : null);
		
		// Enchantments
		Map<String, Integer> enchantmentList = (Map<String, Integer>) infoMap.get("enchantments");
		for (String enchantmentName : enchantmentList.keySet()) {
			
			int enchantmentLevel = enchantmentList.get(enchantmentName);
			itemMeta.addEnchant(Enchantment.getByName(enchantmentName), enchantmentLevel, true);
			
		}
		
		// Item Meta
		String metaType = (String) infoMap.get("meta-type");
		if (metaType == null) return;

		if (metaType.equals("book")) {
			BookMeta bookMeta = (BookMeta) this.itemMeta;
			
			bookMeta.setAuthor((String) infoMap.get("book-author"));
			bookMeta.setTitle((String) infoMap.get("book-title"));
			bookMeta.setPages((List<String>) infoMap.get("book-pages"));
			
		} else if (metaType.equals("enchantmentstorage")) {
			EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) this.itemMeta;
			
			Map<String, Integer> serializedEnchantment = (Map<String, Integer>) infoMap.get("enchantmentstorage-stored");
			for (String enchantmentName : serializedEnchantment.keySet()) {
				
				Integer enchantmentLevel = serializedEnchantment.get(enchantmentName);
				enchantmentStorageMeta.addStoredEnchant(Enchantment.getByName(enchantmentName), enchantmentLevel, true);
				
			}
			
		} else if (metaType.equals("fireworkeffect")) {
			FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) this.itemMeta;

			Builder builder = FireworkEffect.builder();
			
			if ((boolean) infoMap.get("fireworkeffect-haseffect") == true) {
				
				List<Color> serializedColors = new ArrayList<Color>();				
				for (Integer colorRGB : (List<Integer>) infoMap.get("fireworkeffect-colors")) serializedColors.add(Color.fromRGB(colorRGB));
				builder.withColor(serializedColors);

				List<Color> serializedFadeColors = new ArrayList<Color>();
				for (Integer colorRGB : (List<Integer>) infoMap.get("fireworkeffect-fadecolors")) serializedFadeColors.add(Color.fromRGB(colorRGB));
				builder.withFade(serializedFadeColors);

				builder.with(FireworkEffect.Type.valueOf((String) infoMap.get("fireworkeffect-type")));
				builder.flicker((boolean) infoMap.get("fireworkeffect-flicker"));
				builder.trail((boolean) infoMap.get("fireworkeffect-trail"));
				
			}

			fireworkEffectMeta.setEffect(builder.build());
			
		} else if (metaType.equals("firework")) {
			FireworkMeta fireworkMeta = (FireworkMeta) this.itemMeta;

			fireworkMeta.setPower((int) infoMap.get("firework-power"));

			List<Map<String, Object>> fireworkEffects = (List<Map<String, Object>>) infoMap.get("firework-effects");
			for (Map<String, Object> serializedEffect : fireworkEffects) {
				
				Builder builder = FireworkEffect.builder();

				List<Color> serializedColors = new ArrayList<Color>();
				for (Integer colorRGB : (List<Integer>) serializedEffect.get("colors")) serializedColors.add(Color.fromRGB(colorRGB));
				builder.withColor(serializedColors);

				List<Color> serializedFadeColors = new ArrayList<Color>();
				for (Integer colorRGB : (List<Integer>) serializedEffect.get("fadecolors")) serializedFadeColors.add(Color.fromRGB(colorRGB));
				builder.withFade(serializedFadeColors);

				builder.with(FireworkEffect.Type.valueOf((String) serializedEffect.get("type")));
				builder.flicker((boolean) serializedEffect.get("flicker"));
				builder.trail((boolean) serializedEffect.get("trail"));

				fireworkMeta.addEffect(builder.build());
			}
			
		} else if (metaType.equals("leather-armor")) {
			LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemMeta;
			
			leatherArmorMeta.setColor(Color.deserialize((Map<String, Object>) infoMap.get("leather-armor-color")));
			
		} else if (metaType.equals("map")) {
			MapMeta mapMeta = (MapMeta) this.itemMeta;
			
			mapMeta.setScaling((boolean) infoMap.get("map-scaling"));
			
		} else if (metaType.equals("potion")) {
			PotionMeta potionMeta = (PotionMeta) this.itemMeta;
			
			List<Map<String, Object>> potionEffects = (List<Map<String, Object>>) infoMap.get("potion-effects");
			for (Map<String, Object> potionEffect : potionEffects) {
				potionMeta.addCustomEffect(new PotionEffect(potionEffect), true);
			}
			
		} else if (metaType.equals("skull")) {
			
			SkullMeta skullMeta = (SkullMeta) this.itemMeta;
			skullMeta.setOwner((String) infoMap.get("skull-owner"));
			
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		EdgeItemStack another = (EdgeItemStack) obj;
		
		if (type == another.type) {
			if (data == another.data) {
				if (amount == another.amount) {
					if (durability == another.durability) {
						if (itemMeta.equals(another.itemMeta)) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "EdgeItemStack {" + type.name().toLowerCase() + ", " + amount + ", " + data + ", " + durability + ", " + itemMeta + "}";
	}
}
