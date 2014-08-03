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
 * This is the main Plugin Class. From here, everything inside this plugin is
 * started/called.<br>
 * <br>
 * The idea for htis plugin is from {@link http
 * ://forums.bukkit.org/threads/plugin
 * -to-set-flight-time-for-different-ranks.295971/}
 * 
 * @author Sinius15
 * 
 */
public class FlyTimer extends JavaPlugin implements CommandExecutor {

	public static HashMap<String, Rank> ranks = new HashMap<>(); // <name, Rank>
	public static HashMap<UUID, PlayerState> states = new HashMap<>();

	@Override
	public void onEnable() {
		super.onEnable();
		loadconfig();
		getCommand("fly").setExecutor(this);
		Bukkit.getScheduler().runTaskTimer(this, new FlyTimerUpdater(), 0, 20);
	}

	/**
	 * Called when '/fly **' is issued.
	 */
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
			state.toggleFlying();
			return true;
		}
		return false;
	}

	/**
	 * Loads the config. If the config does not exist, the default config will
	 * be copeyd
	 */
	public void loadconfig() {
		saveDefaultConfig();
		for (String key : getConfig().getKeys(true)) {
			String[] split = key.split("\\.");
			if (split[0].equals("rank") && split.length == 2) {
				ranks.put(split[1],
						new Rank(split[1],
								getConfig().getInt(key + ".airTime"),
								getConfig().getInt(key + ".cooldownTime")));
			}
		}

	}

	/**
	 * gives you the right {@link PlayerState} with the right {@link Player}. If
	 * the player does not exist, null will be returned.
	 * 
	 * @param player
	 *            the player to search for.
	 */
	public static PlayerState getState(Player player) {
		return states.get(player.getUniqueId());
	}

}
