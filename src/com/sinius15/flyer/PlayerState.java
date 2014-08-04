package com.sinius15.flyer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Every player has a PlayerSate. In this PlayerState is saved what the player
 * is doing.<br>
 * <br>
 * First it saves the uuid of the player so if you have a {@link Player}, you
 * can find the corresponding {@link PlayerState}. After that you can call the
 * functions to interact with the flying behaviour of the player.<br>
 * <br>
 * 
 * There are no public variables becaues you do not need to edit these
 * variables. Everything can be accieaved by calling funcions on this class.
 * 
 * @author Sinius15
 * 
 */
public class PlayerState {

	private int airTimeLeft, coolDownLeft; // all in seconds.

	private boolean isFlying = false;

	private Rank rank;

	private final UUID uuid;

	/**
	 * Creates a new Player state with the corresponding {@link Player}. This
	 * does not check if the player already has a PlayerState!
	 * 
	 * @param player
	 *            the player to bind to.
	 */
	public PlayerState(Player player) {
		this.uuid = player.getUniqueId();
		this.rank = getPlayerRank(Bukkit.getPlayer(uuid));

		this.airTimeLeft = this.rank.flyTime;
		this.coolDownLeft = 0;
		isFlying = player.getAllowFlight();
	}

	/**
	 * This function should be called every second(inportant!) by the
	 * {@link FlyTimerUpdater}. It updates the airtime or the cooldown time,
	 * sends messages to the player and let people drop from the sky. If this
	 * function is called more than once a second, one second will be less than
	 * a second. (math bitch ;)
	 */
	public void update() {
		updateRank();
		Player player = getPlayer();
		if (!player.isOnline())
			return;
		if (isFlying) {
			// flying
			if (airTimeLeft == -1)
				return;
			airTimeLeft--;
			if (airTimeLeft == 0) {
				player.setFlying(false);
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

	/**
	 * I update the {@link #rank} of this {@link PlayerState}. This must be done
	 * occasionaly in case if someone gets opperator rigts or permissions is
	 * realoded.<br>
	 * <br>
	 * This is an update function, and can not be called to initialize the Rank
	 * for the first time!
	 */
	public void updateRank() {
		// this will throw a nullpointer exception if rank is null.
		String oldRank = this.rank.name;
		this.rank = getPlayerRank(Bukkit.getPlayer(uuid));

		if (this.rank == null)
			throw new NullPointerException(
					"You need to have a 'default' rank for people with no permission and a 'op' rank for opperators!!");
		if (!oldRank.equals(this.rank.name)) {
			airTimeLeft = rank.flyTime;
			coolDownLeft = rank.cooldown;
		}

	}

	/**
	 * this function starts the flying of the player. If the player is already
	 * flying, it will return inmediately. If in cooldown, it will show an error
	 * to the player.
	 */
	public void startFlying() {
		if (isFlying)
			return;
		Player player = getPlayer();
		if (airTimeLeft > 0 || airTimeLeft == -1) {
			isFlying = true;
			player.setAllowFlight(true);
			if (rank.flyTime != -1)
				player.sendMessage(ChatColor.YELLOW
						+ "[fly] You can now fly for " + airTimeLeft
						+ " seconds.");
		}
		if (airTimeLeft == 0) {
			player.sendMessage(ChatColor.YELLOW
					+ "[fly] You are still in cooldown for " + coolDownLeft
					+ " seconds.");
		}
	}

	/**
	 * Stops the player from flying. If there is still fly-time left, it will
	 * report that to the player.
	 */
	public void stopFlying() {
		if (!isFlying)
			return;
		Player player = getPlayer();
		isFlying = false;
		player.setAllowFlight(false);
		if (airTimeLeft > 0)
			player.sendMessage(ChatColor.YELLOW
					+ "[fly] You stopped flying, but you have still "
					+ airTimeLeft + " seconds left.");
	}

	/**
	 * Does a '/fly check' to the player. It sends a status message to the
	 * player with the current flying status.
	 */
	public void check() {
		Player player = getPlayer();
		if (isFlying) {

			if (rank.flyTime == -1)
				player.sendMessage(ChatColor.YELLOW
						+ "[fly] Flying, unendless time left.");
			else
				player.sendMessage(ChatColor.YELLOW + "[fly] Flying, "
						+ airTimeLeft + " seconds left.");
		} else if (coolDownLeft > 0) {
			player.sendMessage(ChatColor.YELLOW + "[fly] In cooldown, "
					+ coolDownLeft + " seconds left.");
		} else {
			if (rank.flyTime == -1)
				player.sendMessage(ChatColor.YELLOW
						+ "[fly] Ready to fly, unendless time left.");
			else
				player.sendMessage(ChatColor.YELLOW + "[fly] Ready to fly, "
						+ airTimeLeft + " seconds left.");
		}
		player.sendMessage(ChatColor.YELLOW + "[fly] Your rank is " + rank.name
				+ ".");
	}

	/**
	 * Search for the right permissino by the right player. If NULL is returned,
	 * somthing is missconfigured in the config file. This happens if there is
	 * not 'op' or 'default' rank is definded.
	 * 
	 * @param player
	 *            the player to search the rank for.
	 * @return the right rank that the player has.
	 */
	private static Rank getPlayerRank(Player player) {
		if (player.isOp())
			return FlyTimer.ranks.get("op");
		for (String name : FlyTimer.ranks.keySet()) {
			if (player.hasPermission("flight." + name))
				return FlyTimer.ranks.get(name);
		}

		return FlyTimer.ranks.get("default");
	}

	/**
	 * name sais it all, toggles flying. If you are flying you will stop flying,
	 * and if you are on the gorund you will fly.
	 */
	public void toggleFlying() {
		if (isFlying)
			stopFlying();
		else
			startFlying();
	}

	/**
	 * Resets all the times (the cooldown and the fly-time).
	 */
	public void resetTimers() {
		this.airTimeLeft = rank.flyTime;
		this.coolDownLeft = rank.cooldown;
	}

	/**
	 * @return the right player with this PlayerState.
	 */
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
}
