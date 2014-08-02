package com.sinius15.flyer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerState {

	int airTimeLeft;
	int coolDownLeft;

	boolean isFlying = false;

	Rank rank;

	UUID uuid;

	public PlayerState(Player player) {
		this.uuid = player.getUniqueId();
		updateRank();

		this.airTimeLeft = this.rank.flyTime;
		this.coolDownLeft = 0;
	}

	public void update() {
		updateRank();
		Player player = Bukkit.getPlayer(uuid);
		if (!player.isOnline())
			return;
		if (isFlying) {
			// flying
			if (airTimeLeft == -1)
				return;
			airTimeLeft--;
			if (airTimeLeft == 0) {
				player.setAllowFlight(false);
				isFlying = false;
				coolDownLeft = rank.cooldown;
				player.sendMessage(ChatColor.YELLOW
						+ "[fly] Your flight time is 0 seconds. Now entering cooldown of "
						+ coolDownLeft + " seconds.");
			}
			if (airTimeLeft == 5) {
				player.sendMessage(ChatColor.YELLOW
						+ "[fly] In 5 seconds your flight-timer is empty.");
			}
		} else {
			// not flying
			if (coolDownLeft > 0) {
				coolDownLeft--;
				if (coolDownLeft == 0) {
					player.sendMessage(ChatColor.YELLOW
							+ "[fly] Your cooldown is now done and you can fly again!");
					airTimeLeft = rank.flyTime;
				}
			}
		}
	}

	public void updateRank() {
		this.rank = getPlayerRank(Bukkit.getPlayer(uuid));

		if (this.rank == null)
			throw new NullPointerException(
					"You need to have a 'default' rank for people with no permission and a 'op' rank for opperators!!");
		if(this.rank.cooldown == -1)
			coolDownLeft = -1;
		if(this.rank.flyTime == -1)
			airTimeLeft = -1;
	}

	public void startFlying() {
		if (isFlying)
			return;
		Player player = Bukkit.getPlayer(uuid);
		if (airTimeLeft > 0 || airTimeLeft == -1) {
			isFlying = true;
			player.setAllowFlight(true);
			player.sendMessage(ChatColor.YELLOW + "[fly] You can now fly for "
					+ airTimeLeft + " seconds.");
		}
		if (airTimeLeft == 0) {
			player.sendMessage(ChatColor.YELLOW
					+ "[fly] You are still in cooldown for " + coolDownLeft
					+ " seconds.");
		}
	}

	public void stopFlying() {
		if (!isFlying)
			return;
		Player player = Bukkit.getPlayer(uuid);
		isFlying = false;
		player.setAllowFlight(false);
		player.sendMessage(ChatColor.YELLOW
				+ "[fly] You stopped flying, but you have still " + airTimeLeft
				+ " seconds left.");
	}

	public void check() {
		Player player = Bukkit.getPlayer(uuid);
		if (isFlying)
			player.sendMessage(ChatColor.YELLOW + "[fly] Flying, "
					+ airTimeLeft + " seconds left.");
		else if (coolDownLeft > 0)
			player.sendMessage(ChatColor.YELLOW + "[fly] In cooldown, "
					+ coolDownLeft + " seconds left.");
		else
			player.sendMessage(ChatColor.YELLOW + "[fly] Ready to fly, "
					+ airTimeLeft + " seconds left.");

	}

	private static Rank getPlayerRank(Player p) {
		if (p.isOp())
			return FlyTimer.ranks.get("op");
		for (String name : FlyTimer.ranks.keySet()) {
			if (p.hasPermission("flight." + name))
				return FlyTimer.ranks.get(name);
		}

		return FlyTimer.ranks.get("default");
	}

}
