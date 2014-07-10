package me.fromgate.sbc;

import java.util.ArrayList;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SBC extends JavaPlugin implements Listener{

	ArrayList<BlockCode> blockCodes = new ArrayList<BlockCode>();
	
	public class BlockCode {
		String code;
		String permGlobal;
		String permLocal;
		boolean sendToBlock;
		
		BlockCode (String code, String permGlobal, String permLocal, boolean sendToBlock){
			this.code = code;
			this.permGlobal = permGlobal;
			this.permLocal = permLocal;
			this.sendToBlock = sendToBlock;
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerJoin (PlayerJoinEvent event){
		for (int i = 0; i<blockCodes.size(); i++) 
			if (checkPermBC(event.getPlayer(), blockCodes.get(i))) 
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', blockCodes.get(i).code));
	} 

	@Override
	public void onEnable() {
		loadCfg();
		if (blockCodes.isEmpty()) {
			getLogger().info("Config file (config.yml) not found. Will use default config.");
			initCfg();
			saveCfg();
		}
		getLogger().info(Integer.toString(blockCodes.size())+" block-codes included");
		if (!getServer().getAllowFlight()) getLogger().info("Allow-flight entry is set to false in your server.properties file");
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void loadCfg(){
		Set<String> keys = getConfig().getKeys(true);
		for (String s : keys){
			String [] ln = s.split("\\.");
			if (ln.length==2) 
				blockCodes.add(new BlockCode (getConfig().getString(s+".code","unknown"),ln[0],ln[1],getConfig().getBoolean(s+".block")));
		}
	}

	public void initCfg(){
		// Schematica
		blockCodes.add(new BlockCode ("&0&2&0&0&e&f","schematica","printer", true));
		blockCodes.add(new BlockCode ("&0&2&1&0&e&f","schematica","save", true));
		blockCodes.add(new BlockCode ("&0&2&1&1&e&f","schematica","load", true));
		// Zombe 
		blockCodes.add(new BlockCode ("&f &f &2 &0 &4 &8 ","cheats","zmbzcheat", true));
		blockCodes.add(new BlockCode ("&f &f &4 &0 &9 &6 ","cheats","zmbnoclip", true));
		blockCodes.add(new BlockCode ("&f &f &1 &0 &2 &4 ","fly","zmbfly", true));
		// CJB
		blockCodes.add(new BlockCode ("&3 &9 &2 &0 &0 &2 ","cheats","cjbxray", true));
		blockCodes.add(new BlockCode ("&3 &9 &2 &0 &0 &1 ","fly","cjbfly", true));
		blockCodes.add(new BlockCode ("&3 &9 &2 &0 &0 &3 ","radar","cjbradar", true));		
		// Rei's Minimap
		blockCodes.add(new BlockCode ("&0&0&1&e&f", "cheats","reicave", false));
		blockCodes.add(new BlockCode ("&0&0&2&3&4&5&6&7&e&f","radar","reiradar", false));
		// Automap
		blockCodes.add(new BlockCode ("&0&0&1&f&e","cheats","automap-ore", true));
		blockCodes.add(new BlockCode ("&0&0&2&f&e","cheats","automap-cave", true));
		blockCodes.add(new BlockCode ("&0&0&3&4&5&6&7&8&f&e","radar","automap-radar", true));
		// Smart-moving
		blockCodes.add(new BlockCode ("&0&1&0&1&2&f&f","cheats","smart-climb", true));
		blockCodes.add(new BlockCode ("&0&1&3&4&f&f","cheats","smart-swim", true));
		blockCodes.add(new BlockCode ("&0&1&5&f&f","cheats","smart-crawl", true));
		blockCodes.add(new BlockCode ("&0&1&6&f&f","cheats","smart-slide", true));
		blockCodes.add(new BlockCode ("&0&1&7&f&f","fly","smart-fly", true));
		blockCodes.add(new BlockCode ("&0&1&8&9&a&b&f&f","cheats","smart-jump", true));
	}

	public void saveCfg() {
		for (int i = 0; i<blockCodes.size(); i++){
			BlockCode b = blockCodes.get(i);
			getConfig().set(b.permGlobal+"."+b.permLocal+".code", b.code);
			getConfig().set(b.permGlobal+"."+b.permLocal+".block", b.sendToBlock);
		}
		getConfig().options().header("SBC by fromgate, http://dev.bukkit.org/bukkit-plugins/sbc/\n"
				+ "You can add new block codes in this file:\n"
				+ "Field: <BlockCodeGroup>.<BlockCodeName>.code must contain a block code\n"
				+ "Field: <BlockCodeGroup>.<BlockCodeName>.block is set to \"true\" if this code\n"
				+ "required to disable mod and to \"false\" if this code required to allow mod");
		saveConfig();
	}

	public boolean checkPermBC (Player p, BlockCode b){
		boolean result = p.hasPermission("sbc."+b.permGlobal)||p.hasPermission("sbc."+b.permGlobal+"."+b.permLocal);
		return b.sendToBlock ? !result : result;
	}

}
