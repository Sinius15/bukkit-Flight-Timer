package com.sinius15.flyer;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @idee http://forums.bukkit.org/threads/plugin-to-set-flight-time-for-different-ranks.295971/
 * @author Sinius15
 *
 */
public class FlyTimer extends JavaPlugin implements CommandExecutor {

	public static HashMap<String, Rank> ranks = new HashMap<>();  //<name, Rank>
	public static HashMap<UUID, PlayerState> states = new HashMap<>();

	@Override
	public void onEnable() {
		super.onEnable();
		loadconfig();
		getCommand("fly").setExecutor(this);
		Bukkit.getScheduler().runTaskTimer(this, new FlyTimerUpdater(), 0, 20);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Only players can use this command!");
			return true;
		}
		Player p = (Player) sender;
		PlayerState state = getState(p);
		if (args.length == 1 && args[0].equals("check")) {
			state.check();
			return true;
		}
		if (args.length == 0) { // just fly
			if(state.isFlying)
				state.stopFlying();
			else
				state.startFlying();
			return true;
		}
		return false;
	}
	
	public void loadconfig(){
		saveDefaultConfig();
		for(String key : getConfig().getKeys(true)){
			String[] split = key.split("\\.");
			if(split.length == 2){
				ranks.put(split[1], new Rank(split[1], getConfig().getInt(key+".airTime"), getConfig().getInt(key+".cooldownTime")));
			}
		}
		
	}
	
	public static PlayerState getState(UUID player){
		return states.get(player);
	}
	public static PlayerState getState(Player player){
		return states.get(player.getUniqueId());
	}

}
